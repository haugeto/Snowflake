package org.shoppingassistant.advanced;

import org.shoppingassistant.ShoppingItem;
import org.snowflake.Answer;
import org.snowflake.InputOptions;
import org.snowflake.devserver.DevServer;
import org.snowflake.utils.Console;

/**
 * Demonstrates how to specify the contents of a select box, by using class
 * {@link InputOptions}.
 * 
 * @author haugeto
 */
public class ShoppingAssistantWithCustomSelect extends InterceptedShoppingAssistant {

    public ShoppingItem edit(Answer answer, Integer id, DataAccessObject dataAccessObject) {
        Console.println("edit received: " + dataAccessObject);
        InputOptions descrOptions = new InputOptions("description");
        descrOptions.put("entrecote", "entrecote");
        descrOptions.put("salmon", "salmon");
        descrOptions.put("chicken", "chicken");
        answer.addInputOptions(descrOptions);
        return dataAccessObject.retrieveShoppingItemById(id);
    }

    public static void main(String[] args) throws Exception {
        DevServer devServer = new DevServer("Advanced Shopping Assistant", 3000);
        devServer.addRequestInterceptor(new DaoRequestInterceptor());
        devServer.registerController("shopping", new ShoppingAssistantWithCustomSelect());
        devServer.run();
    }
}
