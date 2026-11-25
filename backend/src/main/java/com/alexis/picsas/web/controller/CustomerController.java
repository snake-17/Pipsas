package com.alexis.picsas.web.controller;

import com.alexis.picsas.persistence.entity.CustomerEntity;
import com.alexis.picsas.persistence.entity.OrderEntity;
import com.alexis.picsas.service.CustomerService;
import com.alexis.picsas.service.OrderService;
import jakarta.persistence.OrderBy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerService customerService;
    private final OrderService orderService;

    public CustomerController(CustomerService customerService, OrderService orderService) {
        this.customerService = customerService;
        this.orderService = orderService;
    }
    @GetMapping("/phone/{phone}")
    public ResponseEntity<CustomerEntity> getByPhone(@PathVariable String phone){
        CustomerEntity customer = this.customerService.findByPhone(phone);
        return ResponseEntity.ok(customer);
    }
    @GetMapping("/customer/{id}")
    public ResponseEntity<List<OrderEntity>> getCustomerOrders(@PathVariable String id){
        return ResponseEntity.ok(this.orderService.getCustomerOrders(id));
    }
}
