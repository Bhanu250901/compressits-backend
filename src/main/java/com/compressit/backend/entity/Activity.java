package com.compressit.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "activities")
public class Activity {

    @Id
    @GeneratedValue(
            strategy =
                    GenerationType.IDENTITY
    )
    private Long id;

    private String userEmail;

    private String toolName;

    private Integer coins;

    private String activityDate;

    public Activity() {
    }

    public Long getId() {
        return id;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(
            String userEmail
    ) {
        this.userEmail =
                userEmail;
    }

    public String getToolName() {
        return toolName;
    }

    public void setToolName(
            String toolName
    ) {
        this.toolName =
                toolName;
    }

    public Integer getCoins() {
        return coins;
    }

    public void setCoins(
            Integer coins
    ) {
        this.coins = coins;
    }

    public String getActivityDate() {
        return activityDate;
    }

    public void setActivityDate(
            String activityDate
    ) {
        this.activityDate =
                activityDate;
    }
}