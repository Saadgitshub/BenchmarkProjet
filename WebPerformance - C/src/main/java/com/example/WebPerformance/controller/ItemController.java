package com.example.WebPerformance.controller;

import com.example.WebPerformance.dto.ItemDto;
import com.example.WebPerformance.entities.Category;
import com.example.WebPerformance.entities.Item;
import com.example.WebPerformance.repository.CategoryRepository;
import com.example.WebPerformance.repository.ItemRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemRepository itemRepo;
    private final CategoryRepository catRepo;

    public ItemController(ItemRepository itemRepo, CategoryRepository catRepo) {
        this.itemRepo = itemRepo;
        this.catRepo = catRepo;
    }

    @GetMapping
    public Page<ItemDto> list(
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

    @GetMapping("/{id}")
    public ItemDto get(@PathVariable Long id) {
        return itemRepo.findById(id)
                .map(ItemDto::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found: " + id));
    }

    @PostMapping
    public ResponseEntity<ItemDto> create(@Valid @RequestBody ItemDto dto) {
        Category cat = catRepo.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + dto.getCategoryId()));
        Item entity = dto.toEntity(cat);
        Item saved = itemRepo.save(entity);
        return ResponseEntity
                .created(URI.create("/items/" + saved.getId()))
                .body(ItemDto.fromEntity(saved));
    }

    @PutMapping("/{id}")
    public ItemDto update(@PathVariable Long id, @Valid @RequestBody ItemDto dto) {
        return itemRepo.findById(id)
                .map(existing -> {
                    Category cat = catRepo.findById(dto.getCategoryId())
                            .orElseThrow(
                                    () -> new ResourceNotFoundException("Category not found: " + dto.getCategoryId()));
                    existing.setSku(dto.getSku());
                    existing.setName(dto.getName());
                    existing.setPrice(dto.getPrice());
                    existing.setStock(dto.getStock());
                    existing.setDescription(dto.getDescription());
                    existing.setCategory(cat);
                    return ItemDto.fromEntity(itemRepo.save(existing));
                })
                .orElseThrow(() -> new ResourceNotFoundException("Item not found: " + id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        itemRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}