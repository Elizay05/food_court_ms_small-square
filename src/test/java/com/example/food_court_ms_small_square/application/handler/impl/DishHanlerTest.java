package com.example.food_court_ms_small_square.application.handler.impl;

import com.example.food_court_ms_small_square.application.dto.request.DishRequestDto;
import com.example.food_court_ms_small_square.application.mapper.IDishRequestMapper;
import com.example.food_court_ms_small_square.domain.api.IDishServicePort;
import com.example.food_court_ms_small_square.domain.model.Category;
import com.example.food_court_ms_small_square.domain.model.Dish;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DishHanlerTest {

    @Mock
    private IDishServicePort dishServicePort;

    @Mock
    private IDishRequestMapper dishRequestMapper;

    @InjectMocks
    private DishHandler dishHandler;

    @Test
    void testSaveDish() {
        DishRequestDto dishRequestDto = new DishRequestDto(
                "Pizza", 12.5f, "Delicious pizza", "http://image.url", 1, "123456"
        );

        Category category = new Category(1L, null, null);
        Dish expectedDish = new Dish(
                null,
                "Pizza",
                category,
                "Delicious pizza",
                12.5f,
                "123456",
                "http://image.url",
                null
        );

        Mockito.when(dishRequestMapper.toDish(dishRequestDto)).thenReturn(expectedDish);

        dishHandler.saveDish(dishRequestDto);

        Mockito.verify(dishRequestMapper).toDish(dishRequestDto);
        Mockito.verify(dishServicePort).saveDish(expectedDish);
    }
}
