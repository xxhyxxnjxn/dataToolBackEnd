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
@Table(name = "trade_total")
public class TradeTotal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne(fetch = FetchType.EAGER)//연관관계맺음 Many = Many, User =One
    @JoinColumn(name = "td_name_id")
    private TradeName tradeName; //DB는 오프젝트를 저장할 수 없다. FK, 자바는 오브젝트를 저장할수있다.
    @Column(nullable = false, length = 255)
    private String revenuePer;
    @Column(nullable = false, length = 255)
    private String lossPer;
    @Column(nullable = false, length = 255)
    private String revenueTotal;
    @Column(nullable = false, length = 255)
    private String transCount;
    @Column(nullable = false, length = 255)
    private String revenueCount;
    @Column(nullable = false, length = 255)
    private String lossCount;
    @Column(nullable = false, length = 255)
    private String victoryPer;
    @Column(nullable = false, length = 255)
    private String category;
    @Column(nullable = true, name = "created_at")
    private Date createAt;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = true, name = "updated_at")
    private Date updatedAt;

}
