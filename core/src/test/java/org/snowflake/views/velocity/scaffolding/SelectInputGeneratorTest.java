package org.snowflake.views.velocity.scaffolding;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.snowflake.utils.HtmlWriter;
import org.snowflake.views.velocity.scaffolding.SelectInputGenerator;

public class SelectInputGeneratorTest {

    @Test
    public void testGenerate() {
        HtmlWriter writer = new HtmlWriter();
        SelectInputGenerator generator = new SelectInputGenerator();
        generator.generate(writer, "sampleEnum", "sampleDto", SampleEnum.class);
        assertEquals("<select name=\"sampleEnum\">\n" + "#options($sampleDto.SampleEnum $sampleEnumChoices)"
                + "\n</select>\n", writer.toString());
    }

    enum SampleEnum {
        VALUE1, VALUE2, VALUE3;
    }
}
