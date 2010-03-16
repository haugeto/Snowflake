package org.shoppingassistant.advanced;

import org.shoppingassistant.ItemCategory;
import org.shoppingassistant.ShoppingItem;

public class ShoppingListItem extends ShoppingItem {

    Integer shoppingListId;

    public ShoppingListItem() {
    }

    public ShoppingListItem(Integer id, Integer shoppingListId, ItemCategory category, String description, int quantity) {
        super(id, category, description, quantity);
        this.shoppingListId = shoppingListId;
    }

    public Integer getShoppingListId() {
        return shoppingListId;
    }

    public void setShoppingListId(Integer shoppingListId) {
        this.shoppingListId = shoppingListId;
    }

}
