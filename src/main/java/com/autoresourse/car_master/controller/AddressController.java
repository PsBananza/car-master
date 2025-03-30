package com.autoresourse.car_master.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
public class AddressController {

    private static final Map<String, List<String>> districtsCities = Map.of(
            "Ростовская область", List.of(
                    "Ростов-на-Дону", "Аксай", "Азов", "Батайск", "Волгодонск", "Гуково", "Зверево",
                    "Каменск-Шахтинский", "Красносулинск", "Миллерово", "Морозовск", "Новочеркасск",
                    "Новошахтинск", "Пролетарск", "Ремонтное", "Сальск", "Семикаракорск", "Шахты",
                    "Таганрог", "Тимашевск", "Чертков", "Цимлянск", "Шолоховский"
            ),
            "Краснодарский край", List.of(
                    "Краснодар"
            )
    );

    @GetMapping("/cities")
    public ResponseEntity<Map<String, List<String>>> getCitiesByDistrict(@RequestParam String district) {
        List<String> cities = districtsCities.getOrDefault(district, Collections.emptyList());
        Map<String, List<String>> response = new HashMap<>();
        response.put("cities", cities);
        return ResponseEntity.ok(response);
    }
}