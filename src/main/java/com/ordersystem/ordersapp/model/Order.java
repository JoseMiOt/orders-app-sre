package com.ordersystem.ordersapp.model;

import java.util.List;

public class Order {
    private Long id;
    private List<Product> products;
    private double total;

    public Order() {}

    public Order(Long id, List<Product> products, double total) {
        this.id = id;
        this.products = products;
        this.total = total;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public List<Product> getProducts() { return products; }
    public void setProducts(List<Product> products) { this.products = products; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
}
