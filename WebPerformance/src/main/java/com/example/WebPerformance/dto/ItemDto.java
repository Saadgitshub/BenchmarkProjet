package com.example.WebPerformance.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDto {
    private Long id;
    private String sku;
    private String name;
    private BigDecimal price;
    private Integer stock;
    private String description;
    private Long categoryId;
}