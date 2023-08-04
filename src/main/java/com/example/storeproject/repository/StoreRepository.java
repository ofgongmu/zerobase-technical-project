package com.example.storeproject.repository;

import com.example.storeproject.entity.Account;
import com.example.storeproject.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    boolean existsByNameAndAddress(String name, String address);
    long countByAccount(Account account);

    List<Store> findByNameContainingIgnoreCaseOrAddressContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String keyword);
}
