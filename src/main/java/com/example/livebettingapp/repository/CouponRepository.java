package com.example.livebettingapp.repository;

import com.example.livebettingapp.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
}

