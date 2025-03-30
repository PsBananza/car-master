package com.autoresourse.car_master.service.user;

import com.autoresourse.car_master.entity.Users;

public interface UserService {
    Users findUserOrCreate(Long telegramId);
}
