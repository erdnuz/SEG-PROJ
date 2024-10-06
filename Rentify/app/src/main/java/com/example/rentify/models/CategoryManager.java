package com.example.rentify.models;

import java.util.ArrayList;

public class CategoryManager {
    private static ArrayList<String> categories = new ArrayList<>();

    static {
        categories.add("Tools");
        categories.add("Clothing");
        categories.add("Camping Gear");
    }

    public static boolean addCategory(String category) {
        if (!categories.contains(category)) {
            categories.add(category);
            return true;
        }
        return false;
    }

    public static boolean removeCategory(String category) {
        if (categories.size() > 3) {
            return categories.remove(category);
        } else {
            System.out.println("Cannot remove category. There must be at least 3 categories.");
            return false;
        }
    }

    public static ArrayList<String> getCategories() {
        return categories;
    }
}

