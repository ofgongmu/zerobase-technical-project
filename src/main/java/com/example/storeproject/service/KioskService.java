package com.example.storeproject.service;

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
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class KioskService {
    private final StoreRepository storeRepository;
    private final ReservationRepository reservationRepository;

    public ArrivalDto confirmArrival(ArrivalForm form) {
        Store store = storeRepository.findById(form.getStoreId())
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_DOES_NOT_EXIST));
        Reservation reservation = reservationRepository.findByStoreAndReserveDateTimeAndUserContact(store, form.getReserveDateTime(), form.getUserContact())
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_DOES_NOT_EXIST));

        checkAccepted(reservation.getReservationState());
        checkOnTime(reservation.getReserveDateTime());

        reservation.setVisitedYn(true);
        return ArrivalDto.fromEntity(reservationRepository.save(reservation));
    }

    private void checkAccepted(ReservationState reservationState) {
        if (reservationState != ReservationState.ACCEPTED) {
            throw new CustomException(ErrorCode.UNACCEPTED_RESERVATION);
        }
    }

    private void checkOnTime(LocalDateTime reserveDateTime) {
        if (LocalDateTime.now().isAfter(reserveDateTime.minusMinutes(10))) {
            throw new CustomException(ErrorCode.LATE_ARRIVAL);
        }
    }
}
