package com.compressit.backend.controller;

import com.compressit.backend.dto.CouponRequest;
import com.compressit.backend.entity.User;
import com.compressit.backend.repository.UserRepository;

import com.compressit.backend.withdraw.WithdrawRequest;
import com.compressit.backend.withdraw.WithdrawRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.compressit.backend.entity.CoinHistory;
import com.compressit.backend.repository.CoinHistoryRepository;

import java.util.List;
import java.util.Random;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    @Autowired
    private CoinHistoryRepository
            coinHistoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WithdrawRepository withdrawRepository;

    // GENERATE REFERRAL CODE
    private String generateReferralCode() {

        Random random =
                new Random();

        int number =
                10000 +
                        random.nextInt(90000);

        return "REF" + number;
    }

    // REGISTER
    @PostMapping("/register")
    public String registerUser(
            @RequestBody User user
    ) {
        userRepository.save(user);        System.out.println("REGISTER API CALLED: " + user.getEmail());
        // EMAIL EXISTS
        if (
                userRepository
                        .findByEmail(
                                user.getEmail()
                        )
                        .isPresent()
        ) {

            return "Email already exists!";
        }

        if (
                userRepository.existsByName(
                        user.getName()
                )
        ) {

            return "Username already taken. Try another name.";
        }

// GENERATE REFERRAL CODE
        user.setReferralCode(
                generateReferralCode()
        );



        // DEFAULT BONUS
        user.setCoins(100);

        // REFERRAL BONUS
        if (
                user.getReferredBy()
                        != null &&
                        !user.getReferredBy()
                                .isEmpty()
        ) {

            // FIND INVITER
            User inviter =
                    userRepository
                            .findAll()
                            .stream()
                            .filter(u ->
                                    user
                                            .getReferredBy()
                                            .equals(
                                                    u.getReferralCode()
                                            )
                            )
                            .findFirst()
                            .orElse(null);

            if (inviter != null) {

                // BONUS TO INVITER
                inviter.setCoins(
                        inviter.getCoins() + 500
                );

                userRepository.save(inviter);

                // BONUS TO NEW USER
                user.setCoins(
                        user.getCoins() + 500
                );
                user.setReferralClaimed(true);
            }

        }
        // SAVE USER
        userRepository.save(user);

        return "User registered successfully!";
    }

    // LOGIN
    @PostMapping("/login")
    public String loginUser(
            @RequestBody User user
    ) {

        User existingUser =
                userRepository
                        .findByEmail(
                                user.getEmail()
                        )
                        .orElse(null);

        if (existingUser == null) {

            return "User not found!";
        }

        if (
                !existingUser
                        .getPassword()
                        .equals(
                                user.getPassword()
                        )
        ) {

            return "Invalid password!";
        }

        return "Login successful!";
    }

    // GET USER
    @GetMapping("/user/{email}")
    public User getUserByEmail(
            @PathVariable String email
    ) {

        return userRepository
                .findByEmail(email)
                .orElse(null);
    }

    // GET ALL USERS
    @GetMapping("/all-users")
    public List<User> getAllUsers() {

        return userRepository.findAll();
    }

    @PostMapping("/add-coins/{email}")
    public String addCoins(
            @PathVariable String email
    ) {

        User user =
                userRepository
                        .findByEmail(email)
                        .orElse(null);

        if (user == null) {

            return "User not found!";
        }

        String today =
                LocalDate.now()
                        .toString();

        // RESET DAILY USAGE
        if (
                user.getUsageDate() == null ||
                        !today.equals(
                                user.getUsageDate()
                        )
        ) {

            user.setDailyUsage(0);

            user.setUsageDate(today);
        }

        // DAILY REWARD LIMIT
        if (
                user.getDailyUsage() >= 4
        ) {

            return "Daily reward limit reached!";
        }

        // ADD REWARD
        user.setCoins(
                user.getCoins() + 50
        );

        // UPDATE USAGE
        user.setDailyUsage(
                user.getDailyUsage() + 1
        );

        userRepository.save(user);

        return "+50 Coins Added!";
    }
    // CREATE WITHDRAW REQUEST
    @PostMapping("/withdraw")
    public String createWithdrawRequest(
            @RequestBody WithdrawRequest request
    ) {

        User user =
                userRepository
                        .findByEmail(
                                request.getUserEmail()
                        )
                        .orElse(null);

        if (user == null) {

            return "User not found!";
        }
        if (
                request.getRewardType().equals("10000")
                        &&
                        withdrawRepository.existsByUserEmailAndRewardType(
                                request.getUserEmail(),
                                "10000")
        ){

            return "₹10 welcome reward already claimed!"; }

        // CHECK COINS
        if (
                user.getCoins() <
                        request.getCoins()
        ) {

            return "Not enough coins!";
        }

        // DEDUCT COINS
        user.setCoins(
                user.getCoins() -
                        request.getCoins()
        );

        userRepository.save(user);

        // SAVE REQUEST
        request.setStatus("PENDING");

        if (
                request.getRewardType().equals("10000")
        ) {

            user.setBonusRedeemed(true);

            userRepository.save(user);
        }



        withdrawRepository.save(request);

        return "Withdraw request submitted!";

    }

    // GET ALL WITHDRAW REQUESTS
    @GetMapping("/withdraw-requests")
    public List<WithdrawRequest>
    getAllWithdrawRequests() {

        return withdrawRepository.findAll();
    }

    // APPROVE WITHDRAW
    @PostMapping("/approve-withdraw/{id}/{couponCode}")
    public String approveWithdraw(
            @PathVariable Long id,
            @PathVariable String couponCode
    ) {

        WithdrawRequest request =
                withdrawRepository.findById(id)
                        .orElse(null);

        if (request == null) {
            return "Request not found!";
        }

        request.setStatus("APPROVED");
        request.setCouponCode(couponCode);

        withdrawRepository.save(request);

        return "Coupon sent successfully!";
    }

    // REJECT WITHDRAW
    @PostMapping("/reject-withdraw/{id}")
    public String rejectWithdraw(
            @PathVariable Long id
    ) {

        WithdrawRequest request =
                withdrawRepository.findById(id)
                        .orElse(null);

        if (request == null) {
            return "Request not found!";
        }

        User user =
                userRepository.findByEmail(
                        request.getUserEmail()
                ).orElse(null);

        if (user != null) {

            user.setCoins(
                    user.getCoins()
                            + request.getCoins()
            );

            userRepository.save(user);
        }

        request.setStatus("REJECTED");

        withdrawRepository.save(request);

        return "Request rejected and coins returned!";
    }
    // DAILY REWARD
    @PostMapping(
            "/daily-reward/{email}"
    )
    public String claimDailyReward(
            @PathVariable String email
    ) {

        User user =
                userRepository
                        .findByEmail(email)
                        .orElse(null);

        if (user == null) {

            return "User not found!";
        }

        // TODAY DATE
        String today =
                LocalDate.now()
                        .toString();

        // ALREADY CLAIMED
        if (
                today.equals(
                        user.getLastClaimDate()
                )
        ) {

            return "Daily reward already claimed!";
        }

        // ADD 10 COINS
        user.setCoins(
                user.getCoins() + 50
        );

        CoinHistory history =
                new CoinHistory();

        history.setEmail(
                user.getEmail()
        );

        history.setActivity(
                "Daily Reward"
        );

        history.setCoins(50);

        CoinHistory saved =
                coinHistoryRepository.save(
                        history
                );

        System.out.println(
                "COIN HISTORY SAVED ID = " +
                        saved.getId()
        );

        // SAVE CLAIM DATE
        user.setLastClaimDate(
                today
        );

        userRepository.save(user);

        return "Daily reward claimed! +50 Coins";
    }
    // LEADERBOARD
    @GetMapping("/leaderboard")
    public List<User> leaderboard() {

        return userRepository
                .findAll()
                .stream()
                .sorted(
                        (a, b) ->
                                b.getCoins()
                                        .compareTo(
                                                a.getCoins()
                                        )
                )
                .toList();
    }
    @PostMapping("/image-compress/{email}")
    public String imageCompress(
            @PathVariable String email
    ) {

        User user =
                userRepository
                        .findByEmail(email)
                        .orElse(null);

        if (user == null) {
            return "User not found!";
        }

        String today =
                LocalDate.now()
                        .toString();

        if (
                user.getUsageDate() == null ||
                        !today.equals(
                                user.getUsageDate()
                        )
        ) {

            user.setUsageDate(today);

            user.setImageCompressCount(0);

            user.setImageCompressClaimed(false);

            userRepository.save(user);
        }


        // ALREADY CLAIMED
        if (
                user.isImageCompressClaimed()
        ) {

            return "ALREADY_COMPLETED";
        }

// INCREASE ONLY UNTIL 5
        if (
                user.getImageCompressCount() < 5
        ) {

            user.setImageCompressCount(
                    user.getImageCompressCount() + 1
            );
        }

        userRepository.save(user);

// SHOW CLAIM BUTTON
        if (
                user.getImageCompressCount() == 5
        ) {

            return "CLAIM_AVAILABLE";
        }

// SHOW PROGRESS
        return user.getImageCompressCount() + "/5";   }

    @PostMapping("/claim-image-compress/{email}")
    public String claimImageCompress(
            @PathVariable String email
    ) {

        User user =
                userRepository
                        .findByEmail(email)
                        .orElse(null);


        if (user == null) {
            return "User not found!";
        }

        if (
                user.getImageCompressCount() < 5
        ) {

            return "Complete 5 compressions first!";
        }

        if (
                user.isImageCompressClaimed()
        ) {

            return "Already claimed!";
        }

        user.setCoins(
                user.getCoins() + 50
        );

        CoinHistory history =
                new CoinHistory();

        history.setEmail(
                user.getEmail()
        );

        history.setActivity(
                "Image Compress Reward"
        );

        history.setCoins(50);

        coinHistoryRepository.save(
                history
        );

        user.setImageCompressClaimed(true);

        userRepository.save(user);

        return "+50 Coins Added!";
    }

    @PostMapping("/pdf-compress/{email}")
    public String pdfCompress(
            @PathVariable String email
    ) {

        User user =
                userRepository
                        .findByEmail(email)
                        .orElse(null);


        if (user == null) {
            return "User not found!";
        }

        String today =
                LocalDate.now()
                        .toString();

        if (
                user.getUsageDate() == null ||
                        !today.equals(
                                user.getUsageDate()
                        )
        ) {

            user.setUsageDate(today);

            user.setPdfCompressCount(0);

            user.setPdfCompressClaimed(false);

            userRepository.save(user);
        }


        if (
                user.isPdfCompressClaimed()
        ) {

            return "ALREADY_COMPLETED";
        }

        if (
                user.getPdfCompressCount() < 5
        ) {

            user.setPdfCompressCount(
                    user.getPdfCompressCount() + 1
            );
        }

        userRepository.save(user);

        if (
                user.getPdfCompressCount() >= 5
        ) {

            return "CLAIM_AVAILABLE";
        }

        return user.getPdfCompressCount() + "/5";
    }

    @PostMapping("/claim-pdf-compress/{email}")
    public String claimPdfCompress(
            @PathVariable String email
    ) {

        User user =
                userRepository
                        .findByEmail(email)
                        .orElse(null);

        if (user == null) {
            return "User not found!";
        }

        if (
                user.getPdfCompressCount() < 5
        ) {

            return "Complete 5 PDF compressions first!";
        }

        if (
                user.isPdfCompressClaimed()
        ) {

            return "Already claimed!";
        }

        user.setCoins(
                user.getCoins() + 50
        );

        CoinHistory history =
                new CoinHistory();

        history.setEmail(
                user.getEmail()
        );

        history.setActivity(
                "PDF Compress Reward"
        );

        history.setCoins(50);

        coinHistoryRepository.save(
                history
        );

        user.setPdfCompressClaimed(true);

        userRepository.save(user);

        return "+50 Coins Added!";
    }

    @PostMapping("/image-convert/{email}")
    public String imageConvert(
            @PathVariable String email
    ) {

        User user =
                userRepository
                        .findByEmail(email)
                        .orElse(null);

        if (user == null) {
            return "User not found!";
        }

        String today =
                LocalDate.now()
                        .toString();

        if (
                user.getUsageDate() == null ||
                        !today.equals(
                                user.getUsageDate()
                        )
        ) {

            user.setUsageDate(today);

            user.setImageConvertCount(0);

            user.setImageConvertClaimed(false);

            userRepository.save(user);
        }

        if (
                user.isImageConvertClaimed()
        ) {

            return "ALREADY_COMPLETED";
        }

        if (
                user.getImageConvertCount() < 5
        ) {

            user.setImageConvertCount(
                    user.getImageConvertCount() + 1
            );
        }

        userRepository.save(user);

        if (
                user.getImageConvertCount() >= 5
        ) {

            return "CLAIM_AVAILABLE";
        }

        return user.getImageConvertCount() + "/5";
    }

    @PostMapping("/claim-image-convert/{email}")
    public String claimImageConvert(
            @PathVariable String email
    ) {

        User user =
                userRepository
                        .findByEmail(email)
                        .orElse(null);

        if (user == null) {
            return "User not found!";
        }

        if (
                user.getImageConvertCount() < 5
        ) {

            return "Complete 5 conversions first!";
        }

        if (
                user.isImageConvertClaimed()
        ) {

            return "Already claimed!";
        }

        user.setCoins(
                user.getCoins() + 50
        );

        CoinHistory history =
                new CoinHistory();

        history.setEmail(
                user.getEmail()
        );

        history.setActivity(
                "Image Convert Reward"
        );

        history.setCoins(50);

        coinHistoryRepository.save(
                history
        );

        user.setImageConvertClaimed(true);

        userRepository.save(user);

        return "+50 Coins Added!";
    }

    @PostMapping("/pdf-convert/{email}")
    public String pdfConvert(
            @PathVariable String email
    ) {

        User user =
                userRepository
                        .findByEmail(email)
                        .orElse(null);

        if (user == null) {
            return "User not found!";
        }

        String today =
                LocalDate.now()
                        .toString();

        if (
                user.getUsageDate() == null ||
                        !today.equals(
                                user.getUsageDate()
                        )
        ) {

            user.setUsageDate(today);

            user.setPdfConvertCount(0);

            user.setPdfConvertClaimed(false);

            userRepository.save(user);
        }

        if (
                user.isPdfConvertClaimed()
        ) {

            return "ALREADY_COMPLETED";
        }

        if (
                user.getPdfConvertCount() < 5
        ) {

            user.setPdfConvertCount(
                    user.getPdfConvertCount() + 1
            );
        }

        userRepository.save(user);

        if (
                user.getPdfConvertCount() >= 5
        ) {

            return "CLAIM_AVAILABLE";
        }

        return user.getPdfConvertCount() + "/5";
    }

    @PostMapping("/claim-pdf-convert/{email}")
    public String claimPdfConvert(
            @PathVariable String email
    ) {

        User user =
                userRepository
                        .findByEmail(email)
                        .orElse(null);

        if (user == null) {
            return "User not found!";
        }

        if (
                user.getPdfConvertCount() < 5
        ) {

            return "Complete 5 conversions first!";
        }

        if (
                user.isPdfConvertClaimed()
        ) {

            return "Already claimed!";
        }

        user.setCoins(
                user.getCoins() + 50
        );

        CoinHistory history =
                new CoinHistory();

        history.setEmail(
                user.getEmail()
        );

        history.setActivity(
                "PDF Convert Reward"
        );

        history.setCoins(50);

        coinHistoryRepository.save(
                history
        );

        user.setPdfConvertClaimed(true);

        userRepository.save(user);

        return "+50 Coins Added!";
    }

    @PostMapping(
            "/apply-referral/{email}/{referralCode}"
    )
    public String applyReferral(
            @PathVariable String email,
            @PathVariable String referralCode
    ) {

        User user =
                userRepository
                        .findByEmail(email)
                        .orElse(null);

        if (user == null) {
            return "User not found!";
        }

        if (
                user.isReferralClaimed()
        ) {

            return "Referral already used!";
        }

        if (
                user.getReferralCode()
                        .equals(referralCode)
        ) {

            return "You cannot use your own referral code!";
        }

        User inviter =
                userRepository
                        .findAll()
                        .stream()
                        .filter(u ->
                                referralCode.equals(
                                        u.getReferralCode()
                                )
                        )
                        .findFirst()
                        .orElse(null);

        if (inviter == null) {

            return "Invalid referral code!";
        }

        inviter.setCoins(
                inviter.getCoins() + 500
        );

        user.setCoins(
                user.getCoins() + 500
        );

        CoinHistory history =
                new CoinHistory();

        history.setEmail(
                user.getEmail()
        );

        history.setActivity(
                "Referral Bonus"
        );

        history.setCoins(500);

        coinHistoryRepository.save(
                history
        );

        user.setReferredBy(
                referralCode
        );

        user.setReferralClaimed(
                true
        );

        userRepository.save(inviter);

        userRepository.save(user);

        return "+500 Coins Added!";
    }

    @GetMapping("/coin-history/{email}")
    public List<CoinHistory> getCoinHistory(
            @PathVariable String email
    ) {

        return coinHistoryRepository
                .findByEmail(email);
    }
    @PostMapping("/google-login")
    public User googleLogin(
            @RequestBody User request
    ) {

        User existingUser =
                userRepository
                        .findByEmail(
                                request.getEmail()
                        )
                        .orElse(null);

        if (existingUser != null) {
            return existingUser;
        }

        User user = new User();

        user.setName(
                request.getName()
        );

        user.setEmail(
                request.getEmail()
        );

        user.setCoins(100);

        user.setReferralCode(
                generateReferralCode()
        );

        return userRepository.save(user);
    }
}