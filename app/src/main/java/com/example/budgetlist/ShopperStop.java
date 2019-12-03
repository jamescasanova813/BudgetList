package com.example.budgetlist;

import java.util.ArrayList;

public class ShopperStop {
    private String storeName;
    private float totalCost;

    ShopperStop(String name, float total){
        storeName = name;
        totalCost = total;
    }

    public float getTotalCost() {
        return totalCost;
    }

    public String getStoreName() {
        return storeName;
    }
}
