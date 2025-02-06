package com.example.livebettingapp.controller;

import com.example.livebettingapp.entity.Coupon;
import com.example.livebettingapp.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/coupons")
@RequiredArgsConstructor
public class CouponController {
    private final CouponService couponService;

    @PostMapping
    public String placeCoupon(@RequestBody List<Coupon> coupons) {
        return couponService.placeCouponsWithTimeout(coupons);
    }

}
