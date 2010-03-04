package org.shoppingassistant;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.snowflake.Answer;
import org.snowflake.Question;
import org.snowflake.ValidationException;
import org.snowflake.devserver.DevServer;
import org.snowflake.utils.Console;

/**
 * A very simple controller showing the core features of Snowflake. All HTML
 * code seen in a browser when running this application is auto generated. The
 * idea is that you as a web app developer should remain in Java scope for some
 * time during development, before it's time to do final tweaking of HTML code.
 * 
 * When you decide it IS time to tweak the generated HTML code, Snowflake can
 * save the generated template code to a file. Snowflake will discover your
 * custom template file if you follow the
 * "[package name].[class name].[method name].vm" naming convention: For
 * instance, the template for the index method of this class would be in the
 * class path as "org/shoppingassistant/ShoppingAssistant.index.vm".
 * 
 * To look at or save generated template code, type "help" and hit enter in the
 * Java console where Snowflake is running, and follow the instructions.
 * 
 * @author haugeto
 */
public class ShoppingAssistant {

    Map<Integer, ShoppingItem> shoppingItems = new LinkedHashMap<Integer, ShoppingItem>();

    public ShoppingAssistant() {
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
    public Collection<ShoppingItem> index(Question question, Answer answer) {
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
    public void save(Question question, ShoppingItem shoppingItem) {
        Console.println(question.toString());

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
        // Register Controller at base URL "shopping":
        devServer.registerController("shopping", new ShoppingAssistant());
        devServer.run();
    }
}
