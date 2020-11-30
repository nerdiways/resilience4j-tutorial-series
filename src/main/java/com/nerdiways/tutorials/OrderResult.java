package com.nerdiways.tutorials;

import java.util.List;

public class OrderResult {

    private final List<Item> items;

    private final String message;

    public OrderResult(List<Item> items, String message){
        this.items = items;
        this.message = message;
    }

    public List<Item> getItems() {
        return items;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        String itemNames = "";

        for(Item item: items){
           itemNames = itemNames.concat(", " + item.getName());
        }
        return "OrderResult{" +
                "items=" + itemNames +
                ", message='" + message + '\'' +
                '}';
    }
}
