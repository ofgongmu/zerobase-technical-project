package com.example.storeproject.dto;

import com.example.storeproject.constants.AccountType;
import com.example.storeproject.entity.Account;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDto {

    private long id;

    private String email;

    private boolean activated;

    private AccountType accountType;

    public static AccountDto fromEntity(Account account) {
        return AccountDto.builder()
                .id(account.getId())
                .email(account.getEmail())
                .activated(account.isActivated())
                .accountType(AccountType.ROLE_OWNER)
                .build();
    }
}
