package com.example.food_court_ms_small_square.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateDishStatusRequestDto {
    @NotNull
    private Boolean enabled;
}
