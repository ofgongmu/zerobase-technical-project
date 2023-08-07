package com.example.storeproject.service;

import com.example.storeproject.StoreProjectApplication;
import com.example.storeproject.constants.ReservationState;
import com.example.storeproject.dto.ArrivalDto;
import com.example.storeproject.entity.Reservation;
import com.example.storeproject.entity.Store;
import com.example.storeproject.exception.CustomException;
import com.example.storeproject.exception.ErrorCode;
import com.example.storeproject.model.ArrivalForm;
import com.example.storeproject.repository.ReservationRepository;
import com.example.storeproject.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class KioskService {
    private final StoreRepository storeRepository;
    private final ReservationRepository reservationRepository;

    private static final Logger logger = LoggerFactory.getLogger(StoreProjectApplication.class);

    // 방문 확인
    @Transactional
    public ArrivalDto confirmArrival(ArrivalForm form) {
        Store store = storeRepository.findById(form.getStoreId())
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_DOES_NOT_EXIST));
        Reservation reservation = reservationRepository.findByStoreAndReserveDateTimeAndUserContact(store, form.getReserveDateTime(), form.getUserContact())
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_DOES_NOT_EXIST));

        checkAccepted(reservation.getReservationState());
        checkOnTime(reservation.getReserveDateTime());

        reservation.setVisitedYn(true);
        logger.trace("RESERVATION {} {} VISITED AT {}", reservation.getStore().getName(), reservation.getReserveDateTime(), LocalDateTime.now());
        return ArrivalDto.fromEntity(reservationRepository.save(reservation));
    }

    // 확정된 예약인지 확인
    private void checkAccepted(ReservationState reservationState) {
        if (reservationState != ReservationState.ACCEPTED) {
            throw new CustomException(ErrorCode.UNACCEPTED_RESERVATION);
        }
    }

    // 예약 시간 10분 전까지 도착했는지 확인
    private void checkOnTime(LocalDateTime reserveDateTime) {
        if (LocalDateTime.now().isAfter(reserveDateTime.minusMinutes(10))) {
            throw new CustomException(ErrorCode.LATE_ARRIVAL);
        }
    }
}
