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
    UNACTIVATED_ACCOUNT("탈퇴된 회원입니다."),

    STORE_ALREADY_ADDED("이미 등록된 가게입니다."),
    STORE_DOES_NOT_EXIST("존재하지 않는 상점입니다."),
    STORE_OWNER_UNMATCH("상점의 점주만 상점 정보를 수정할 수 있습니다."),
    REGISTERED_STORE_EXISTS("해당 계정으로 등록된 상점이 존재합니다."),

    ACCOUNT_RESERVATION_EXISTS("해당 계정으로 등록된 예약이 존재합니다."),
    DUPLICATED_RESERVATION("같은 날짜와 시간의 예약이 이미 존재합니다."),
    RESERVATION_DOES_NOT_EXIST("존재하지 않는 예약입니다."),
    RESERVATION_OWNER_UNMATCH("자신이 예약한 예약만을 확인할 수 있습니다."),
    RESERVATION_STORE_OWNER_UNMATCH("본인의 상점의 예약만 승인 혹은 거절할 수 있습니다."),
    UNACCEPTED_RESERVATION("승인되지 않은 예약입니다."),
    LATE_ARRIVAL("예약 시간 10분 전까지 도착해야 합니다.");

    private final String description;
}
