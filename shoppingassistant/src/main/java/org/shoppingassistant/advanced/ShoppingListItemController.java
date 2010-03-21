package org.shoppingassistant.advanced;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.shoppingassistant.ShoppingAssistant;
import org.snowflake.Answer;
import org.snowflake.InputOptions;
import org.snowflake.Question;
import org.snowflake.ValidationException;
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
public class ShoppingListItemController {

    public Collection<ShoppingListItem> index(Answer answer, Question question, DataAccessObject dataAccessObject) {
        if (question.hasParameter("shoppingListId")) {
            Long shoppingListId;
            try {
                shoppingListId = Long.parseLong(question.getParameter("shoppingListId"));
            } catch (NumberFormatException e) {
                throw new ValidationException("shoppingListId", e.getMessage());
            }
            Set<ShoppingListItem> result = new LinkedHashSet<ShoppingListItem>();
            for (ShoppingListItem item : dataAccessObject.retrieveAllShoppingItems()) {
                if (shoppingListId.equals(item.getShoppingListId())) {
                    result.add(item);
                }
            }
            return result;
        } else {
            return dataAccessObject.retrieveAllShoppingItems();
        }
    }

    public ShoppingListItem add() {
        return new ShoppingListItem();
    }

    public ShoppingListItem edit(Answer answer, Long id, DataAccessObject dataAccessObject) {
        Console.println("edit received: " + dataAccessObject);
        InputOptions shoppingListChoice = new InputOptions("shoppingListId");
        for (ShoppingList list : dataAccessObject.retrieveAllShoppingLists()) {
            shoppingListChoice.put(Long.toString(list.getId()), list.getDescription());
        }
        answer.addInputOptions(shoppingListChoice);
        return dataAccessObject.retrieveShoppingItemById(id);
    }

    public void save(ShoppingListItem shoppingItem, DataAccessObject dataAccessObject) {
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

    public void more(Long id, DataAccessObject dataAccessObject) {
        Console.println("more received: " + dataAccessObject);
        ShoppingListItem shoppingItem = dataAccessObject.retrieveShoppingItemById(id);
        if (shoppingItem != null) {
            shoppingItem.setQuantity(shoppingItem.getQuantity() + 1);
        }
    }

    public void delete(Question question, Long id, DataAccessObject dataAccessObject) {
        Console.println("delete received: " + dataAccessObject);
        dataAccessObject.removeShoppingItem(id);
    }

}
