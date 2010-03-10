package org.shoppingassistant.advanced;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.shoppingassistant.ShoppingAssistant;
import org.shoppingassistant.ShoppingItem;
import org.snowflake.Answer;
import org.snowflake.Question;
import org.snowflake.ValidationException;
import org.snowflake.devserver.DevServer;
import org.snowflake.utils.Console;

/**
 * For examples of basic Snowflake features, see {@link ShoppingAssistant}; this
 * class demonstrates how a RequestInterceptor can be used to provide controller
 * methods with custom arguments. In this example, the {@link DataAccessObject}
 * is made available for argument injection by the {@link DaoRequestInterceptor}
 * , and argument injected to all methods specifying it in their method
 * signature.
 * 
 * @author haugeto
 */
public class InterceptedShoppingAssistant {

    public Collection<ShoppingItem> index(Answer answer, Question question, DataAccessObject dataAccessObject) {
        Console.println("index received: " + dataAccessObject);
        answer.setTitle("Welcome!");
        return dataAccessObject.retrieveAllShoppingItems();
    }

    public ShoppingItem add() {
        return new ShoppingItem();
    }

    public ShoppingItem edit(Answer answer, Integer id, DataAccessObject dataAccessObject) {
        Console.println("edit received: " + dataAccessObject);
        return dataAccessObject.retrieveShoppingItemById(id);
    }

    public void save(ShoppingItem shoppingItem, DataAccessObject dataAccessObject) {
        Console.println("Save received: " + dataAccessObject);

        ValidationException validationException = new ValidationException();
        if (shoppingItem.getQuantity() == 0)
            validationException.invalidateField("quantity", "Zero not allowed");
        if (StringUtils.isEmpty(shoppingItem.getDescription()))
            validationException.invalidateField("description", "Cannot be empty");
        if (validationException.isInvalidated())
            throw validationException;

        dataAccessObject.createOrUpdate(shoppingItem);
    }

    public void more(Integer id, DataAccessObject dataAccessObject) {
        Console.println("more received: " + dataAccessObject);
        ShoppingItem shoppingItem = dataAccessObject.retrieveShoppingItemById(id);
        if (shoppingItem != null) {
            shoppingItem.setQuantity(shoppingItem.getQuantity() + 1);
        }
    }

    public void delete(Question question, Integer id, DataAccessObject dataAccessObject) {
        Console.println("delete received: " + dataAccessObject);
        dataAccessObject.removeShoppingItem(id);
    }

    public static void main(String[] args) throws Exception {
        DevServer devServer = new DevServer("Advanced Shopping Assistant", 3000);
        devServer.addRequestInterceptor(new DaoRequestInterceptor());
        devServer.registerController("shopping", new InterceptedShoppingAssistant());
        devServer.run();
    }

}
