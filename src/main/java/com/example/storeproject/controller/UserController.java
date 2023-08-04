package com.example.storeproject.controller;

import com.example.storeproject.dto.AccountDto;
import com.example.storeproject.entity.Account;
import com.example.storeproject.model.ReserveForm;
import com.example.storeproject.model.SearchForm;
import com.example.storeproject.model.SignInForm;
import com.example.storeproject.model.SignUpForm;
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

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody SignUpForm form) {
        return ResponseEntity.ok(userService.signUp(form));
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody SignInForm form) {
        AccountDto accountDto = userService.signIn(form);
        String token = tokenProvider.createToken(accountDto.getEmail(), accountDto.getAccountType());
        return ResponseEntity.ok(token);
    }

    @DeleteMapping("/account/{id}")
    public ResponseEntity<?> deleteUserAccount(@AuthenticationPrincipal Account account) {
        userService.deleteUserAccount(account);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchStore(@RequestBody SearchForm form) {
        return ResponseEntity.ok(userService.searchStore(form));
    }


    @PostMapping("/user/reserve/{storeId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> reserveStore(@AuthenticationPrincipal Account account, @PathVariable long storeId, @RequestBody ReserveForm form) {
        return ResponseEntity.ok(userService.reserveStore(account, storeId, form));
    }

    @GetMapping("/user/reserve/{reservationId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> seeReservation(@AuthenticationPrincipal Account account, @PathVariable long reservationId) {
        return ResponseEntity.ok(userService.seeReservation(account, reservationId));
    }

    @DeleteMapping("/user/reserve/{reservationId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> cancelReservation(@AuthenticationPrincipal Account account, @PathVariable long reservationId) {
        userService.cancelReservation(account, reservationId);
        return ResponseEntity.ok().build();
    }

}
