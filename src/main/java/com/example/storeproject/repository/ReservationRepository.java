package com.example.storeproject.repository;

import com.example.storeproject.entity.Account;
import com.example.storeproject.entity.Reservation;
import com.example.storeproject.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    long countByAccountAndStoreAndRAndReserveDateTime(Account account, Store store, LocalDateTime time);
    Optional<Reservation> findByStoreAndReserveDateTimeAndUserContact(Store store, LocalDateTime reserveDateTime, String userContact);

    long countByAccount(Account account);

    List<Reservation> findByStoreOrderByStoreDescReserveDateTimeDesc(Store store);
}
