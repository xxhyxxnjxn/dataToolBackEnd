package com.gmclabs.datatoolbackend.repository;

import com.gmclabs.datatoolbackend.entity.TradeTotal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TradeTotalRepository extends JpaRepository<TradeTotal, Integer> {
    TradeTotal findByCategoryEqualsAndTradeNameIdEquals(String category,int tdNameId);

    @Transactional
    @Modifying
    @Query(value = "delete from trade_total where td_name_id = :tdNameId", nativeQuery = true)
    void deleteData(int tdNameId);
}
