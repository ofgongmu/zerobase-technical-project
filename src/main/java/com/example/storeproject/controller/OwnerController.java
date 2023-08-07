package com.example.storeproject.controller;

import com.example.storeproject.constants.ReservationState;
import com.example.storeproject.dto.AccountDto;
import com.example.storeproject.entity.Account;
import com.example.storeproject.model.AddStoreForm;
import com.example.storeproject.model.EditStoreForm;
import com.example.storeproject.model.SignInForm;
import com.example.storeproject.model.SignUpForm;
import com.example.storeproject.security.TokenProvider;
import com.example.storeproject.service.OwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class OwnerController {
    private final OwnerService ownerService;
    private final TokenProvider tokenProvider;

    // 점주 회원가입
    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody SignUpForm form) {
        return ResponseEntity.ok(ownerService.signUp(form));
    }

    // 점주 로그인 및 인증 토큰 발급
    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody SignInForm form) {
        AccountDto accountDto = ownerService.signIn(form);
        String token = tokenProvider.createToken(accountDto.getEmail(), accountDto.getAccountType());
        return ResponseEntity.ok(token);
    }

    // 점주 회원 탈퇴
    @DeleteMapping("/account/{id}")
    public ResponseEntity<?> deleteOwnerAccount(@AuthenticationPrincipal Account account) {
        ownerService.deleteOwnerAccount(account);
        return ResponseEntity.ok().build();
    }

    // 점주 상점 추가
    @PostMapping("/store/add")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<?> addStore(@AuthenticationPrincipal Account account, @RequestBody AddStoreForm form) {
        return ResponseEntity.ok(ownerService.addStore(account, form));
    }

    // 점주 상점 정보 수정
    @PutMapping("/store/{storeId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<?> editStoreInfo(@AuthenticationPrincipal Account account, @PathVariable Long storeId, @RequestBody EditStoreForm form) {
        return ResponseEntity.ok(ownerService.editStoreInfo(account, storeId, form));
    }

    // 점주 상점 삭제
    @DeleteMapping("/store/{storeId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<?> deleteStore(@AuthenticationPrincipal Account account, @PathVariable Long storeId) {
        ownerService.deleteStore(account, storeId);
        return ResponseEntity.ok().build();
    }

    // 점주 예약 확인
    @GetMapping("/store/reservation")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<?> checkReservations(@AuthenticationPrincipal Account account) {
        return ResponseEntity.ok(ownerService.getReservations(account));
    }

    // 점주 예약 확정 및 거절인
    @PutMapping("/store/reservation/{reservationId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<?> confirmReservation(@AuthenticationPrincipal Account account, @PathVariable Long reservationId, @RequestBody ReservationState state) {
        return ResponseEntity.ok(ownerService.confirmReservation(account, reservationId, state));

    }
}
