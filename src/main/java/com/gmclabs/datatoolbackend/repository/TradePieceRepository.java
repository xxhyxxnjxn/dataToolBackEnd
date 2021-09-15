package com.gmclabs.datatoolbackend.repository;

import com.gmclabs.datatoolbackend.entity.TradePiece;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface TradePieceRepository extends JpaRepository<TradePiece, Integer> {

    @Query(value = "select entry from trade_piece where td_name_id = :tdNameId group by entry", nativeQuery = true)
    List<String> findGroupByEntry(String tdNameId);

    List<TradePiece> findAllByEntryEqualsAndTradeNameIdEquals(String entry, int tdNameId);

    List<TradePiece> findAllByTradeNameIdEquals(int tdNameId);

    @Transactional
    @Modifying
    @Query(value = "delete from trade_piece where td_name_id = :tdNameId", nativeQuery = true)
    void deleteData(int tdNameId);
}
