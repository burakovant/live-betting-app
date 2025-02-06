package com.example.livebettingapp.repository;

import com.example.livebettingapp.entity.Odds;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OddsRepository extends JpaRepository<Odds, Long> {
    List<Odds> findAllByOrderByUpdatedAtAsc();

    Optional<Odds> findByMatchId(Long id);
}
