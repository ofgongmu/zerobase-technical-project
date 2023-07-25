package com.example.storeproject.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    EMAIL_ALREADY_REGISTERED("이미 가입된 메일 주소입니다."),
    PASSWORD_CANNOT_BE_NULL("비밀번호는 1자 이상이어야 합니다."),
    ACCOUNT_DOES_NOT_EXIST("존재하지 않는 계정입니다."),
    PASSWORD_IS_INCORRECT("비밀번호가 일치하지 않습니다."),

    STORE_ALREADY_ADDED("이미 등록된 가게입니다.");

    private final String description;
}
