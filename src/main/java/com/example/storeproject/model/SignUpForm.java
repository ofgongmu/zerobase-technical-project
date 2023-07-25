package com.example.storeproject.model;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpForm {
    private String email;
    private String password;
}
