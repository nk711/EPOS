package com.example.epos.Models;

import java.io.Serializable;

public class Product implements Serializable {
    private String barcode;
    private String name;
    private String category;
    private String price;
    private String additional;

    public Product(String barcode, String name, String category, String price) {
        this.barcode = barcode;
        this.name = name;
        this.category = category;
        this.price = price;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getName() {
        return name;
    }

    public String getAdditional() {
        return additional;
    }

    public void setAdditional(String additional) {
        this.additional = additional;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}