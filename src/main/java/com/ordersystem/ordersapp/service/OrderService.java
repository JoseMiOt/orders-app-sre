package com.ordersystem.ordersapp.service;

import com.ordersystem.ordersapp.model.Order;
import com.ordersystem.ordersapp.model.Product;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Gauge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final List<Product> products = new ArrayList<>();
    private final List<Order> orders = new ArrayList<>();
    private Long orderCounter = 1L;

    // MÉTRICAS PRINCIPALES
    private final Counter ordersCreatedCounter;
    private final Counter ordersFailedCounter;
    private final Counter productsViewedCounter;
    private final Timer orderCreationTimer;

    // Métricas por producto vendido
    private final Map<Long, Counter> productCounters = new ConcurrentHashMap<>();

    // Gauge: órdenes activas actualmente
    private final Gauge activeOrdersGauge;


    public OrderService(MeterRegistry registry) {

        // Mock de productos
        products.addAll(Arrays.asList(
            new Product(1L, "Laptop", 1200.00),
            new Product(2L, "Teclado Mecánico", 85.50),
            new Product(3L, "Mouse Gamer", 45.00),
            new Product(4L, "Pantalla 27\"", 310.00)
        ));

        // MÉTRICAS
        productsViewedCounter = registry.counter("ordersapp_products_viewed_total");
        ordersCreatedCounter = registry.counter("ordersapp_orders_total");
        ordersFailedCounter  = registry.counter("ordersapp_orders_failed_total");

        orderCreationTimer = registry.timer("ordersapp_order_creation_time_seconds");

        // MÉTRICAS POR PRODUCTO
        for (Product p : products) {
            Counter c = Counter.builder("ordersapp_product_sold_total")
                    .tag("productId", p.getId().toString())
                    .tag("productName", p.getName())
                    .description("Units sold of product")
                    .register(registry);

            productCounters.put(p.getId(), c);
        }

        // GAUGE: Órdenes activas
        activeOrdersGauge = Gauge.builder("ordersapp_active_orders", orders, List::size)
                .description("Current number of active orders")
                .register(registry);
    }

    // GET /products
    public List<Product> getProducts() {
        productsViewedCounter.increment();
        logger.info("Products viewed");
        return products;
    }

    // GET /orders
    public List<Order> getOrders() {
        return orders;
    }

    // GET /orders/{id}
    public Order getOrderById(Long id) {
        return orders.stream()
                .filter(o -> o.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    // DELETE /orders/{id}
    public boolean deleteOrderById(Long id) {
        boolean removed = orders.removeIf(o -> o.getId().equals(id));

        if (removed) {
            logger.info("Order deleted id={}", id);
        } else {
            logger.warn("Attempted to delete non-existent order id={}", id);
        }

        return removed;
    }

    // DELETE /orders (ALL)
    public void deleteAllOrders() {
        logger.info("All orders deleted. Count={}", orders.size());
        orders.clear();
    }

    // POST /orders
    public Order createOrder(List<Long> productIds) {

        return orderCreationTimer.record(() -> {

            // Simular latencia artificial (50–200 ms)
            try {
                Thread.sleep(ThreadLocalRandom.current().nextInt(50, 200));
            } catch (InterruptedException ignored) {}

            List<Product> selectedProducts = new ArrayList<>();
            double total = 0;

            for (Long id : productIds) {

                Product product = products.stream()
                        .filter(p -> p.getId().equals(id))
                        .findFirst()
                        .orElse(null);

                if (product == null) {
                    ordersFailedCounter.increment();
                    logger.warn("Failed to create order: invalid product ID {}", id);
                    throw new IllegalArgumentException("Invalid product ID: " + id);
                }

                selectedProducts.add(product);
                total += product.getPrice();

                // Incrementar métrica por producto vendido
                productCounters.get(id).increment();
            }

            Order order = new Order(orderCounter++, selectedProducts, total);
            orders.add(order);

            ordersCreatedCounter.increment();
            logger.info("Order created id={} total={}", order.getId(), order.getTotal());

            return order;
        });
    }
}
