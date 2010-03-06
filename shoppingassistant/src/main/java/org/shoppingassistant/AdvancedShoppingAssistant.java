package org.shoppingassistant;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.snowflake.Answer;
import org.snowflake.Question;
import org.snowflake.RequestInterceptor;
import org.snowflake.ValidationException;
import org.snowflake.devserver.DevServer;
import org.snowflake.utils.Console;

public class AdvancedShoppingAssistant {

    Map<Integer, ShoppingItem> shoppingItems = new LinkedHashMap<Integer, ShoppingItem>();

    public AdvancedShoppingAssistant() {
        // Put a sample data object in our "database"
        shoppingItems.put(1, new ShoppingItem(1, ItemCategory.MEAT, "Entrecote", 2));
    }

    /**
     * This method returns a collection, which tells Snowflake to list the
     * returned objects in an HTML table, with links to other operations
     * provided by this controller.
     * 
     * The Snowflake naming convention binds the index method to the base URL of
     * the webapp.
     * 
     * The question- and answer arguments are provided if asked for. This is
     * Snowflake's automatic argument injection. Snowflake can also inject a
     * Map<String, String> argument, which would contain the HTTP query
     * parameters, if any. See more examples of argument injection in methods
     * below.
     */
    public Collection<ShoppingItem> index(Question question, Answer answer, Session session) {
        Console.println("index received: " + session);
        answer.setTitle("Welcome!");
        return shoppingItems.values();
    }

    /**
     * A method with a custom return type will be recognized as a method for
     * rendering an HTML form. The form is populated with the contents of the
     * returned object.
     */
    public ShoppingItem add() {
        ShoppingItem shoppingItem = new ShoppingItem();
        shoppingItem.setId(shoppingItems.size() + 1);
        return shoppingItem;
    }

    /**
     * A method with an Integer parameter and a custom return type will be
     * recognized as an operation for showing the contents of an existing object
     * in an HTML form.
     * 
     * @param id
     *            The Id of the object to edit. It is retrieved from the URL of
     *            the request, e.g. "42" in this URL: /shopping/edit/42
     */
    public ShoppingItem edit(Integer id) {
        return shoppingItems.get(id);
    }

    /**
     * Matched with the add- and edit methods because this method's argument is
     * the same as the return type of the add- and edit methods.
     * 
     * @param shoppingItem
     *            This object has been populated with values from the HTML form.
     *            Basic data validation has already been taken care of as this
     *            method is invoked; business logic data validation takes place
     *            in the save method.
     */
    public void save(Question question, ShoppingItem shoppingItem, Session session) {
        Console.println(question.toString());
        Console.println("Save received: " + session);

        if (shoppingItem.getQuantity() == 0) {
            throw new ValidationException("quantity", "Zero not allowed");
        }
        if (shoppingItem.getId() == null) {
            // Yes, I know. This is a horrible way of generating an id.
            shoppingItem.setId(shoppingItems.size());
        }
        shoppingItems.put(shoppingItem.getId(), shoppingItem);
    }

    /**
     * This method has no return type. This tells Snowflake to reuse the index
     * method for rendering the result of the operation.
     * 
     * @param id
     *            The Id is retrieved from the URL of the request, e.g. "42" in
     *            this URL: /shopping/more/42
     */
    public void more(Integer id) {
        if (shoppingItems.containsKey(id)) {
            ShoppingItem shoppingItem = shoppingItems.get(id);
            shoppingItem.setQuantity(shoppingItem.getQuantity() + 1);
        }
    }

    /**
     * The fact that this method (and the "more" method above) has an Integer
     * parameter, tells Snowflake that the method performs an operation
     * associated with a particular object, identified by the id argument.
     * Hence, a link to this operation is provided for each object in the table
     * rendered by the index method.
     * 
     * @param question
     *            Injected argument with information about the request
     * @param id
     *            Id of object, who's delete link was clicked, retrieved from
     *            the URL
     */
    public void delete(Question question, Integer id) {
        Console.println(question.toString());
        shoppingItems.remove(id);
    }

    /**
     * Your Snowflake application's starting point.
     */
    public static void main(String[] args) throws Exception {
        DevServer devServer = new DevServer("Shopping Assistant", 3000);
        devServer.addRequestInterceptor(new RequestInterceptorExample());
        // Register Controller at base URL "shopping":
        devServer.registerController("shopping", new ShoppingAssistant());
        devServer.run();
    }

    static class RequestInterceptorExample implements RequestInterceptor {

        @Override
        public void after(Question question, Answer answer, Object object) throws Exception {
            Console.println("After: " + question + ": " + object);
        }

        @Override
        public Object before(Question question, Answer answer) throws Exception {
            return new Session("session1");
        }

    }

    static class Session {

        String name;

        public Session(String name) {
            this.name = name;
        }

        public String toString() {
            return new ReflectionToStringBuilder(this).toString();
        }
    }

}
