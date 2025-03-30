package com.autoresourse.car_master.repository;

import com.autoresourse.car_master.entity.Files;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FileRepository extends JpaRepository<Files, UUID> {
}
