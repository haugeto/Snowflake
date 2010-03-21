package org.shoppingassistant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.snowflake.Answer;
import org.snowflake.Question;
import org.snowflake.ValidationException;

/**
 * Unit tests for {@link ShoppingAssistant}
 * 
 * @author haugeto
 */
public class ShoppingAssistantTest {

    ShoppingAssistant shoppingAssistant;
    ShoppingItem shoppingItem;

    @Before
    public void setup() {
        shoppingAssistant = new ShoppingAssistant();
        shoppingItem = new ShoppingItem();
        shoppingItem.setId(1L);
        shoppingAssistant.shoppingItems.put(1L, shoppingItem);
    }

    @Test
    public void indexReturnsAllItems() {
        Collection<ShoppingItem> result = shoppingAssistant.index(new Question(), new Answer());
        assertEquals(1, result.size());
        assertSame(shoppingItem, result.iterator().next());
    }

    @Test
    public void addCreatesNewItem() {
        assertNull(shoppingAssistant.add().getId());
    }

    @Test
    public void editReturnsRequestedItem() {
        assertSame(shoppingItem, shoppingAssistant.edit(1));
    }

    @Test
    public void saveReplacesOldItem() {
        ShoppingItem fromForm = new ShoppingItem();
        fromForm.setId(1L);
        fromForm.setDescription("new description");
        shoppingAssistant.save(new Question(), fromForm);
        assertNotSame(shoppingItem, shoppingAssistant.shoppingItems.get(1));
        assertEquals("new description", shoppingAssistant.shoppingItems.get(1).getDescription());
    }

    @Test(expected = ValidationException.class)
    public void saveInvalidatesZeroQuantity() {
        ShoppingItem fromForm = new ShoppingItem();
        fromForm.setQuantity(0);
        shoppingAssistant.save(new Question(), fromForm);
    }

    @Test(expected = ValidationException.class)
    public void saveInvalidatesEmptyDescription() {
        ShoppingItem fromForm = new ShoppingItem();
        fromForm.setDescription("");
        shoppingAssistant.save(new Question(), fromForm);
    }

    @Test
    public void moreIncreasesQuantity() {
        shoppingAssistant.more(1);
        assertEquals(2, (int) shoppingAssistant.shoppingItems.get(1).getQuantity());
    }

    @Test
    public void deleteRemovesItem() {
        shoppingAssistant.delete(new Question(), 1);
        assertTrue(shoppingAssistant.shoppingItems.isEmpty());
    }

}
