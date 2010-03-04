package org.shoppingassistant;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.snowflake.Answer;
import org.snowflake.Question;
import org.snowflake.RequestInterceptor;
import org.snowflake.ValidationException;
import org.snowflake.devserver.DevServer;

public class ShoppingAssistant {

    Map<Integer, ShoppingItem> shoppingItems = new LinkedHashMap<Integer, ShoppingItem>();

    public ShoppingAssistant() {
        shoppingItems.put(1, new ShoppingItem(1, ItemCategory.MEAT, "Entrecote", 2));
    }

    public Collection<ShoppingItem> index() {
        return shoppingItems.values();
    }

    public ShoppingItem edit(Integer id) {
        return shoppingItems.get(id);
    }

    public void more(Integer id) {
        if (shoppingItems.containsKey(id)) {
            ShoppingItem shoppingItem = shoppingItems.get(id);
            shoppingItem.setQuantity(shoppingItem.getQuantity() + 1);
        }
    }

    public void less(Integer id) {
        if (shoppingItems.containsKey(id)) {
            ShoppingItem shoppingItem = shoppingItems.get(id);
            if (shoppingItem.getQuantity() > 1)
                shoppingItem.setQuantity(shoppingItem.getQuantity() - 1);
        }
    }

    public ShoppingItem add() {
        ShoppingItem shoppingItem = new ShoppingItem();
        shoppingItem.setId(shoppingItems.size() + 1);
        return shoppingItem;
    }

    public void delete(Integer id) {
        shoppingItems.remove(id);
    }

    public void save(ShoppingItem shoppingItem) {
        if (shoppingItem.getQuantity() == 0) {
            throw new ValidationException("quantity", "Zero not allowed");
        }
        if (shoppingItem.getId() == null) {
            // Obviously, this is a horrible way of generating an id:
            shoppingItem.setId(shoppingItems.size());
        }
        shoppingItems.put(shoppingItem.getId(), shoppingItem);
    }

    public static void main(String[] args) throws Exception {
        DevServer devServer = new DevServer("Shopping Assistant", 3000);
        devServer.addRequestInterceptor(new RequestInterceptor() {
            @Override
            public void before(Question question, Answer answer) throws Exception {
                System.out.println("RequestInterceptor.Before: " + question);
            }

            @Override
            public void after(Question question, Answer answer) throws Exception {
                System.out.println("RequestInterceptor.After: " + answer);
            }
        });
        devServer.registerController("shopping", new ShoppingAssistant());
        devServer.run();
    }
}
