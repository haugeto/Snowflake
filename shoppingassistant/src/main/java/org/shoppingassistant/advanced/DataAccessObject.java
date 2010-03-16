/**
 * 
 */
package org.shoppingassistant.advanced;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.shoppingassistant.ItemCategory;
import org.shoppingassistant.ShoppingItem;
import org.snowflake.utils.Console;

/**
 * Part of the example in {@link ShoppingListItemController}. Instances of this
 * class will be passed to controller methods by activating
 * {@link DaoRequestInterceptor} as a request interceptor. See
 * {@link ShoppingListItemController#main(String[])}
 * 
 * @author haugeto
 */
public class DataAccessObject {

    static final Map<Integer, ShoppingList> database = new LinkedHashMap<Integer, ShoppingList>();

    static {
        ShoppingList list1 = new ShoppingList(1, "Dinner party");
        list1.getShoppingListItems().add(new ShoppingListItem(1, 1, ItemCategory.SEAFOOD, "Salmon", 1));
        list1.getShoppingListItems().add(new ShoppingListItem(2, 1, ItemCategory.VEGETABLES, "Carrots", 10));
        database.put(list1.getId(), list1);
        ShoppingList list2 = new ShoppingList(2, "Easter lunch");
        list2.getShoppingListItems().add(new ShoppingListItem(3, 2, ItemCategory.DIARY, "Milk", 5));
        list2.getShoppingListItems().add(new ShoppingListItem(4, 2, ItemCategory.DIARY, "Butter", 1));
        database.put(list2.getId(), list2);
    }

    private Map<Integer, ShoppingList> shoppingLists;

    int id;

    boolean open = false;

    public DataAccessObject(int id) {
        this.id = id;
        this.shoppingLists = database;
    }

    public Set<ShoppingList> retrieveAllShoppingLists() {
        return new LinkedHashSet<ShoppingList>(shoppingLists.values());
    }
    
    public Set<ShoppingListItem> retrieveAllShoppingItems() {
        LinkedHashSet<ShoppingListItem> result = new LinkedHashSet<ShoppingListItem>();
        for (ShoppingList list : this.shoppingLists.values()) {
            result.addAll(list.getShoppingListItems());
        }
        return result;
    }

    public void assertIsOpen() {
        if (!open)
            throw new IllegalStateException("DataAccessObject not opened");
    }

    public ShoppingListItem retrieveShoppingItemById(int id) {
        assertIsOpen();
        for (ShoppingList list : this.shoppingLists.values()) {
            for (ShoppingListItem item : list.getShoppingListItems()) {
                if (item.getId() == id) {
                    return item;
                }
            }
        }
        return null;
    }

    public void open() {
        this.open = true;
        Console.println("Session #" + id + " opened");
    }

    public void close() {
        assertIsOpen();
        this.open = false;
        Console.println("Session #" + id + " closed");
    }

    public void removeShoppingItem(Integer id) {
        assertIsOpen();
        this.shoppingLists.remove(id);
    }

    public void createOrUpdate(ShoppingList shoppingList) {
        if (shoppingList.getId() == null) {
            shoppingList.setId(shoppingLists.size() + 1);
        }
        if (shoppingLists.containsKey(shoppingList.getId()))
            shoppingLists.remove(shoppingList);

        this.shoppingLists.put(shoppingList.getId(), shoppingList);
    }
    
    public void createOrUpdate(ShoppingListItem shoppingItem) {
        assertIsOpen();
        if (shoppingItem == null)
            return;

        if (shoppingItem.getId() == null) {
            int maxValue = 0;
            for (ShoppingItem each : this.retrieveAllShoppingItems()){
                maxValue = Math.max(maxValue, each.getId());
            }
            shoppingItem.setId(maxValue + 1);
        }
        ShoppingList shoppingList = this.shoppingLists.get(shoppingItem.getShoppingListId());
        shoppingList.getShoppingListItems().add(shoppingItem);
    }

    public String toString() {
        return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
    }
}