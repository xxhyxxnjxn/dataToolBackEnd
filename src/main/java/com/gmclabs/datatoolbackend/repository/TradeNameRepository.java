package com.gmclabs.datatoolbackend.repository;

import com.gmclabs.datatoolbackend.entity.TradeName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface TradeNameRepository extends JpaRepository<TradeName, Integer> {

    TradeName findByTdName(String tdName);

    @Transactional
    @Modifying
    @Query(value = "delete from trade_name where id = :id", nativeQuery = true)
    void deleteData(int id);
}
