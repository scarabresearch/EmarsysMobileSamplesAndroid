package com.emarsys.predict.shop.shopitems;

import com.emarsys.predict.CartItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cart {

    private Map<String, ShopCartItem> shopCartItems;

    private static Cart shared;

    private Cart() {
    }

    public static Cart sharedCart() {
        if (shared == null) {
            shared = new Cart();
            shared.shopCartItems = new HashMap<>();
        }
        return shared;
    }

    public void addItem(Item item) {
        if (shopCartItems.containsKey(item.getItemID())) {
            ShopCartItem cartItem = shopCartItems.get(item.getItemID());
            cartItem.incrementCount();
        } else {
            ShopCartItem cartItem = new ShopCartItem(item);
            cartItem.incrementCount();
            shopCartItems.put(item.getItemID(), cartItem);
        }
    }

    public void removeItem(Item item) {
        if (shopCartItems.containsKey(item.getItemID())) {
            ShopCartItem cartItem = shopCartItems.get(item.getItemID());
            if (cartItem.getCount() > 1) {
                cartItem.decrementCount();
            } else {
                shopCartItems.remove(cartItem.getItem().getItemID());
            }
        }
    }

    public void removeAllItem(Item item) {
        if (shopCartItems.containsKey(item.getItemID())) {
            ShopCartItem cartItem = shopCartItems.get(item.getItemID());
            shopCartItems.remove(cartItem.getItem().getItemID());
        }
    }

    public List<CartItem> getCartItems() {
        List<CartItem> cartItems = new ArrayList<>();
        for (ShopCartItem sci : shopCartItems.values()) {
            Item item = sci.getItem();
            CartItem cartItem =
                    new CartItem(item.getItemID(), (float) item.getPrice(), sci.getCount());
            cartItems.add(cartItem);
        }
        return cartItems;
    }

    public List<ShopCartItem> getShopCartItems() {
        List<ShopCartItem> cartItems = new ArrayList<>();
        cartItems.addAll(shopCartItems.values());
        return cartItems;
    }

    public void clear() {
        shopCartItems.clear();
    }
}
