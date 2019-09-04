package com.example.epos.Model;

public class Product {
    private String id;
    private String name;
    private String category;
    private int currentStock;
    private boolean outOfStock;


    public Product(String id, String name, String category, int currentStock, boolean outOfStock) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.currentStock = currentStock;
        this.outOfStock = outOfStock;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(int currentStock) {
        this.currentStock = currentStock;
    }

    public boolean isOutOfStock() {
        return outOfStock;
    }

    public void setOutOfStock(boolean outOfStock) {
        this.outOfStock = outOfStock;
    }
}
