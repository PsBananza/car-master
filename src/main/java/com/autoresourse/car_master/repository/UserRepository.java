package com.autoresourse.car_master.repository;

import com.autoresourse.car_master.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<Users, UUID> {

    Users findByTelegramId(Long telegramId);
}
