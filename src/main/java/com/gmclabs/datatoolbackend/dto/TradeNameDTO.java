package com.gmclabs.datatoolbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TradeNameDTO {
    private int id;
    private String tdName;
    private Date createAt;
    private Date updatedAt;
}
