package com.emarsys.predict.shop.recommended;

import com.emarsys.predict.shop.shopitems.Item;

import java.util.List;

public abstract class RecommendCompletionHandler {

    public abstract void onRecommendedRequestComplete(List<Item> resultData);
}
