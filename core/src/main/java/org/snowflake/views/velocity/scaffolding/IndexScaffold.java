package org.snowflake.views.velocity.scaffolding;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.lang.StringUtils;
import org.snowflake.Answer;
import org.snowflake.ScaffoldHints;
import org.snowflake.WebAction;
import org.snowflake.views.scaffolding.Scaffold;
import org.snowflake.views.scaffolding.ScaffoldingHelper;
import org.snowflake.views.scaffolding.TableColumn;

public class IndexScaffold implements Scaffold {

    public String generate(Answer answer) throws Exception {
        String title;
        String singularName = "entry";

        ScaffoldHints scaffoldHints = answer.getScaffoldHints();
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
            for (TableColumn columnTitle : scaffoldHints.getTableColumns()) {
                writer.println("<th>" + columnTitle.getTitle() + "</th>");
            }
            if (!scaffoldHints.getRowActions().isEmpty()) {
                writer.println("<th>Actions</th></tr>\n</thead>\n<tbody>");
            }

            writer.println("#foreach($" + singularName + " in $" + answer.getIndexDataName() + ")");
            writer.println("\t<tr>");
            for (TableColumn column : scaffoldHints.getTableColumns()) {
                writer.print("\t\t<td>");
                if (column.hasLink()) {
                    writer.print("<a href=\"" + column.getLink() + "\">"
                            + StringUtils.capitalize(column.getFieldName()) + "</a>");
                } else {
                    writer.print("$!" + singularName + "." + StringUtils.capitalize(column.getFieldName()));
                }
                writer.println("</td>");
            }
            if (!scaffoldHints.getRowActions().isEmpty()) {
                writer.print("\t\t<td>");
                for (WebAction action : scaffoldHints.getRowActions()) {
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
        for (WebAction pageAction : scaffoldHints.getPageActions()) {
            writer.println("<p><a href=\"" + pageAction.getUrl() + "\">" + pageAction.getDescription() + "</a></p>");
        }
        writer.print("</div></div>");
        return result.toString();
    }
}
