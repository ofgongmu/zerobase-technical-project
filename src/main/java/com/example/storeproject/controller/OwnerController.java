package com.example.storeproject.controller;

import com.example.storeproject.dto.AccountDto;
import com.example.storeproject.dto.StoreDto;
import com.example.storeproject.model.AddStoreForm;
import com.example.storeproject.model.SignInForm;
import com.example.storeproject.model.SignUpForm;
import com.example.storeproject.service.OwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OwnerController {
    private final OwnerService ownerService;

    @PostMapping("/signup")
    public ResponseEntity<AccountDto> signUp(@RequestBody SignUpForm form) {
        return ResponseEntity.ok(ownerService.signUp(form));
    }

    @PostMapping("/signin")
    public ResponseEntity<AccountDto> signIn(@RequestBody SignInForm form) {
        return ResponseEntity.ok(ownerService.signIn(form));
    }

    @PostMapping("/store/add")
    public ResponseEntity<StoreDto> addStore(@RequestBody AddStoreForm form) {
        return ResponseEntity.ok(ownerService.addStore(form));
    }

}
