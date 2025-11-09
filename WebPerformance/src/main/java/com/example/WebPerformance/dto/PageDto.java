package com.example.WebPerformance.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PageDto<T> {
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    public PageDto(List<T> content, int page, int size, long totalElements) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = (int) Math.ceil((double) totalElements / size);
    }
}
