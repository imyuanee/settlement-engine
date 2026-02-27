package com.yuan.settlement.repository;

import com.yuan.settlement.domain.SettlementResult;
import com.yuan.settlement.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {
    Optional<Store> findByStoreId(String storeId);
}