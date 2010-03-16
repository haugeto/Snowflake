package org.shoppingassistant.advanced;

import java.util.LinkedHashSet;
import java.util.Set;

import org.snowflake.devserver.DevServer;

public class ShoppingPlanner {

    public ShoppingPlanner() {
    }

    public Set<ShoppingList> index(DataAccessObject dao) {
        return new LinkedHashSet<ShoppingList>(dao.retrieveAllShoppingLists());
    }

    public ShoppingList add() {
        return new ShoppingList();
    }

    public void save(ShoppingList shoppingList, DataAccessObject dao) {
        dao.createOrUpdate(shoppingList);
    }

    public static void main(String[] args) throws Exception {
        DevServer devServer = new DevServer("Shopping planner");
        devServer.addRequestInterceptor(new DaoRequestInterceptor());
        devServer.registerController("lists", new ShoppingPlanner());
        devServer.registerController("items", new ShoppingListItemController());
        devServer.run();
    }

}
