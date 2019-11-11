package com.example.epos.Models;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class Category extends ExpandableGroup<Product> {
    public Category(String title, List<Product> items) {
        super(title, items);
    }
}
