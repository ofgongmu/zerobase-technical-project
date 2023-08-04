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
public class ReviewDto {
    private String storeName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime reservationDateTime;
    private int stars;
    private String review;

    public static ReviewDto fromEntity(Reservation reservation) {
        return ReviewDto.builder()
                .storeName(reservation.getStore().getName())
                .reservationDateTime(reservation.getReserveDateTime())
                .stars(reservation.getStars())
                .review(reservation.getReview())
                .build();
    }
}
