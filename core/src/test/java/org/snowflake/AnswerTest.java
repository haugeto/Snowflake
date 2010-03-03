package org.snowflake;

import static org.junit.Assert.*;

import org.junit.Test;
import org.snowflake.Answer;
import org.snowflake.InputOptions;

public class AnswerTest {

    @Test
    public void testCreateInputOptionsForEnums() {
        Answer answer = new Answer();
        answer.createInputOptionsForEnums(SampleFormData.class);
        assertEquals(1, answer.inputOptions.size());
        InputOptions inputOptions = answer.inputOptions.iterator().next();
        assertEquals("sampleEnum", inputOptions.getFieldName());
        assertTrue(inputOptions.getNameValues().containsKey("CHOICE1"));
        assertTrue(inputOptions.getNameValues().containsKey("CHOICE2"));
    }

    class SampleFormData {

        SampleEnum sampleEnum;

        public SampleEnum getSampleEnum() {
            return sampleEnum;
        }

        public void setSampleEnum(SampleEnum sampleEnum) {
            this.sampleEnum = sampleEnum;
        }

    }

    enum SampleEnum {

        CHOICE1, CHOICE2;

    }

}
