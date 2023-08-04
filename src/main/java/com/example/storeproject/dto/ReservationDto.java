package com.example.storeproject.dto;

import com.example.storeproject.constants.ReservationState;
import com.example.storeproject.entity.Reservation;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationDto {
    private String storeName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime reservationDateTime;
    private ReservationState state;

    public static ReservationDto fromEntity(Reservation reservation) {
        return ReservationDto.builder()
                .storeName(reservation.getStore().getName())
                .reservationDateTime(reservation.getReserveDateTime())
                .state(reservation.getReservationState())
                .build();
    }

}
