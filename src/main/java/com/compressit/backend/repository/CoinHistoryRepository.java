package com.compressit.backend.repository;

import com.compressit.backend.entity.CoinHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CoinHistoryRepository
        extends JpaRepository<CoinHistory, Long> {

    List<CoinHistory> findByEmail(
            String email
    );
}