package com.example.livebettingapp.service;

import com.example.livebettingapp.entity.Bet;
import com.example.livebettingapp.entity.Coupon;
import com.example.livebettingapp.exception.BettingException;
import com.example.livebettingapp.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
public class CouponService {
    private final CouponRepository couponRepository;
    private final MatchService matchService;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    @Value("${betting.coupon.timeout:2}")
    private long timeoutSeconds = 2;

    @Value("${betting.coupons.max-per-match:500}")
    private int maxPerMatch;

    /**
     * Belirtilen bir zaman aşımı süresiyle bir liste kupon yerleştirir. İşlem, tanımlanan
     * zaman aşımı süresinden daha uzun sürerse, iptal edilir ve bir BettingException fırlatılır.
     * <p>
     * Kupon sayısı 5'i aşarsa zaman kazanmak için kuponlar paralel olarak işlenir; aksi takdirde
     * sıralı bir şekilde işlenir. Her bir kupon, ilişkili maç için bahis sayısı limitinin aşılmadığından
     * emin olduktan sonra depoya kaydedilir.
     * <p>
     * Bu yöntem transactionaldir ve herhangi bir istisna durumunda yapılan değişiklikler geri alınır.
     *
     * @param coupons Yerleştirilecek kuponların listesi
     * @return Kuponların başarıyla yerleştirildiğini belirten bir mesaj
     * @throws BettingException İşlem zaman aşımına uğrarsa veya kupon yerleştirme sırasında herhangi bir hata oluşursa
     */
    @Transactional(rollbackFor = Throwable.class)
    public String placeCouponsWithTimeout(List<Coupon> coupons) {
        Future<String> future = executorService.submit(() -> {
            if (coupons.size() > 5) {
                coupons.parallelStream().forEach(this::placeCoupon);
            } else {
                for (Coupon coupon : coupons) {
                    placeCoupon(coupon);
                }
            }
            return "Coupon placed successfully!";
        });

        try {
            return future.get(timeoutSeconds, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
            throw new BettingException("Coupon placement timed out!");
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            throw new BettingException("Error placing coupon: " + e.getMessage());
        }
    }

    private void placeCoupon(Coupon coupon) {
        for (Bet bet : coupon.getBets()) {
            if (matchService.getMatchById(bet.getMatchId()).getBetCount() >= maxPerMatch) {
                throw new BettingException("Bet limit " + maxPerMatch + " exceeded for match " + bet.getMatchId());
            }
            matchService.incrementBetCount(bet.getMatchId());
        }
        couponRepository.save(coupon);
    }
}
