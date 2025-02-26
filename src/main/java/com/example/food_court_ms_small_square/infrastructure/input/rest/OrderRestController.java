package com.example.food_court_ms_small_square.infrastructure.input.rest;

import com.example.food_court_ms_small_square.application.dto.request.OrderRequestDto;
import com.example.food_court_ms_small_square.application.dto.response.OrderResponseDto;
import com.example.food_court_ms_small_square.application.dto.response.PageResponseDto;
import com.example.food_court_ms_small_square.application.handler.IOrderHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderRestController {

    private final IOrderHandler orderHandler;

    @Operation(
            summary = "Create a new order",
            description = "Registers a new order in the system."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Order details for registration",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = OrderRequestDto.class)
            )
    )
    @ApiResponse(responseCode = "201", description = "Order successfully created",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = OrderResponseDto.class)))
    @ApiResponse(responseCode = "400", description = "Validation error",
            content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "403", description = "Forbidden - User lacks necessary permissions",
            content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json"))
    @PostMapping("/save")
    @PreAuthorize("hasRole('ROLE_Customer')")
    public ResponseEntity<OrderResponseDto> saveOrder(@RequestBody OrderRequestDto orderRequestDto) {
        OrderResponseDto orderResponseDto = orderHandler.saveOrder(orderRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderResponseDto);
    }


    @Operation(
            summary = "List orders with optional filters",
            description = "Retrieves a paginated list of orders. The list can be filtered by status."
    )
    @ApiResponse(responseCode = "200", description = "Orders retrieved successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Page.class)))
    @ApiResponse(responseCode = "403", description = "Forbidden - User lacks necessary permissions",
            content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json"))
    @GetMapping("/list")
    @PreAuthorize("hasRole('ROLE_Employee')")
    public ResponseEntity<PageResponseDto<OrderResponseDto>> listOrders(
            @RequestParam(required = false) String estado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageResponseDto<OrderResponseDto> orders = orderHandler.listOrdersByFilters(estado, page, size);
        return ResponseEntity.ok(orders);
    }

    @Operation(
            summary = "Assign an order to the authenticated employee",
            description = "Allows an employee to take responsibility for a specific order."
    )
    @ApiResponse(responseCode = "200", description = "Order successfully assigned",
            content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "403", description = "Forbidden - User lacks necessary permissions",
            content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Order not found",
            content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json"))
    @PatchMapping("/{orderId}/assign")
    @PreAuthorize("hasRole('ROLE_Employee')")
    public ResponseEntity<OrderResponseDto> assignOrder(@PathVariable Long orderId) {
        OrderResponseDto orderResponseDto = orderHandler.assignOrder(orderId);
        return ResponseEntity.ok().body(orderResponseDto);
    }

}
