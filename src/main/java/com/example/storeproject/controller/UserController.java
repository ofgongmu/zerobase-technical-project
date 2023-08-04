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

    @GetMapping("/search/by-name/{page}")
    public ResponseEntity<?> storeListByName(@PathVariable int page) {
        return ResponseEntity.ok(userService.getStoreListByName(page));
    }

    @GetMapping("/search/by-stars/{page}")
    public ResponseEntity<?> storeListByStars(@PathVariable int page) {
        return ResponseEntity.ok(userService.getStoreListByStars(page));
    }

//    @GetMapping("/search/by-distance/{page}")
//    public ResponseEntity<?> storeListByDistance(@PathVariable int page) {
//        return ResponseEntity.ok(userService.getStoreListByDistance(page));
//    }


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
        return ResponseEntity.ok(userService.cancelReservation(account, reservationId));
    }

    @PutMapping("/user/reserve/{reservationId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> writeReview(@AuthenticationPrincipal Account account, @PathVariable long reservationId, @RequestBody ReviewForm form) {
        return ResponseEntity.ok(userService.writeReview(account, reservationId, form));
    }

}
