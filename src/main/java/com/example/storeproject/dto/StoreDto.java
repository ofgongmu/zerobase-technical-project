package com.example.storeproject.dto;

import com.example.storeproject.entity.Reservation;
import com.example.storeproject.entity.Store;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.OptionalDouble;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreDto {
    private String name;
    private String address;
    private String description;

    private Double stars;
    private List<String> reviews;

    public static StoreDto fromEntity(Store store) {

        List<Integer> starList = store.getReservations().stream().map(Reservation::getStars).toList();
        OptionalDouble average = starList.stream().mapToDouble(i -> i).average();

        Double stars;
        if (average.isPresent()) {
           stars = average.getAsDouble();
        } else {
           stars = null;
        }

        return StoreDto.builder()
                .name(store.getName())
                .address(store.getAddress())
                .description(store.getDescription())
                .stars(stars)
                .reviews(store.getReservations().stream().map(Reservation::getReview).toList())
                .build();
    }
}
