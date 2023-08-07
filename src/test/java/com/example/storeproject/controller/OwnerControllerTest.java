package com.example.storeproject.controller;

import com.example.storeproject.config.SecurityConfig;
import com.example.storeproject.constants.AccountType;
import com.example.storeproject.dto.AccountDto;
import com.example.storeproject.model.SignInForm;
import com.example.storeproject.model.SignUpForm;
import com.example.storeproject.security.TokenProvider;
import com.example.storeproject.service.OwnerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OwnerController.class)
@Import(SecurityConfig.class)
class OwnerControllerTest {

    @MockBean
    private OwnerService ownerService;
    @MockBean
    private TokenProvider tokenProvider;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void success_signup() throws Exception {
        //given
        given(ownerService.signUp(any()))
                .willReturn(AccountDto.builder()
                        .id(1L)
                        .email("test@naver.com")
                        .accountType(AccountType.ROLE_OWNER)
                        .build());
        //when
        //then
        mockMvc.perform(post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        new SignUpForm("test@naver.com", "1111")
                )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("test@naver.com"))
                .andExpect(jsonPath("$.accountType").value("ROLE_OWNER"));


    }

    @Test
    void success_signin() throws Exception {
        //given
        given(ownerService.signIn(any()))
                .willReturn(AccountDto.builder()
                        .id(1L)
                        .email("ofgongmu@gmail.com")
                        .accountType(AccountType.ROLE_OWNER)
                        .build());
        given(tokenProvider.createToken(anyString(), any()))
                .willReturn("token");
        //when
        //then
        mockMvc.perform(post("/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        new SignInForm("test@naver.com", "1111")
                )))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @WithUserDetails
    void success_deleteAccount() {
        //given
        //when
        //then
    }
}