package com.example.food_court_ms_small_square.infrastructure.input.rest;

import com.example.food_court_ms_small_square.application.dto.request.DishRequestDto;
import com.example.food_court_ms_small_square.application.dto.request.UpdateDishRequestDto;
import com.example.food_court_ms_small_square.application.handler.IDishHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dish")
@RequiredArgsConstructor
public class DishRestController {

    private final IDishHandler dishHandler;

    @PostMapping("/save")
    public ResponseEntity<Void> saveDish(@RequestBody @Valid DishRequestDto dishRequestDto) {
        dishHandler.saveDish(dishRequestDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<Void> updateDish(@RequestBody @Valid UpdateDishRequestDto updateDishRequestDto) {
        dishHandler.updateDish(updateDishRequestDto);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
