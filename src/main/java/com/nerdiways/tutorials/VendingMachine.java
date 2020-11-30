package com.nerdiways.tutorials;

import java.util.List;

public class VendingMachine {

    private boolean isPowerOn;

    private boolean isStockAvailable;

    private boolean isDeliveryHandOk;

    private List<Item> items;

    private static final int itemIdTracker = 0;

    public boolean isPowerOn() {
        return isPowerOn;
    }

    public void setPowerOn(boolean powerOn) {
        isPowerOn = powerOn;
    }

    public boolean isStockAvailable() {
        return isStockAvailable;
    }

    public void setStockAvailable(boolean stockAvailable) {
        isStockAvailable = stockAvailable;
    }

    public boolean isDeliveryHandOk() {
        return isDeliveryHandOk;
    }

    public void setDeliveryHandOk(boolean deliveryHandOk) {
        isDeliveryHandOk = deliveryHandOk;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public boolean removeItem(int id){
       return items.removeIf(item -> item.getId() == id);
    }
}
