package com.example.storeproject.controller;

import com.example.storeproject.dto.AccountDto;
import com.example.storeproject.entity.Account;
import com.example.storeproject.model.*;
import com.example.storeproject.security.TokenProvider;
import com.example.storeproject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final TokenProvider tokenProvider;

    // 사용자 회원 가입
    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody SignUpForm form) {
        return ResponseEntity.ok(userService.signUp(form));
    }

    // 사용자 로그인
    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody SignInForm form) {
        AccountDto accountDto = userService.signIn(form);
        String token = tokenProvider.createToken(accountDto.getEmail(), accountDto.getAccountType());
        return ResponseEntity.ok(token);
    }

    // 사용자 계정 탈퇴
    @DeleteMapping("/account/{id}")
    public ResponseEntity<?> deleteUserAccount(@AuthenticationPrincipal Account account) {
        userService.deleteUserAccount(account);
        return ResponseEntity.ok().build();
    }

    // 사용자 상점 검색
    @GetMapping("/search")
    public ResponseEntity<?> searchStore(@RequestBody SearchForm form) {
        return ResponseEntity.ok(userService.searchStore(form));
    }

    // 사용자 이름순 상점 조회
    @GetMapping("/search/by-name/{page}")
    public ResponseEntity<?> storeListByName(@PathVariable int page) {
        return ResponseEntity.ok(userService.getStoreListByName(page));
    }

    // 사용자 별점순 상점 조회
    @GetMapping("/search/by-stars/{page}")
    public ResponseEntity<?> storeListByStars(@PathVariable int page) {
        return ResponseEntity.ok(userService.getStoreListByStars(page));
    }


    // 사용자 상점 예약
    @PostMapping("/user/reserve/{storeId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> reserveStore(@AuthenticationPrincipal Account account, @PathVariable long storeId, @RequestBody ReserveForm form) {
        return ResponseEntity.ok(userService.reserveStore(account, storeId, form));
    }

    // 사용자 예약 확인
    @GetMapping("/user/reserve/{reservationId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> seeReservation(@AuthenticationPrincipal Account account, @PathVariable long reservationId) {
        return ResponseEntity.ok(userService.seeReservation(account, reservationId));
    }

    // 사용자 예약 취소
    @DeleteMapping("/user/reserve/{reservationId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> cancelReservation(@AuthenticationPrincipal Account account, @PathVariable long reservationId) {
        return ResponseEntity.ok(userService.cancelReservation(account, reservationId));
    }

    // 사용자 리뷰 작성 및 수정
    @PutMapping("/user/reserve/{reservationId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> writeReview(@AuthenticationPrincipal Account account, @PathVariable long reservationId, @RequestBody ReviewForm form) {
        return ResponseEntity.ok(userService.writeReview(account, reservationId, form));
    }

}
