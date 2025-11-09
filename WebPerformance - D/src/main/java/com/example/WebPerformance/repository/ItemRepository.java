package com.example.WebPerformance.repository;

import com.example.WebPerformance.entities.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

@RepositoryRestResource(collectionResourceRel = "items", path = "items")
public interface ItemRepository extends JpaRepository<Item, Long> {

    @RestResource(path = "category", rel = "by-category")
    Page<Item> findByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);

    @Query("SELECT i FROM Item i JOIN FETCH i.category WHERE i.category.id = :categoryId")
    @RestResource(path = "category-join", rel = "by-category-join")
    Page<Item> findByCategoryIdWithJoin(@Param("categoryId") Long categoryId, Pageable pageable);
}