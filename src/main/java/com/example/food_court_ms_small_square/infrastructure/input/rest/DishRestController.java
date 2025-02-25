package com.example.food_court_ms_small_square.infrastructure.input.rest;

import com.example.food_court_ms_small_square.application.dto.request.DishRequestDto;
import com.example.food_court_ms_small_square.application.dto.request.UpdateDishRequestDto;
import com.example.food_court_ms_small_square.application.dto.request.UpdateDishStatusRequestDto;
import com.example.food_court_ms_small_square.application.dto.response.DishResponseDto;
import com.example.food_court_ms_small_square.application.handler.IDishHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dish")
@RequiredArgsConstructor
public class DishRestController {

    private final IDishHandler dishHandler;

    @Operation(
            summary = "Register a new dish",
            description = "Creates a new dish in the system."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Dish details for registration",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = DishRequestDto.class)
            )
    )
    @ApiResponse(responseCode = "201", description = "Dish successfully created")
    @ApiResponse(responseCode = "400", description = "Validation error",
            content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "403", description = "Forbidden - User lacks necessary permissions",
            content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json"))
    @PostMapping("/save")
    @PreAuthorize("hasRole('ROLE_Owner')")
    public ResponseEntity<Void> saveDish(@RequestBody @Valid DishRequestDto dishRequestDto) {
        dishHandler.saveDish(dishRequestDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(
            summary = "Update an existing dish",
            description = "Updates the details of an existing dish."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Updated dish details",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UpdateDishRequestDto.class)
            )
    )
    @ApiResponse(responseCode = "202", description = "Dish successfully updated")
    @ApiResponse(responseCode = "400", description = "Validation error",
            content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "403", description = "Forbidden - User lacks necessary permissions",
            content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Dish not found",
            content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json"))
    @PutMapping("/update")
    @PreAuthorize("hasRole('ROLE_Owner')")
    public ResponseEntity<Void> updateDish(@RequestBody @Valid UpdateDishRequestDto updateDishRequestDto) {
        dishHandler.updateDish(updateDishRequestDto);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @Operation(
            summary = "Enable or disable a dish",
            description = "Updates the status of an existing dish by enabling or disabling it."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Status update request (enabled = true to enable, false to disable)",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UpdateDishStatusRequestDto.class)
            )
    )
    @ApiResponse(responseCode = "202", description = "Dish status successfully updated")
    @ApiResponse(responseCode = "400", description = "Validation error",
            content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "403", description = "Forbidden - User lacks necessary permissions",
            content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Dish not found",
            content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json"))
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ROLE_Owner')")
    public ResponseEntity<Void> updateDishStatus(
            @PathVariable Long id,
            @RequestBody @Valid UpdateDishStatusRequestDto updateDishStatusRequestDto
    ) {
        dishHandler.updateDishStatus(id, updateDishStatusRequestDto);
        return ResponseEntity.accepted().build();
    }


    @Operation(
            summary = "List all dishes",
            description = "Returns a paginated and sorted list of dishes"
    )
    @ApiResponse(responseCode = "200", description = "Dishes successfully retrieved")
    @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json"))
    @GetMapping("/list")
    @PreAuthorize("hasRole('ROLE_Customer')")
    public ResponseEntity<Page<DishResponseDto>> listDishes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<DishResponseDto> dishses = dishHandler.listDishes(page, size);
        return ResponseEntity.ok(dishses);
    }
}
