package com.example.storeproject.service;

import com.example.storeproject.constants.AccountType;
import com.example.storeproject.constants.ReservationState;
import com.example.storeproject.dto.AccountDto;
import com.example.storeproject.dto.ReservationDto;
import com.example.storeproject.dto.ReviewDto;
import com.example.storeproject.dto.StoreDto;
import com.example.storeproject.entity.Account;
import com.example.storeproject.entity.Reservation;
import com.example.storeproject.entity.Store;
import com.example.storeproject.exception.CustomException;
import com.example.storeproject.exception.ErrorCode;
import com.example.storeproject.model.*;
import com.example.storeproject.repository.AccountRepository;
import com.example.storeproject.repository.ReservationRepository;
import com.example.storeproject.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
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
                                .role(AccountType.ROLE_USER)
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

    public void deleteUserAccount(Account account) {
        checkIfReservationExists(account);
        account.setActivated(false);
        accountRepository.save(account);
    }

    public List<StoreDto> searchStore(SearchForm form) {
        List<Store> result = storeRepository.findByNameContainingIgnoreCaseOrAddressContainingIgnoreCaseOrDescriptionContainingIgnoreCase(form.getKeyword(), form.getKeyword(), form.getKeyword());
        return result.stream().map(StoreDto::fromEntity).collect(Collectors.toList());
    }

    public Page<StoreDto> getStoreListByName(int page) {
        Pageable pageable = PageRequest.of(page, 10);
        List<StoreDto> result = new java.util.ArrayList<>(storeRepository.findAll().stream().map(StoreDto::fromEntity).toList());
        result.sort(Comparator.comparing(StoreDto::getName));
        return new PageImpl<>(result, pageable, result.size());
    }


    public Page<StoreDto> getStoreListByStars(int page) {
        Pageable pageable = PageRequest.of(page, 10);
        List<StoreDto> result = new java.util.ArrayList<>(storeRepository.findAll().stream().map(StoreDto::fromEntity).toList());
        result.sort((x, y) -> y.getStars().compareTo(x.getStars()));
        return new PageImpl<>(result, pageable, result.size());
    }

    public ReservationDto reserveStore(Account account, long storeId, ReserveForm form) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_DOES_NOT_EXIST));
        checkNonDuplicatedReserve(account, store, form);

        return ReservationDto.fromEntity(
                reservationRepository.save(
                        Reservation.builder()
                                .store(store)
                                .account(account)
                                .reserveDateTime(form.getReserveDateTime())
                                .userContact(form.getUserContact())
                                .reservationState(ReservationState.PENDING)
                                .build()));
    }

    public ReservationDto seeReservation(Account account, long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_DOES_NOT_EXIST));
        checkReservationOwner(account, reservation);
        return ReservationDto.fromEntity(reservation);
    }

    public ReservationDto cancelReservation(Account account, long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_DOES_NOT_EXIST));
        checkReservationOwner(account, reservation);

        reservation.setReservationState(ReservationState.CANCELED);
        return ReservationDto.fromEntity(reservationRepository.save(reservation));
    }

    public ReviewDto writeReview(Account account, long reservationId, ReviewForm form) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_DOES_NOT_EXIST));

        checkReservationOwner(account, reservation);
        checkIfVisited(reservation);
        checkStarsInRange(form.getStars());

        reservation.setStars(form.getStars());
        reservation.setReview(form.getReview());
        return ReviewDto.fromEntity(reservationRepository.save(reservation));
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

    private void checkNonDuplicatedReserve(Account account, Store store, ReserveForm form) {
        if (reservationRepository.countByAccountAndStoreAndRAndReserveDateTime(account, store, form.getReserveDateTime()) > 0) {
            throw new CustomException(ErrorCode.DUPLICATED_RESERVATION);
        }
    }

    private void checkReservationOwner(Account account, Reservation reservation) {
        if (account.getId() != reservation.getAccount().getId()) {
            throw new CustomException(ErrorCode.RESERVATION_OWNER_UNMATCH);
        }
    }

    private void checkIfReservationExists(Account account) {
        if (reservationRepository.countByAccount(account) > 0)  {
            throw new CustomException(ErrorCode.ACCOUNT_RESERVATION_EXISTS);
        }
    }

    private void checkIfVisited(Reservation reservation) {
        if (!reservation.isVisitedYn()) {
            throw new CustomException(ErrorCode.UNVISITED_RESERVATION);
        }
    }

    private void checkStarsInRange(int stars) {
        if (stars < 1 || stars > 5) {
            throw new CustomException(ErrorCode.STARS_MUST_BETWEEN_1_TO_5);
        }
    }


}
