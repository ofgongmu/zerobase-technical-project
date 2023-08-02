package com.example.storeproject.controller;

import com.example.storeproject.constants.AccountType;
import com.example.storeproject.dto.AccountDto;
import com.example.storeproject.entity.Account;
import com.example.storeproject.model.AddStoreForm;
import com.example.storeproject.model.SignInForm;
import com.example.storeproject.model.SignUpForm;
import com.example.storeproject.security.TokenProvider;
import com.example.storeproject.service.OwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OwnerController {
    private final OwnerService ownerService;
    private final TokenProvider tokenProvider;

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody SignUpForm form) {
        return ResponseEntity.ok(ownerService.signUp(form));
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody SignInForm form) {
        AccountDto accountDto = ownerService.signIn(form);
        String token = tokenProvider.createToken(accountDto.getEmail(), accountDto.getAccountType());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/store/add")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<?> addStore(@AuthenticationPrincipal Account account, @RequestBody AddStoreForm form) {
        return ResponseEntity.ok(ownerService.addStore(account, form));
    }

}
