package org.snowflake.views.velocity.scaffolding;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;


import org.junit.Test;
import org.snowflake.Answer;
import org.snowflake.TestDataObject;
import org.snowflake.WebAction;
import org.snowflake.views.velocity.scaffolding.IndexScaffold;

public class IndexScaffoldTest {
    @Test
    public void testGenerate() throws Exception {
        TestDataObject dataObject = new TestDataObject();
        dataObject.setIntField(42);
        dataObject.setStrField("Riesling is better than Chardonnay");
        IndexScaffold indexScaffold = new IndexScaffold();
        Answer answer = new Answer();
        answer.setData(Arrays.asList(dataObject));
        answer.getViewHints().columns("StrField", "IntField");
        answer.getViewHints().addRowAction(new WebAction("/edit", "Edit"));
        answer.getViewHints().addPageAction(new WebAction("/add", "Add"));

        String html = indexScaffold.generate(answer);
        assertTrue(html.contains("<h1>Test Data Objects</h1>"));
        assertTrue(html.contains("<table"));
        assertTrue(html.contains("#foreach($entry in $testDataObjects)"));
        assertTrue(html.contains("<td>$!entry.StrField</td>"));
        assertTrue(html.contains("<td>$!entry.IntField</td>"));
        assertTrue(html.contains("<td><a href=\"/edit/$!entry.Id\">Edit</a> </td>"));
        assertTrue(html.contains("#end"));
        assertTrue(html.contains("<p><a href=\"/add\">Add</a></p>"));
    }
}
