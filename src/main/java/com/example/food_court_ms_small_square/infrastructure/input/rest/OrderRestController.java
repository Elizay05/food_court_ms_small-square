package com.example.food_court_ms_small_square.infrastructure.input.rest;

import com.example.food_court_ms_small_square.application.dto.request.OrderRequestDto;
import com.example.food_court_ms_small_square.application.dto.request.PinRequestDto;
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

    @Operation(
            summary = "Mark an order as ready",
            description = "Allows an authenticated employee to update an order's status to 'ready'."
    )
    @ApiResponse(responseCode = "200", description = "Order successfully marked as ready",
            content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "403", description = "Forbidden - User lacks necessary permissions",
            content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Order not found",
            content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json"))
    @PatchMapping("/{orderId}/ready")
    @PreAuthorize("hasRole('ROLE_Employee')")
    public ResponseEntity<OrderResponseDto> readyOrder(@PathVariable Long orderId) {
        OrderResponseDto orderResponseDto = orderHandler.readyOrder(orderId);
        return ResponseEntity.ok().body(orderResponseDto);
    }

    @Operation(
            summary = "Mark an order as delivered",
            description = "Allows an authenticated employee to update an order's status to 'delivered' using a PIN."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "PIN required to confirm order delivery",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PinRequestDto.class)
            )
    )
    @ApiResponse(responseCode = "200", description = "Order successfully marked as delivered",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = OrderResponseDto.class)))
    @ApiResponse(responseCode = "400", description = "Invalid PIN provided",
            content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "403", description = "Forbidden - User lacks necessary permissions",
            content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Order not found",
            content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json"))
    @PatchMapping("/{orderId}/delivered")
    @PreAuthorize("hasRole('ROLE_Employee')")
    public ResponseEntity<OrderResponseDto> deliveredOrder(@PathVariable Long orderId, @RequestBody PinRequestDto pinRequestDto) {
        OrderResponseDto orderResponseDto = orderHandler.deliveredOrder(orderId, pinRequestDto.getPin());
        return ResponseEntity.ok().body(orderResponseDto);
    }

    @Operation(
            summary = "Cancel an order",
            description = "Allows an authenticated customer to cancel their order if it has not been prepared."
    )
    @ApiResponse(responseCode = "204", description = "Order successfully canceled")
    @ApiResponse(responseCode = "400", description = "Order cannot be canceled at this stage",
            content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "403", description = "Forbidden - User lacks necessary permissions",
            content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Order not found",
            content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json"))
    @DeleteMapping("/{orderId}")
    @PreAuthorize("hasRole('ROLE_Customer')")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId) {
        orderHandler.cancelOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}
