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
@Table(name = "trade_name")
public class TradeName {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false, length = 255, name = "td_name")
    private String tdName;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = true, name = "created_at")
    private Date createAt;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = true, name = "updated_at")
    private Date updatedAt;
    @Column(nullable = true, length = 255, name = "start_price")
    private String startPrice;
    @Column(nullable = true, length = 255, name = "end_price")
    private String endPrice;
}
