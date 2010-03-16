package org.shoppingassistant;

public class ShoppingItem {

    ItemCategory category;

    Integer id;
    
    Integer quantity = 1;

    String description;

    boolean niceToHave = false;

    public ShoppingItem() {

    }

    public ShoppingItem(Integer id, ItemCategory category, String description, int quantity) {
        this.id = id;
        this.category = category;
        this.description = description;
        this.quantity = quantity;
    }

    public ItemCategory getCategory() {
        return category;
    }

    public void setCategory(ItemCategory category) {
        this.category = category;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean getNiceToHave() {
        return niceToHave;
    }

    public void setNiceToHave(boolean niceToHave) {
        this.niceToHave = niceToHave;
    }

}
