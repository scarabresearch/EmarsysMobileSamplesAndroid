package com.emarsys.predict.shop.recommended;

import com.emarsys.predict.shop.shopitems.Item;

import java.util.HashMap;
import java.util.List;

public abstract class RecommendListCompletionHandler {

    public abstract void onRecommendedRequestComplete(List<String> categories,
                                                      HashMap<String, List<Item>> data);
}
