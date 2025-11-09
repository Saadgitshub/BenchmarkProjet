package com.example.WebPerformance.controller;

import com.example.WebPerformance.dto.ItemDto;
import com.example.WebPerformance.repository.CategoryRepository;
import com.example.WebPerformance.repository.ItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
public class CategoryItemRelationController {

    private final ItemRepository itemRepo;
    private final CategoryRepository catRepo;

    public CategoryItemRelationController(ItemRepository itemRepo, CategoryRepository catRepo) {
        this.itemRepo = itemRepo;
        this.catRepo = catRepo;
    }

    @GetMapping("/categories/{catId}/items")
    public Page<ItemDto> itemsOfCategory(@PathVariable Long catId, Pageable pageable) {
        catRepo.findById(catId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + catId));

        boolean useJoin = Boolean.parseBoolean(System.getProperty("join.fetch", "false"));
        if (useJoin) {
            return itemRepo.findByCategoryIdWithJoin(catId, pageable).map(ItemDto::fromEntity);
        } else {
            return itemRepo.findByCategoryId(catId, pageable).map(ItemDto::fromEntity);
        }
    }
}