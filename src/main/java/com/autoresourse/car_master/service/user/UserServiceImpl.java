package com.autoresourse.car_master.service.user;

import com.autoresourse.car_master.entity.Users;
import com.autoresourse.car_master.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Value("${webApp.id}")
    private Long webAppUrl;

    @Override
    @Transactional
    public Users findUserOrCreate(Long telegramId) {
        Users user =  findUserByTelegramId(telegramId);

        if (user == null) {
            return createUser(telegramId);
        } else if (Objects.equals(user.getTelegramId(), webAppUrl) || Objects.equals(user.getTelegramId(), 5037955504L)) {
            return user;
        } else {
            throw new RuntimeException("Invalid user");
        }
    }

    private Users createUser(Long telegramId) {
        Users newUser = new Users();
        newUser.setTelegramId(telegramId);
        save(newUser);

        return newUser;
    }

    private void save(Users user) {
        userRepository.save(user);
    }

    private Users findUserByTelegramId(Long telegramId) {
        return userRepository.findByTelegramId(telegramId);
    }
}
