package org.snowflake.views.velocity.scaffolding;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.snowflake.views.velocity.scaffolding.SelectInputGenerator;

public class SelectInputGeneratorTest {

    @Test
    public void testGenerate() {
        String result = new SelectInputGenerator().generate("sampleEnum", "sampleDto", SampleEnum.class);
        assertEquals("<select name=\"sampleEnum\">\n" + "#options($sampleDto.SampleEnum $sampleEnumChoices)\n"
                + "</select>", result);
    }

    enum SampleEnum {
        VALUE1, VALUE2, VALUE3;
    }
}
