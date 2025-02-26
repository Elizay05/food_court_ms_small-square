package com.example.food_court_ms_small_square.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResponseDto<T> {
    private List<T> content;
    private int totalPages;
    private long totalElements;
}
