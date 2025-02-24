package com.example.food_court_ms_small_square.infrastructure.input.rest;

import com.example.food_court_ms_small_square.application.dto.request.RestaurantRequestDto;
import com.example.food_court_ms_small_square.application.handler.impl.RestaurantHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/restaurant")
@RequiredArgsConstructor
public class RestaurantRestController {

    private final RestaurantHandler restaurantHandler;

    @Operation(
            summary = "Register a new restaurant",
            description = "Creates a new restaurant in the system."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Restaurant details for registration",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RestaurantRequestDto.class)
            )
    )
    @ApiResponse(responseCode = "201", description = "Restaurant successfully created")
    @ApiResponse(responseCode = "400", description = "Validation error",
            content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json"))
    @PostMapping("/save")
    @PreAuthorize("hasRole('ROLE_Administrator')")
    public ResponseEntity<Void> saveObject(@RequestBody @Valid RestaurantRequestDto restaurantRequestDto) {
        restaurantHandler.saveRestaurant(restaurantRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(
            summary = "Validate NIT",
            description = "Retrieves the NIT of the restaurant associated with the authenticated owner."
    )
    @ApiResponse(
            responseCode = "200",
            description = "NIT successfully retrieved",
            content = @Content(mediaType = "application/json", schema = @Schema(type = "string"))
    )
    @ApiResponse(
            responseCode = "403",
            description = "Forbidden - The user does not have permission",
            content = @Content(mediaType = "application/json")
    )
    @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(mediaType = "application/json")
    )
    @GetMapping("/validateNit")
    @PreAuthorize("hasRole('ROLE_Owner')")
    public ResponseEntity<String> validateNit() {
        String nit = restaurantHandler.validateNit();
        return ResponseEntity.ok(nit);
    }
}
