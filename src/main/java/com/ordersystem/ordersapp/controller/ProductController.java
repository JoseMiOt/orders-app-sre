package com.ordersystem.ordersapp.controller;

import com.ordersystem.ordersapp.model.Product;
import com.ordersystem.ordersapp.service.OrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProductController {

    private final OrderService orderService;

    public ProductController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/products")
    public List<Product> getProducts() {
        return orderService.getProducts();
    }
}
