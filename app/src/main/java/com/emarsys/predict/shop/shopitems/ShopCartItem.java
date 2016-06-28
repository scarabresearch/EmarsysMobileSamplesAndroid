package com.emarsys.predict.shop.shopitems;

public class ShopCartItem {

    private int count = 0;
    private Item item;

    public ShopCartItem(Item item) {
        this.item = item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public void setCount(int i) {
        this.count = i;
    }

    public int getCount() {
        return count;
    }

    public Item getItem() {
        return item;
    }

    public void incrementCount() {
        count++;
    }

    public void decrementCount() {
        count--;
    }
}
