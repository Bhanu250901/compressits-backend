package com.compressit.backend.withdraw;

import jakarta.persistence.*;

@Entity
@Table(name = "withdraw_requests")
public class WithdrawRequest {

    @Id
    @GeneratedValue(
            strategy =
                    GenerationType.IDENTITY
    )
    private Long id;

    private String userEmail;

    private Integer coins;

    private String rewardType;

    private String paymentDetails;

    private String status = "PENDING";

    private Integer rewardAmount;

    private String couponCode = "";

    public WithdrawRequest() {
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

    public Integer getCoins() {
        return coins;
    }

    public void setCoins(
            Integer coins
    ) {
        this.coins = coins;
    }

    public String getRewardType() {
        return rewardType;
    }

    public void setRewardType(
            String rewardType
    ) {
        this.rewardType =
                rewardType;
    }

    public String getPaymentDetails() {
        return paymentDetails;
    }

    public void setPaymentDetails(
            String paymentDetails
    ) {
        this.paymentDetails =
                paymentDetails;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(
            String status
    ) {
        this.status = status;
    }

    public Integer getRewardAmount() {
        return rewardAmount;
    }

    public void setRewardAmount(
            Integer rewardAmount
    ) {
        this.rewardAmount =
                rewardAmount;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(
            String couponCode
    ) {
        this.couponCode =
                couponCode;
    }
}