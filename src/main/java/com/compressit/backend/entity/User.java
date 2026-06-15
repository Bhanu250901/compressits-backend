package com.compressit.backend.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(
            strategy =
                    GenerationType.IDENTITY
    )
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;



    @JsonIgnore
    private String password;

    // WELCOME BONUS
    private Integer coins = 100;

    // REFERRAL CODE
    private String referralCode;

    // REFERRED BY
    private String referredBy;

    // DAILY REWARD
    private String lastClaimDate;

    private Integer dailyUsage = 0;

    private String usageDate;

    private Integer imageCompressCount = 0;

    private Integer pdfCompressCount = 0;

    private Integer imageConvertCount = 0;

    private Integer pdfConvertCount = 0;

    private boolean imageCompressClaimed = false;

    private boolean pdfCompressClaimed = false;

    private boolean imageConvertClaimed = false;

    private boolean pdfConvertClaimed = false;

    private boolean bonusRedeemed = false;

    private boolean referralClaimed = false;

    public User() {
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(
            String name
    ) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(
            String email
    ) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(
            String password
    ) {
        this.password = password;
    }

    public Integer getCoins() {
        return coins;
    }

    public void setCoins(
            Integer coins
    ) {
        this.coins = coins;
    }

    public String getReferralCode() {
        return referralCode;
    }

    public void setReferralCode(
            String referralCode
    ) {
        this.referralCode =
                referralCode;
    }

    public String getReferredBy() {
        return referredBy;
    }

    public void setReferredBy(
            String referredBy
    ) {
        this.referredBy =
                referredBy;
    }

    public String getLastClaimDate() {
        return lastClaimDate;
    }

    public void setLastClaimDate(
            String lastClaimDate
    ) {
        this.lastClaimDate =
                lastClaimDate;
    }
    public Integer getDailyUsage() {
        return dailyUsage;
    }

    public void setDailyUsage(
            Integer dailyUsage
    ) {
        this.dailyUsage =
                dailyUsage;
    }

    public String getUsageDate() {
        return usageDate;
    }

    public void setUsageDate(
            String usageDate
    ) {
        this.usageDate =
                usageDate;
    }

    public boolean isBonusRedeemed() {
        return bonusRedeemed;
    }

    public void setBonusRedeemed(
            boolean bonusRedeemed
    ) {
        this.bonusRedeemed =
                bonusRedeemed;
    }

    public Integer getImageCompressCount() {
        return imageCompressCount;
    }

    public void setImageCompressCount(Integer imageCompressCount) {
        this.imageCompressCount = imageCompressCount;
    }

    public Integer getPdfCompressCount() {
        return pdfCompressCount;
    }

    public void setPdfCompressCount(Integer pdfCompressCount) {
        this.pdfCompressCount = pdfCompressCount;
    }

    public Integer getImageConvertCount() {
        return imageConvertCount;
    }

    public void setImageConvertCount(Integer imageConvertCount) {
        this.imageConvertCount = imageConvertCount;
    }

    public Integer getPdfConvertCount() {
        return pdfConvertCount;
    }

    public void setPdfConvertCount(Integer pdfConvertCount) {
        this.pdfConvertCount = pdfConvertCount;
    }

    public boolean isImageCompressClaimed() {
        return imageCompressClaimed;
    }

    public void setImageCompressClaimed(boolean imageCompressClaimed) {
        this.imageCompressClaimed = imageCompressClaimed;
    }

    public boolean isPdfCompressClaimed() {
        return pdfCompressClaimed;
    }

    public void setPdfCompressClaimed(boolean pdfCompressClaimed) {
        this.pdfCompressClaimed = pdfCompressClaimed;
    }

    public boolean isImageConvertClaimed() {
        return imageConvertClaimed;
    }

    public void setImageConvertClaimed(boolean imageConvertClaimed) {
        this.imageConvertClaimed = imageConvertClaimed;
    }

    public boolean isPdfConvertClaimed() {
        return pdfConvertClaimed;
    }

    public void setPdfConvertClaimed(boolean pdfConvertClaimed) {
        this.pdfConvertClaimed = pdfConvertClaimed;
    }

    public boolean isReferralClaimed() {
        return referralClaimed;
    }

    public void setReferralClaimed(
            boolean referralClaimed
    ) {
        this.referralClaimed =
                referralClaimed;
    }
}

