/**
 * 
 */
package org.shoppingassistant.advanced;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.shoppingassistant.ItemCategory;
import org.shoppingassistant.ShoppingItem;
import org.snowflake.utils.Console;

/**
 * Part of the example in {@link InterceptedShoppingAssistant}. Instances of
 * this class will be passed to controller methods by activating
 * {@link SessionRequestInterceptor} as a request interceptor.
 * 
 * @author haugeto
 */
public class Session {

    static final Map<Integer, ShoppingItem> database = new LinkedHashMap<Integer, ShoppingItem>();

    static {
        // initialize some data:
        database.put(1, new ShoppingItem(1, ItemCategory.MEAT, "Entrecote", 2));
    }

    Map<Integer, ShoppingItem> shoppingItems;

    int id;

    public Session(int id) {
        this.id = id;
        this.shoppingItems = database;
    }

    public void open() {
        Console.println("Session #" + id + " opened");
    }

    public void close() {
        Console.println("Session #" + id + " closed");
    }

    public String toString() {
        return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
    }
}