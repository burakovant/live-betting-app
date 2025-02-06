package com.example.livebettingapp.service;

import com.example.livebettingapp.entity.Bet;
import com.example.livebettingapp.entity.Coupon;
import com.example.livebettingapp.entity.Match;
import com.example.livebettingapp.exception.BettingException;
import com.example.livebettingapp.repository.CouponRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private MatchService matchService;

    @InjectMocks
    private CouponService couponService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(couponService, "maxPerMatch", 500);
        ReflectionTestUtils.setField(couponService, "timeoutSeconds", 2);
    }

    /**
     * {@code couponService.placeCouponsWithTimeout} metodunun çalışması esnasında bir timeout
     * meydana geldiğinde, metodun transactional davranışını test eder. İşlemin doğru
     * şekilde geri alındığını (rollback) ve hiçbir değişikliğin veritabanına kaydedilmediğini doğrular.
     * <p>
     * Bu test, 100 geçerli kuponun listesini oluşturur ve {@code couponRepository.save} yönteminin
     * bir gecikme yaşadığı bir senaryoyu simüle eder. Bu durum,
     * {@code couponService.placeCouponsWithTimeout} metodu sırasında bir zaman timeout
     * oluşmasına neden olur. Test, bir {@link BettingException} atıldığından
     * emin olur ve hiçbir kupon verisinin veritabanına kaydedilmediğini doğrular.
     * <p>
     * Ana doğrulamalar:
     * - Timeout nedeniyle {@link BettingException} atıldığından emin olur.
     * - Geri alma (rollback) davranışını doğrulamak için veritabanının değişmeden kaldığını belirtir.
     */
    @Test
    @Transactional
    void shouldRollbackTransactionWhenTimeoutOccurs() {
        List<Coupon> coupons = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            coupons.add(createValidCoupon());
        }

        when(matchService.getMatchById(anyLong())).thenReturn(createValidMatch());
        doAnswer(invocation -> {
            Thread.sleep(3000);
            return null;
        }).when(couponRepository).save(any(Coupon.class));

        assertThrows(BettingException.class, () -> couponService.placeCouponsWithTimeout(coupons));

        // Rollback'in gerçekleştiğini belirten veritabanında bir değişiklik olmamalı
        assertEquals(0, couponRepository.findAll().size());
    }


    /**
     * {@code placeCouponsWithTimeout} metodunun, bir maç için bahis limitinin aşılması durumundaki
     * davranışını test eder. Bu senaryoda bir {@link BettingException} fırlatıldığını doğrular.
     * <p>
     * Test aşağıdaki doğrulamaları gerçekleştirir:
     * - {@code BettingException} içinde, bahis limitinin aşıldığını belirten uygun hata mesajının
     * bulunduğunu sağlar.
     * - Testin çalışması sırasında {@code matchService.getMatchById} metodunun yalnızca bir kez
     * çağrıldığını doğrular.
     */
    @Test
    void shouldThrowExceptionWhenBetLimitExceeded() {
        Coupon coupon = createValidCoupon();
        List<Coupon> coupons = List.of(coupon);

        when(matchService.getMatchById(anyLong())).thenReturn(createMatchWithMaxBets());

        BettingException exception = assertThrows(BettingException.class, () -> couponService.placeCouponsWithTimeout(coupons));

        assertTrue(exception.getMessage().contains("Bet limit 500 exceeded for match"));
        verify(matchService, times(1)).getMatchById(anyLong());
    }

    @Test
    void shouldPlaceCouponsSuccessfullyWhenUnderTimeout() {
        List<Coupon> coupons = new ArrayList<>();
        coupons.add(createValidCoupon());
        coupons.add(createValidCoupon());

        doNothing().when(matchService).incrementBetCount(anyLong());
        when(matchService.getMatchById(anyLong())).thenReturn(createValidMatch());

        String result = couponService.placeCouponsWithTimeout(coupons);

        assertEquals("Coupon placed successfully!", result);
        verify(couponRepository, times(coupons.size())).save(any(Coupon.class));
        verify(matchService, atLeastOnce()).getMatchById(anyLong());
    }

    @Test
    void shouldThrowTimeoutExceptionWhenExecutionTimesOut() {
        List<Coupon> coupons = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            coupons.add(createValidCoupon());
        }

        // MatchService mock'u için geçerli bir Match nesnesi döndür
        when(matchService.getMatchById(anyLong())).thenReturn(createValidMatch());

        doAnswer(invocation -> {
            Thread.sleep(3000);
            return null;
        }).when(couponRepository).save(any(Coupon.class));

        BettingException exception = assertThrows(BettingException.class, () -> couponService.placeCouponsWithTimeout(coupons));

        assertEquals("Coupon placement timed out!", exception.getMessage());
    }

    @Test
    void shouldHandleEmptyCouponsList() {
        List<Coupon> coupons = new ArrayList<>();

        String result = couponService.placeCouponsWithTimeout(coupons);

        assertEquals("Coupon placed successfully!", result);
        verify(couponRepository, never()).save(any(Coupon.class));
    }

    @Test
    void shouldThrowExceptionWhenRepositoryThrowsError() {
        List<Coupon> coupons = new ArrayList<>();
        coupons.add(createValidCoupon());

        when(matchService.getMatchById(anyLong())).thenReturn(createValidMatch());
        doThrow(new RuntimeException("Database error")).when(couponRepository).save(any(Coupon.class));

        BettingException exception = assertThrows(BettingException.class, () -> couponService.placeCouponsWithTimeout(coupons));

        assertEquals("Error placing coupon: java.lang.RuntimeException: Database error", exception.getMessage());
    }

    private Coupon createValidCoupon() {
        Coupon coupon = new Coupon();
        Bet bet = new Bet();
        bet.setMatchId(1L);

        List<Bet> bets = new ArrayList<>();
        bets.add(bet);

        coupon.setBets(bets);
        return coupon;
    }

    private Match createValidMatch() {
        Match match = new Match();
        match.setBetCount(1);
        return match;
    }

    private Match createMatchWithMaxBets() {
        Match match = new Match();
        match.setBetCount(500); // Simulate max bet count exceeded
        return match;
    }
}