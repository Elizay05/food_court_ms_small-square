package com.example.food_court.infrastructure.input.rest;

import com.example.food_court.application.dto.request.UserRequest;
import com.example.food_court.application.handler.impl.UserHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User-related operations")
public class UserRestController {

    private final UserHandler userHandler;

    @PostMapping("/saveOwner")
    @Operation(
            summary = "Create a new owner",
            description = "Registers a new owner in the system."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "User details for registration",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserRequest.class)
            )
    )
    @ApiResponse(responseCode = "201", description = "Owner successfully created")
    @ApiResponse(responseCode = "400", description = "Validation error",
            content = @Content(mediaType = "application/json"))
    public ResponseEntity<Void> saveOwner(@Valid @RequestBody UserRequest userRequest) {
        userHandler.saveOwner(userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
