package com.example.WebPerformance.dto;

import com.example.WebPerformance.entities.Category;
import com.example.WebPerformance.entities.Item;
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

    public static ItemDto fromEntity(Item entity) {
        return ItemDto.builder()
                .id(entity.getId())
                .sku(entity.getSku())
                .name(entity.getName())
                .price(entity.getPrice())
                .stock(entity.getStock())
                .description(entity.getDescription())
                .categoryId(entity.getCategory().getId())
                .build();
    }

    public Item toEntity(Category category) {
        return Item.builder()
                .sku(this.sku)
                .name(this.name)
                .price(this.price)
                .stock(this.stock)
                .description(this.description)
                .category(category)
                .build();
    }
}