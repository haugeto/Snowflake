package org.shoppingassistant.advanced;

import org.shoppingassistant.ItemCategory;
import org.shoppingassistant.ShoppingItem;

public class ShoppingListItem extends ShoppingItem {

    Long shoppingListId;

    public ShoppingListItem() {
    }

    public ShoppingListItem(Long id, Long shoppingListId, ItemCategory category, String description, int quantity) {
        super(id, category, description, quantity);
        this.shoppingListId = shoppingListId;
    }

    public Long getShoppingListId() {
        return shoppingListId;
    }

    public void setShoppingListId(Long shoppingListId) {
        this.shoppingListId = shoppingListId;
    }

}
