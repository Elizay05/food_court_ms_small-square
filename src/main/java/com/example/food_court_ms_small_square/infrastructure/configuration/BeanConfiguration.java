package com.example.food_court_ms_small_square.infrastructure.configuration;

import com.example.food_court_ms_small_square.domain.api.IDishServicePort;
import com.example.food_court_ms_small_square.domain.api.IOrderServicePort;
import com.example.food_court_ms_small_square.domain.api.IRestaurantServicePort;
import com.example.food_court_ms_small_square.domain.spi.*;
import com.example.food_court_ms_small_square.domain.usecase.DishUseCase;
import com.example.food_court_ms_small_square.domain.usecase.OrderUseCase;
import com.example.food_court_ms_small_square.domain.usecase.RestaurantUseCase;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.adapter.DishJpaAdapter;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.adapter.OrderJpaAdapter;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.adapter.RestaurantJpaAdapter;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.mapper.IDishEntityMapper;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.mapper.IOrderDishEntityMapper;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.mapper.IOrderEntityMapper;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.mapper.IRestaurantEntityMapper;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.repository.IDishRespository;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.repository.IOrderDishRepository;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.repository.IOrderRepository;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.repository.IRestaurantRepository;
import com.example.food_court_ms_small_square.infrastructure.output.rest.MessageRestAdapter;
import com.example.food_court_ms_small_square.infrastructure.output.rest.TraceabilityRestAdapter;
import com.example.food_court_ms_small_square.infrastructure.output.rest.UserRestAdapter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
public class BeanConfiguration {

    private final IRestaurantRepository restaurantRepository;
    private final IRestaurantEntityMapper restaurantEntityMapper;
    private final IDishEntityMapper dishEntityMapper;
    private final IDishRespository dishRepository;
    private final IOrderRepository orderRepository;
    private final IOrderEntityMapper orderEntityMapper;
    private final IOrderDishEntityMapper orderDishEntityMapper;
    private final IOrderDishRepository orderDishRepository;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public IUserValidationPersistencePort userValidationPersistencePort(RestTemplate restTemplate, HttpServletRequest httpServletRequest) {
        return new UserRestAdapter(restTemplate, httpServletRequest);
    }

    @Bean
    public IMessagePersistencePort messagePersistencePort(RestTemplate restTemplate, HttpServletRequest httpServletRequest) {
        return new MessageRestAdapter(restTemplate, httpServletRequest);
    }

    @Bean
    public ITraceabilityPersistencePort traceabilityPersistencePort(RestTemplate restTemplate, HttpServletRequest httpServletRequest) {
        return new TraceabilityRestAdapter(restTemplate, httpServletRequest);
    }

    @Bean
    public IRestaurantServicePort restaurantServicePort(IUserValidationPersistencePort userValidationPersistencePort) {
        return new RestaurantUseCase(restaurantPersistencePort(), userValidationPersistencePort);
    }

    @Bean
    public IRestaurantPersistencePort restaurantPersistencePort() {
        return new RestaurantJpaAdapter(restaurantRepository, restaurantEntityMapper);
    }

    @Bean
    public IDishPersistencePort dishPersistencePort() {
        return new DishJpaAdapter(dishEntityMapper, dishRepository, restaurantRepository);
    }

    @Bean
    public IDishServicePort dishServicePort() {
        return new DishUseCase(dishPersistencePort());
    }

    @Bean
    public IOrderPersistencePort orderPersistencePort() {
        return new OrderJpaAdapter(orderRepository, orderEntityMapper, orderDishEntityMapper, orderDishRepository, dishRepository, traceabilityPersistencePort(restTemplate(), null));
    }

    @Bean
    public IOrderServicePort orderServicePort(IUserValidationPersistencePort userValidationPersistencePort, IMessagePersistencePort messagePersistencePort) {
        return new OrderUseCase(orderPersistencePort(),  userValidationPersistencePort, messagePersistencePort);
    }
}