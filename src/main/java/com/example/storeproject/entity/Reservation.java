package com.example.storeproject.entity;

import com.example.storeproject.constants.ReservationState;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    Store store;

    @ManyToOne
    @JoinColumn(nullable = false)
    Account account;

    @Column(nullable = false)
    private LocalDateTime reserveDateTime;

    @Column(nullable = false)
    private String userContact;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReservationState reservationState;

    private boolean visitedYn;

    @Min(1)
    @Max(5)
    private int stars;
    private String review;


    @CreatedDate
    private LocalDateTime requestedAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
