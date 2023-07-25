package com.example.storeproject.service;

import com.example.storeproject.constants.AccountType;
import com.example.storeproject.dto.AccountDto;
import com.example.storeproject.dto.StoreDto;
import com.example.storeproject.entity.Account;
import com.example.storeproject.entity.Store;
import com.example.storeproject.exception.CustomException;
import com.example.storeproject.exception.ErrorCode;
import com.example.storeproject.model.AddStoreForm;
import com.example.storeproject.model.SignInForm;
import com.example.storeproject.model.SignUpForm;
import com.example.storeproject.repository.AccountRepository;
import com.example.storeproject.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OwnerService {
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    private final StoreRepository storeRepository;

    public AccountDto signUp(SignUpForm form) {
        checkNonExistAccount(form.getEmail());
        return AccountDto.fromEntity(
                accountRepository.save(
                        Account.builder()
                                .email(form.getEmail())
                                .password(encodePassword(form.getPassword()))
                                .accountType(AccountType.OWNER)
                                .activated(true)
                                .build())
                );
    }

    public AccountDto signIn(SignInForm form) {
        Account account = accountRepository.findByEmail(form.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_DOES_NOT_EXIST));

        if (!passwordEncoder.matches(form.getPassword(), account.getPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_IS_INCORRECT);
        }
        return AccountDto.fromEntity(account);
    }

    public StoreDto addStore(AddStoreForm form) {
        checkNonAddedStore(form.getName(), form.getAddress());
        return StoreDto.fromEntity(
                storeRepository.save(
                        Store.builder()
                                .name(form.getName())
                                .address(form.getAddress())
                                .description(form.getDescription())
                                .build())
                );
    }
    private void checkNonExistAccount(String email) {
        if (accountRepository.existsByEmail(email)) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_REGISTERED);
        }
    }

    private String encodePassword(String password) {
        if (password == null || password.equals("")) {
            throw new CustomException(ErrorCode.PASSWORD_CANNOT_BE_NULL);
        }
        return passwordEncoder.encode(password);
    }

    private void checkNonAddedStore(String name, String address) {
        if (storeRepository.existsByNameAndAddress(name, address)) {
            throw new CustomException(ErrorCode.STORE_ALREADY_ADDED);
        }
    }
}
