package com.autoresourse.car_master.repository;

import com.autoresourse.car_master.entity.OrganizationCards;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrganizationCardRepository extends JpaRepository<OrganizationCards, UUID> {

    @Query("SELECT oc FROM organization_cards oc JOIN oc.subCategories sub WHERE sub.name = :subName and oc.city = :city")
    List<OrganizationCards> findBySubCategoriesName(@Param("subName") String subName, @Param("city") String city);

    @Query("SELECT oc FROM organization_cards oc JOIN oc.categories cat WHERE cat.id = :catId and oc.city = :city")
    List<OrganizationCards> findByCategoryId(@Param("catId") UUID catId, @Param("city") String city);

}
