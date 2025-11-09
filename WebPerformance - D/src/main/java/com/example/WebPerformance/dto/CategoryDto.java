package com.example.WebPerformance.dto;

import lombok.*;

import com.example.WebPerformance.entities.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDto {
    private Long id;
    private String code;
    private String name;

    public static CategoryDto fromEntity(Category entity) {
        return CategoryDto.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .build();
    }

    public Category toEntity() {
        return Category.builder()
                .code(this.code)
                .name(this.name)
                .build();
    }
}