package com.compressit.backend.repository;

import com.compressit.backend.entity.Activity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepository
        extends JpaRepository<Activity, Long> {
}