package org.snowflake.views.velocity.scaffolding;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;


import org.apache.commons.lang.StringUtils;
import org.snowflake.Answer;
import org.snowflake.ViewHints;
import org.snowflake.WebAction;
import org.snowflake.views.scaffolding.Scaffold;
import org.snowflake.views.scaffolding.ScaffoldingHelper;

public class IndexScaffold implements Scaffold {

    public String generate(Answer answer) throws Exception {
        String title;
        String pluralName = null;
        String singularName = "entry";
        final List<String> headers = new ArrayList<String>();

        ViewHints viewHints = answer.getViewHints();
        if (answer.hasIndexData()) {
            headers.addAll(viewHints.getColumnNames());
            pluralName = answer.getIndexDataName();
        }
        title = ScaffoldingHelper.createPluralTitle(answer.getIndexData());
        if (title == null) {
            title = "Empty collection";
        }
        StringWriter result = new StringWriter();
        PrintWriter writer = new PrintWriter(result);
        writer.println("<div id=\"hd\" role=\"navigation\"><h1>" + title + "</h1></div>");
        writer.println("<div id=\"bd\" role=\"main\"><div class=\"yui-g\">");
        if (answer.hasIndexData()) {
            writer.println("<table cellpadding=\"4\" border=\"1\">");
            writer.println("<thead>\n<tr>");
            for (String columnTitle : headers) {
                writer.println("<th>" + columnTitle + "</th>");
            }
            if (!viewHints.getRowActions().isEmpty()) {
                writer.println("<th>Actions</th></tr>\n</thead>\n<tbody>");
            }

            writer.println("#foreach($" + singularName + " in $" + pluralName + ")");
            writer.println("\t<tr>");
            for (String key : headers) {
                writer.println("\t\t<td>$!" + singularName + "." + StringUtils.capitalize(key) + "</td>");
            }
            if (!viewHints.getRowActions().isEmpty()) {
                writer.print("\t\t<td>");
                for (WebAction action : viewHints.getRowActions()) {
                    // TODO: Infer the ID field more dynamically
                    writer.print("<a href=\"" + action.getUrl() + "/$!" + singularName + ".Id\">"
                            + action.getDescription() + "</a> ");
                }
                writer.println("</td>");
            }
            writer.println("\t</tr>");
            writer.println("#end");

            writer.print("</tbody>\n</table>");
        }
        for (WebAction pageAction : viewHints.getPageActions()) {
            writer.println("<p><a href=\"" + pageAction.getUrl() + "\">" + pageAction.getDescription() + "</a></p>");
        }
        writer.print("</div></div>");
        return result.toString();
    }
}
