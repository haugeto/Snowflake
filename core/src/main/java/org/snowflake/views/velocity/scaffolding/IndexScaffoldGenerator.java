package org.snowflake.views.velocity.scaffolding;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.snowflake.Answer;
import org.snowflake.Question;
import org.snowflake.ScaffoldHints;
import org.snowflake.WebAction;
import org.snowflake.WebApp;
import org.snowflake.WebMethod;
import org.snowflake.WebPage;
import org.snowflake.utils.HtmlWriter;
import org.snowflake.utils.ReflectionHelpers;
import org.snowflake.views.scaffolding.ScaffoldGenerator;
import org.snowflake.views.scaffolding.ScaffoldingHelper;
import org.snowflake.views.scaffolding.TableColumn;

public class IndexScaffoldGenerator implements ScaffoldGenerator {

    final WebApp webApp;

    public IndexScaffoldGenerator(WebApp webApp) {
        this.webApp = webApp;
    }

    public String generate(Question question, Answer answer) throws Exception {
        ScaffoldHints scaffoldHints = answer.getScaffoldHints();
        initializeUrls(question, scaffoldHints.getPageActions());

        Class<?> indexType = answer.getIndexDataType();
        if (indexType != null) {
            if (WebMethod.isBuiltInType(indexType)) {
                List<ModelObjectAdapter> adapters = new ArrayList<ModelObjectAdapter>();
                for (Object rowObject : answer.getIndexData()) {
                    adapters.add(new ModelObjectAdapter(rowObject));
                }
                answer.setIndexData("Values", adapters);
            }
            if (scaffoldHints.getColumnNames().isEmpty())
                scaffoldHints.columns(ReflectionHelpers.publicFieldNames(indexType));
            initColumnLinks(indexType, scaffoldHints);
            initializeUrls(question, scaffoldHints.getRowActions());
        }
        return buildScaffoldTemplate(answer);
    }

    protected String buildScaffoldTemplate(Answer answer) {
        HtmlWriter writer = new HtmlWriter(3);

        String title = answer.getTitle();
        if (title == null)
            title = ScaffoldingHelper.createPluralTitle(answer.getIndexDataType());
        if (title == null)
            title = "Empty collection";

        writer.startEndTags("<div id=\"hd\">", "<h1>" + title + "</h1>", "</div>");
        writer.startTags("<div id=\"bd\">", "<div class=\"yui-g\">");

        ScaffoldHints scaffoldHints = answer.getScaffoldHints();
        if (answer.hasIndexData()) {
            writer.startTags("<table cellpadding=\"4\" border=\"1\">", "<thead>", "<tr>");
            for (TableColumn columnTitle : scaffoldHints.getTableColumns()) {
                writer.println("<th>" + columnTitle.getTitle() + "</th>");
            }
            if (!scaffoldHints.getRowActions().isEmpty()) {
                writer.println("<th>Actions</th>");
            }
            writer.endTags("</tr>", "</thead>");
            writer.startTag("<tbody>");

            String singularName = "entry";
            writer.println("#foreach($" + singularName + " in $" + answer.getIndexDataName() + ")", false);
            writer.startTag("<tr>");
            for (TableColumn column : scaffoldHints.getTableColumns()) {
                String cellContents = "<td>";
                if (column.hasLink()) {
                    cellContents += "<a href=\"" + column.getLink() + "\">"
                            + StringUtils.capitalize(column.getFieldName()) + "</a>";
                } else {
                    cellContents += "$!" + singularName + "." + StringUtils.capitalize(column.getFieldName());
                }
                cellContents += "</td>";
                writer.println(cellContents);
            }
            if (!scaffoldHints.getRowActions().isEmpty()) {
                writer.startTag("<td>");
                for (WebAction action : scaffoldHints.getRowActions()) {
                    // TODO: Infer the ID field more dynamically
                    writer.println("<a href=\"" + action.getUrl() + "/$!" + singularName + ".Id\">"
                            + action.getDescription() + "</a>");
                }
                writer.endTag("</td>");
            }
            writer.endTag("</tr>");
            writer.println("#end", false);
            writer.endTags("</tbody>", "</table>");
        }
        for (WebAction pageAction : scaffoldHints.getPageActions()) {
            writer.startEndTags("<p>", "<a href=\"" + pageAction.getUrl() + "\">" + pageAction.getDescription()
                    + "</a>", "</p>");
        }
        writer.endTags("</div>", "</div>");
        return writer.toString();
    }

    protected void initColumnLinks(Class<?> rowType, ScaffoldHints scaffoldHints) {
        Map<String, Class<?>> fields = ReflectionHelpers.publicFields(rowType);
        for (String fieldName : fields.keySet()) {
            Class<?> fieldType = fields.get(fieldName);
            if (Set.class.isAssignableFrom(fieldType)) {
                String collectionTypeName = rowType.getPackage().getName() + "."
                        + StringUtils.capitalize(fieldName.substring(0, fieldName.length() - 1));
                Class<?> type;
                try {
                    type = Class.forName(collectionTypeName);
                } catch (ClassNotFoundException e) {
                    // convention attempt unsuccessful. No link can be
                    // generated.
                    return;
                }

                WebMethod indexMethod = webApp.indexMethodForType(type);
                if (indexMethod != null) {
                    TableColumn tableColumn = scaffoldHints.getTableColumnByFieldName(fieldName);
                    String paramName = StringUtils.uncapitalize(rowType.getSimpleName());
                    tableColumn.setLink(indexMethod.getUrl() + "?" + paramName + "Id=$!entry.Id");
                }
            }
        }
    }

    protected void initializeUrls(Question question, Iterable<WebAction> webActions) {
        for (WebAction webAction : webActions) {
            WebPage actionPage = webApp.getWebPageForController(webAction.getController());
            WebMethod actionMethod = actionPage.getWebMethodByName(webAction.getMethodName());
            webAction.setDescription(actionMethod.getName());
            webAction.setUrl(actionMethod.getUrl());
        }
    }
}
