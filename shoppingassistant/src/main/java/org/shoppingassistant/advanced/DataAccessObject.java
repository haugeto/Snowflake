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
 * Part of the example in {@link InterceptedShoppingAssistant}. Instances of
 * this class will be passed to controller methods by activating
 * {@link DaoRequestInterceptor} as a request interceptor. See
 * {@link InterceptedShoppingAssistant#main(String[])}
 * 
 * @author haugeto
 */
public class DataAccessObject {

    static final Map<Integer, ShoppingItem> database = new LinkedHashMap<Integer, ShoppingItem>();

    static {
        // initialize some data:
        database.put(1, new ShoppingItem(1, ItemCategory.MEAT, "Entrecote", 2));
    }

    private Map<Integer, ShoppingItem> shoppingItems;

    int id;

    boolean open = false;

    public DataAccessObject(int id) {
        this.id = id;
        this.shoppingItems = database;
    }

    public Set<ShoppingItem> retrieveAllShoppingItems() {
        return new LinkedHashSet<ShoppingItem>(this.shoppingItems.values());
    }

    public void assertIsOpen() {
        if (!open)
            throw new IllegalStateException("DataAccessObject not opened");
    }

    public ShoppingItem retrieveShoppingItemById(int id) {
        assertIsOpen();
        return this.shoppingItems.get(id);
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
        this.shoppingItems.remove(id);
    }

    public void createOrUpdate(ShoppingItem shoppingItem) {
        assertIsOpen();
        if (shoppingItem == null)
            return;

        if (shoppingItem.getId() == null) {
            int maxValue = 0;
            for (ShoppingItem each : this.shoppingItems.values()) {
                maxValue = Math.max(maxValue, each.getId());
            }
            shoppingItem.setId(maxValue + 1);
        }
        this.shoppingItems.put(shoppingItem.getId(), shoppingItem);
    }

    public String toString() {
        return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
    }
}