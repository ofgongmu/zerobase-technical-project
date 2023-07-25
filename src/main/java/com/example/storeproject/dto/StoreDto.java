package com.example.storeproject.dto;

import com.example.storeproject.entity.Account;
import com.example.storeproject.entity.Store;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreDto {
    private String name;
    private String address;
    private String description;

    public static StoreDto fromEntity(Store store) {
        return StoreDto.builder()
                .name(store.getName())
                .address(store.getAddress())
                .description(store.getDescription())
                .build();
    }
}
