package org.snowflake.views.velocity.scaffolding;

import static org.junit.Assert.*;

import org.junit.Test;
import org.snowflake.views.velocity.scaffolding.ScaffoldingViewDecorator;

public class ScaffoldingViewDecoratorTest {

    @Test
    public void testDeduceTemplateFileName() {
        assertEquals("shoppingassistant.edit.vm", ScaffoldingViewDecorator
                .deduceTemplateFileName("/Users/haugeto/org.shoppingassistant.shoppingassistant.edit.vm.auto"));
    }
}
