package com.alexis.picsas.web.controller;

import com.alexis.picsas.service.OrderPizzaService;
import com.alexis.picsas.service.dto.CheckoutDto;
import com.alexis.picsas.service.dto.OrderDto;
import com.alexis.picsas.service.dto.OrderItemDto;
import com.alexis.picsas.service.dto.OrderItemResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders-pizza")
public class OrderPizzaController {
    private final OrderPizzaService orderPizzaService;

    public OrderPizzaController(OrderPizzaService orderPizzaService) {
        this.orderPizzaService = orderPizzaService;
    }
    @PostMapping()
    public ResponseEntity<OrderDto> createCart(){
        return ResponseEntity.status(HttpStatus.CREATED).body(this.orderPizzaService.createCart());

    }
    @PostMapping("/{orderId}/items")
    public ResponseEntity<OrderItemResponseDto> addPizzas(@PathVariable int orderId, @Valid @RequestBody OrderItemDto orderItemDto) {
            return ResponseEntity.status(HttpStatus.OK).body(this.orderPizzaService.addPizzaService(orderId,orderItemDto));
    }
    @PatchMapping("/{orderId}/items")
    public ResponseEntity<OrderItemResponseDto> updatePizzaOrder(@PathVariable int orderId, @Valid @RequestBody OrderItemDto orderItemDto){
        return ResponseEntity.status(HttpStatus.OK).body(this.orderPizzaService.updatePizzaService(orderId, orderItemDto));
    }
    @DeleteMapping("/{orderId}/items/{pizzaId}")
    public ResponseEntity<Void> deletePizzaOrder(@PathVariable int orderId, @PathVariable int pizzaId){
        orderPizzaService.deletePizzaService(orderId,pizzaId);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/{orderId}/checkout")
    public ResponseEntity<Void> checkoutPizzaOrder(@PathVariable int orderId, @Valid @RequestBody CheckoutDto checkout){
        orderPizzaService.checkoutPizzaService(orderId, checkout);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/{orderId}/pay")
    public ResponseEntity<Void> payPizzaOrder(@PathVariable int orderId){
        orderPizzaService.payPizzaService(orderId);
        return ResponseEntity.noContent().build();
    }
}
