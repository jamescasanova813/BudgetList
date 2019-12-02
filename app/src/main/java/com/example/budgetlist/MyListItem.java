package com.example.budgetlist;

public class MyListItem {
    private String itemName;
    private int quantity;

    public MyListItem(String name){
        itemName = name;
        quantity = 1;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getItemName() {
        return itemName;
    }

    public void setQuantity(int amount) {
        quantity += amount;
    }
}
