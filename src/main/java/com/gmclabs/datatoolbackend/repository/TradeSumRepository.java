package com.gmclabs.datatoolbackend.repository;

import com.gmclabs.datatoolbackend.entity.TradeSum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface TradeSumRepository extends JpaRepository<TradeSum, Integer> {
    @Query(value = "select * from trade_sum where td_name_id = :tdNameId and entry like 'short%'", nativeQuery = true)
    List<TradeSum> findAllByEntryLikeShortAndTradeNameIdEquals(int tdNameId);

    @Query(value = "select * from trade_sum where td_name_id = :tdNameId and entry like 'long%'", nativeQuery = true)
    List<TradeSum> findAllByEntryLikeLongAndTradeNameIdEquals(int tdNameId);

    @Query(value = "select * from trade_sum where td_name_id = :tdNameId and (entry like 'long%' or entry like 'short%')", nativeQuery = true)
    List<TradeSum> findAllByEntryLikeTotalAndTradeNameIdEquals(int tdNameId);

    @Transactional
    @Modifying
    @Query(value = "delete from trade_sum where td_name_id = :tdNameId", nativeQuery = true)
    void deleteData(int tdNameId);

    TradeSum findByEntryEqualsAndIdEquals(String entry,int id);
}
