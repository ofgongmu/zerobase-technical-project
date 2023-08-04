package com.example.storeproject.service;

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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OwnerService implements UserDetailsService {
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    private final StoreRepository storeRepository;
    private final ReservationRepository reservationRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return this.accountRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_DOES_NOT_EXIST));
    }

    public AccountDto signUp(SignUpForm form) {
        checkNonExistAccount(form.getEmail());
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

    public AccountDto signIn(SignInForm form) {
        Account account = accountRepository.findByEmail(form.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_DOES_NOT_EXIST));

        if (!passwordEncoder.matches(form.getPassword(), account.getPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_IS_INCORRECT);
        }
        if (!account.isActivated()) {
            throw new CustomException(ErrorCode.UNACTIVATED_ACCOUNT);
        }
        return AccountDto.fromEntity(account);
    }

    public void deleteOwnerAccount(Account account) {
        checkIfStoresDeleted(account);
        account.setActivated(false);
        accountRepository.save(account);
    }

    public StoreDto addStore(Account account, AddStoreForm form) {
        checkNonAddedStore(form.getName(), form.getAddress());

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

    public StoreDto editStoreInfo(Account account, Long storeId, EditStoreForm form) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_DOES_NOT_EXIST));
        checkIfStoreOwner(account, store);

        store.setName(form.getName());
        store.setAddress(form.getAddress());
        store.setDescription(form.getDescription());

        return StoreDto.fromEntity(storeRepository.save(store));
    }

    public void deleteStore(Account account, Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_DOES_NOT_EXIST));
        checkIfStoreOwner(account, store);
        storeRepository.delete(store);
    }


    public List<ReservationDto> getReservations(Account account) {
        List<Store> stores = storeRepository.findByAccount(account);
        return stores.stream().flatMap(store -> reservationRepository.findByStoreOrderByStoreDescReserveDateTimeDesc(store).stream())
                .map(ReservationDto::fromEntity).collect(Collectors.toList());
    }

    public ReservationDto confirmReservation(Account account, Long reservationId, ReservationState state) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_DOES_NOT_EXIST));
        checkIfReservationStoreOwner(account, reservation);
        reservation.setReservationState(state);
        return ReservationDto.fromEntity(reservationRepository.save(reservation));
    }


    private void checkNonExistAccount(String email) {
        if (accountRepository.existsByEmail(email)) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_REGISTERED);
        }
    }

    private String encodePassword(String password) {
        if (password == null || password.length() < 1) {
            throw new CustomException(ErrorCode.PASSWORD_CANNOT_BE_NULL);
        }
        return passwordEncoder.encode(password);
    }

    private void checkIfStoresDeleted(Account account) {
        if (storeRepository.countByAccount(account) > 0) {
            throw new CustomException(ErrorCode.REGISTERED_STORE_EXISTS);
        }
    }

    private void checkNonAddedStore(String name, String address) {
        if (storeRepository.existsByNameAndAddress(name, address)) {
            throw new CustomException(ErrorCode.STORE_ALREADY_ADDED);
        }
    }

    private void checkIfStoreOwner(Account account, Store store) {
        if (store.getAccount().getId() != account.getId()) {
            throw new CustomException(ErrorCode.STORE_OWNER_UNMATCH);
        }
    }

    private void checkIfReservationStoreOwner(Account account, Reservation reservation) {
        if (reservation.getStore().getAccount().getId() != account.getId()) {
            throw new CustomException(ErrorCode.RESERVATION_STORE_OWNER_UNMATCH);
        }
    }

}
