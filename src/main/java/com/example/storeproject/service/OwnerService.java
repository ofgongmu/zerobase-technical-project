package com.example.storeproject.service;

import com.example.storeproject.StoreProjectApplication;
import com.example.storeproject.constants.AccountType;
import com.example.storeproject.constants.ReservationState;
import com.example.storeproject.dto.AccountDto;
import com.example.storeproject.dto.ReservationDto;
import com.example.storeproject.dto.StoreDto;
import com.example.storeproject.entity.Account;
import com.example.storeproject.entity.Reservation;
import com.example.storeproject.entity.Store;
import com.example.storeproject.exception.CustomException;
import com.example.storeproject.exception.ErrorCode;
import com.example.storeproject.model.AddStoreForm;
import com.example.storeproject.model.EditStoreForm;
import com.example.storeproject.model.SignInForm;
import com.example.storeproject.model.SignUpForm;
import com.example.storeproject.repository.AccountRepository;
import com.example.storeproject.repository.ReservationRepository;
import com.example.storeproject.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OwnerService implements UserDetailsService {
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    private final StoreRepository storeRepository;
    private final ReservationRepository reservationRepository;
    private static final Logger logger = LoggerFactory.getLogger(StoreProjectApplication.class);


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return this.accountRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_DOES_NOT_EXIST));
    }

    // 점주 회원 가입
    @Transactional
    public AccountDto signUp(SignUpForm form) {
        checkNonExistAccount(form.getEmail());
        logger.trace("OWNER SIGNUP: {}", form.getEmail());

        return AccountDto.fromEntity(
                accountRepository.save(
                        Account.builder()
                                .email(form.getEmail())
                                .password(encodePassword(form.getPassword()))
                                .role(AccountType.ROLE_OWNER)
                                .activated(true)
                                .build())
                );
    }

    // 점주 로그인
    @Transactional
    public AccountDto signIn(SignInForm form) {
        Account account = accountRepository.findByEmail(form.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_DOES_NOT_EXIST));

        if (!passwordEncoder.matches(form.getPassword(), account.getPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_IS_INCORRECT);
        }
        if (!account.isActivated()) {
            throw new CustomException(ErrorCode.UNACTIVATED_ACCOUNT);
        }
        logger.trace("OWNER SIGNIN: {}", form.getEmail());

        return AccountDto.fromEntity(account);
    }

    // 점주 계정 탈퇴
    @Transactional
    public void deleteOwnerAccount(Account account) {
        checkIfStoresDeleted(account);
        account.setActivated(false);
        logger.trace("OWNER WITHDRAW: {}", account.getEmail());

        accountRepository.save(account);
    }

    // 점주 상점 추가
    @Transactional
    public StoreDto addStore(Account account, AddStoreForm form) {
        checkNonAddedStore(form.getName(), form.getAddress());

        logger.trace("OWNER {} ADDED STORE {}", account.getEmail(), form.getName());

        return StoreDto.fromEntity(
                storeRepository.save(
                        Store.builder()
                                .name(form.getName())
                                .address(form.getAddress())
                                .description(form.getDescription())
                                .account(account)
                                .build())
                );
    }


    // 점주 상점 정보 수정
    @Transactional
    public StoreDto editStoreInfo(Account account, Long storeId, EditStoreForm form) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_DOES_NOT_EXIST));
        checkIfStoreOwner(account, store);

        store.setName(form.getName());
        store.setAddress(form.getAddress());
        store.setDescription(form.getDescription());

        logger.trace("OWNER {} EDITED STORE {}", account.getEmail(), form.getName());

        return StoreDto.fromEntity(storeRepository.save(store));
    }

    // 점주 상점 삭제
    @Transactional
    public void deleteStore(Account account, Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_DOES_NOT_EXIST));
        checkIfStoreOwner(account, store);

        logger.trace("OWNER {} DELETED STORE {}", account.getEmail(), store.getName());

        storeRepository.delete(store);
    }


    // 점주 예약 목록 확인
    @Transactional(readOnly = true)
    public List<ReservationDto> getReservations(Account account) {
        List<Store> stores = storeRepository.findByAccount(account);
        return stores.stream().flatMap(store -> reservationRepository.findByStoreOrderByStoreDescReserveDateTimeDesc(store).stream())
                .map(ReservationDto::fromEntity).collect(Collectors.toList());
    }

    // 점주 예약 확정 및 거절
    @Transactional
    public ReservationDto confirmReservation(Account account, Long reservationId, ReservationState state) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_DOES_NOT_EXIST));
        checkIfReservationStoreOwner(account, reservation);
        checkIfNotCanceled(reservation);
        reservation.setReservationState(state);

        logger.trace("OWNER {} CHANGED RESERVATION {} {} STATE TO {}", account.getEmail(), reservation.getStore().getName(), reservation.getReserveDateTime(), state);

        return ReservationDto.fromEntity(reservationRepository.save(reservation));
    }



    // 이미 가입한 적 있는 이메일인지 확인
    private void checkNonExistAccount(String email) {
        if (accountRepository.existsByEmail(email)) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_REGISTERED);
        }
    }

    // 비밀번호 암호화
    private String encodePassword(String password) {
        if (password == null || password.length() < 1) {
            throw new CustomException(ErrorCode.PASSWORD_CANNOT_BE_NULL);
        }
        return passwordEncoder.encode(password);
    }

    // 계정 삭제 전 해당 계정으로 등록된 상점 존재하는지 확인
    private void checkIfStoresDeleted(Account account) {
        if (storeRepository.countByAccount(account) > 0) {
            throw new CustomException(ErrorCode.REGISTERED_STORE_EXISTS);
        }
    }

    // 이미 추가된 상점인지 확인
    private void checkNonAddedStore(String name, String address) {
        if (storeRepository.existsByNameAndAddress(name, address)) {
            throw new CustomException(ErrorCode.STORE_ALREADY_ADDED);
        }
    }

    // 로그인된 계정과 상점 주인이 동일한지 확인
    private void checkIfStoreOwner(Account account, Store store) {
        if (store.getAccount().getId() != account.getId()) {
            throw new CustomException(ErrorCode.STORE_OWNER_UNMATCH);
        }
    }

    // 로그인된 계정과 해당 예약이 있는 상점 주인이 동일한지 확인
    private void checkIfReservationStoreOwner(Account account, Reservation reservation) {
        if (reservation.getStore().getAccount().getId() != account.getId()) {
            throw new CustomException(ErrorCode.RESERVATION_STORE_OWNER_UNMATCH);
        }
    }

    // 취소된 예약인지 확인
    private void checkIfNotCanceled(Reservation reservation) {
        if (reservation.getReservationState() == ReservationState.CANCELED) {
            throw new CustomException(ErrorCode.RESERVATION_CANCELED);
        }
    }

}
