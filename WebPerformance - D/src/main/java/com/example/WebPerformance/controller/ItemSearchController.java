package com.example.WebPerformance.controller;

import com.example.WebPerformance.dto.ItemDto;
import com.example.WebPerformance.repository.ItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
public class ItemSearchController {

    private final ItemRepository itemRepo;

    public ItemSearchController(ItemRepository itemRepo) {
        this.itemRepo = itemRepo;
    }

    @GetMapping("/items")
    public Page<ItemDto> search(
            @RequestParam(required = false) Long categoryId,
            Pageable pageable) {

        boolean useJoin = Boolean.parseBoolean(System.getProperty("join.fetch", "false"));

        if (categoryId != null) {
            if (useJoin) {
                return itemRepo.findByCategoryIdWithJoin(categoryId, pageable)
                        .map(ItemDto::fromEntity);
            } else {
                return itemRepo.findByCategoryId(categoryId, pageable)
                        .map(ItemDto::fromEntity);
            }
        }
        return itemRepo.findAll(pageable).map(ItemDto::fromEntity);
    }
}