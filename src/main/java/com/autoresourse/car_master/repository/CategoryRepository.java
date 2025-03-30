package com.autoresourse.car_master.repository;


import com.autoresourse.car_master.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    @Query("SELECT c FROM categories as c ORDER BY c.name ASC")
    List<Category> findAllOrderByNameASC();

    Optional<Category> findByName(String name);
}