package org.snowflake.views.velocity.scaffolding;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.snowflake.Answer;
import org.snowflake.Question;
import org.snowflake.TestDataObject;
import org.snowflake.views.scaffolding.FormFieldTemplateGenerator;

public class FormScaffoldTest {

    @Test
    public void testGenerate() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        TestDataObject dataObject = new TestDataObject();
        dataObject.setIntField(42);
        Set<FormFieldTemplateGenerator> generators = new HashSet<FormFieldTemplateGenerator>();
        generators.addAll(Arrays.asList(FormScaffoldGenerator.DEFAULT_GENERATORS));
        FormScaffoldGenerator generator = new FormScaffoldGenerator(TestDataObject.class, generators);
        Answer answer = new Answer();
        answer.setData(dataObject);

        String html = generator.generate(new Question(), answer);
        out.flush();
        assertTrue(html.contains("<form"));
        assertTrue(html.contains("method=\"post\""));
        assertTrue(html.contains("action=\"$postBackUrl\""));
        assertTrue(html.contains("<input type=\"text\" name=\"strField\" value=\"$!testDataObject.StrField\""));
        assertTrue(html.contains("<input type=\"text\" name=\"intField\" value=\"$!testDataObject.IntField\""));
        assertTrue(html.contains("<input type=\"text\" name=\"dateField\" value=\"$!testDataObject.DateField\""));
        assertTrue(html.contains("<input type=\"submit\""));
    }

}
