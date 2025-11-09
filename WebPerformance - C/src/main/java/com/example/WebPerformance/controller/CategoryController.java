package com.example.WebPerformance.controller;

import com.example.WebPerformance.dto.CategoryDto;
import com.example.WebPerformance.entities.Category;
import com.example.WebPerformance.repository.CategoryRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryRepository repo;

    public CategoryController(CategoryRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public Page<CategoryDto> list(Pageable pageable) {
        return repo.findAll(pageable).map(CategoryDto::fromEntity);
    }

    @GetMapping("/{id}")
    public CategoryDto get(@PathVariable Long id) {
        return repo.findById(id)
                .map(CategoryDto::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
    }

    @PostMapping
    public ResponseEntity<CategoryDto> create(@Valid @RequestBody CategoryDto dto) {
        Category entity = dto.toEntity();
        Category saved = repo.save(entity);
        return ResponseEntity
                .created(URI.create("/categories/" + saved.getId()))
                .body(CategoryDto.fromEntity(saved));
    }

    @PutMapping("/{id}")
    public CategoryDto update(@PathVariable Long id, @Valid @RequestBody CategoryDto dto) {
        return repo.findById(id)
                .map(existing -> {
                    existing.setCode(dto.getCode());
                    existing.setName(dto.getName());
                    return CategoryDto.fromEntity(repo.save(existing));
                })
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}