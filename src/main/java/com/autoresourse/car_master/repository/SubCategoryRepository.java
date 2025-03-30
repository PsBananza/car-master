package com.autoresourse.car_master.repository;

import com.autoresourse.car_master.entity.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory, UUID> {

    List<SubCategory> findAllByCategoryId(UUID categoryId);

    boolean existsByCategoryId(UUID categoryId);

    @Query("SELECT DISTINCT su FROM sub_categories as su JOIN su.category ca WHERE su.name = :name and ca.id = :id")
    Optional<SubCategory> findByNameAndCategory(@Param("name") String name, @Param("id") UUID id);
}

