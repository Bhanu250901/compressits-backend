package com.compressit.backend.withdraw;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WithdrawRepository
        extends JpaRepository<
        WithdrawRequest,
        Long
        > {
    boolean existsByUserEmailAndRewardType(
            String userEmail,
            String rewardType
    );
}