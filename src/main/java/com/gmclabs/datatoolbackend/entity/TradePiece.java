package com.gmclabs.datatoolbackend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "trade_piece")
public class TradePiece {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne(fetch = FetchType.EAGER)//연관관계맺음 Many = Many, User =One
    @JoinColumn(name = "td_name_id")
    private TradeName tradeName; //DB는 오프젝트를 저장할 수 없다. FK, 자바는 오브젝트를 저장할수있다.
    @Column(nullable = false, length = 255, name = "td_id")
    private String tdId;
    @Column(nullable = true, length = 255)
    private String start;
    @Column(nullable = true, length = 255)
    private String entry;
    @Column(nullable = true, length = 255)
    private String end;
    @Column(nullable = false, length = 255)
    private String profitPer;
    @Column(nullable = false, length = 255)
    private String runupPer;
    @Column(nullable = false, length = 255)
    private String drawdownPer;
    @Column(nullable = true, name = "created_at")
    private Date createAt;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = true, name = "updated_at")
    private Date updatedAt;
    @Column(nullable = true, length = 5000)
    private String memo;
}
