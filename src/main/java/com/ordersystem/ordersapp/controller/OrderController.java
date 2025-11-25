package com.ordersystem.ordersapp.controller;

import com.ordersystem.ordersapp.model.Order;
import com.ordersystem.ordersapp.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // GET /orders → obtener todas las órdenes
    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getOrders();
    }

    // GET /orders/{id} → obtener una orden específica
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);

        if (order == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(order);
    }

    // POST /orders → crear orden nueva
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody List<Long> productIds) {
        try {
            Order newOrder = orderService.createOrder(productIds);
            return ResponseEntity.ok(newOrder);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    // DELETE /orders/{id} → eliminar una orden por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderById(@PathVariable Long id) {
        boolean removed = orderService.deleteOrderById(id);

        if (!removed) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    // DELETE /orders → eliminar TODAS las órdenes
    @DeleteMapping
    public ResponseEntity<Void> deleteAllOrders() {
        orderService.deleteAllOrders();
        return ResponseEntity.noContent().build();
    }
}
