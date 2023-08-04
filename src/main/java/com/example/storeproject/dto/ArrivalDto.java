package com.example.storeproject.dto;

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
public class ArrivalDto {
    private String storeName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime reservationDateTime;
    private boolean visitedYn;

    public static ArrivalDto fromEntity(Reservation reservation) {
        return ArrivalDto.builder()
                .storeName(reservation.getStore().getName())
                .reservationDateTime(reservation.getReserveDateTime())
                .visitedYn(reservation.isVisitedYn())
                .build();
    }
}
