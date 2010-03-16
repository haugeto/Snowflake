package org.shoppingassistant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.snowflake.Answer;
import org.snowflake.Question;

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
        shoppingItem.setId(1);
        shoppingAssistant.shoppingItems.put(1, shoppingItem);
    }

    @Test
    public void indexReturnsAllItems() {
        Collection<ShoppingItem> result = shoppingAssistant.index(new Question(), new Answer());
        assertEquals(1, result.size());
        assertSame(shoppingItem, result.iterator().next());
    }

    @Test
    public void addCreatesNewItem() {
        assertNotNull(shoppingAssistant.add());
    }

    @Test
    public void editReturnsRequestedItem() {
        assertSame(shoppingItem, shoppingAssistant.edit(1));
    }

    @Test
    public void saveReplacesOldItem() {
        ShoppingItem fromForm = new ShoppingItem();
        fromForm.setId(1);
        fromForm.setDescription("new description");
        shoppingAssistant.save(new Question(), fromForm);
        assertNotSame(shoppingItem, shoppingAssistant.shoppingItems.get(1));
        assertEquals("new description", shoppingAssistant.shoppingItems.get(1).getDescription());
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
