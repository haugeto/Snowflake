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

        if (answer.hasIndexData()) {
            Class<?> indexType = answer.getIndexDataType();
            if (WebMethod.isBuiltInType(indexType)) {
                answer.setTitle("Result");
                List<ModelObjectAdapter> adapters = new ArrayList<ModelObjectAdapter>();
                for (Object rowObject : answer.getIndexData()) {
                    adapters.add(new ModelObjectAdapter(rowObject));
                }
                answer.setIndexData("Values", adapters);
            }
            if (scaffoldHints.getColumnNames().isEmpty()) {
                scaffoldHints.columns(ReflectionHelpers.publicFieldNames(indexType));
            }

            if (indexType != null)
                initColumnLinks(indexType, scaffoldHints);

            initializeUrls(question, scaffoldHints.getRowActions());
        }
        return buildScaffoldTemplate(answer);
    }

    String buildScaffoldTemplate(Answer answer) {
        ScaffoldHints scaffoldHints = answer.getScaffoldHints();
        String title = answer.getTitle();
        if (title == null)
            title = ScaffoldingHelper.createPluralTitle(answer.getIndexData());
        if (title == null) {
            title = "Empty collection";
        }
        HtmlWriter writer = new HtmlWriter(3);
        writer.tags("<div id=\"hd\">", "<h1>" + title + "</h1>", "</div>");
        writer.startTag("<div id=\"bd\">");
        writer.startTag("<div class=\"yui-g\">");
        if (answer.hasIndexData()) {
            writer.startTag("<table cellpadding=\"4\" border=\"1\">");
            writer.startTag("<thead>");
            writer.startTag("<tr>");
            for (TableColumn columnTitle : scaffoldHints.getTableColumns()) {
                writer.println("<th>" + columnTitle.getTitle() + "</th>");
            }
            if (!scaffoldHints.getRowActions().isEmpty()) {
                writer.println("<th>Actions</th>");
            }
            writer.endTag("</tr>");
            writer.endTag("</thead>");
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
                String cellContents = "<td>";
                for (WebAction action : scaffoldHints.getRowActions()) {
                    // TODO: Infer the ID field more dynamically
                    cellContents += "<a href=\"" + action.getUrl() + "/$!" + singularName + ".Id\">"
                            + action.getDescription() + "</a> ";
                }
                cellContents += "</td>";
                writer.println(cellContents);
            }
            writer.endTag("</tr>");
            writer.println("#end", false);
            writer.endTag("</tbody>");
            writer.endTag("</table>");
        }
        for (WebAction pageAction : scaffoldHints.getPageActions()) {
            writer.startTag("<p>");
            writer.println("<a href=\"" + pageAction.getUrl() + "\">" + pageAction.getDescription() + "</a>");
            writer.endTag("</p>");
        }
        writer.endTag("</div>");

        writer.endTag("</div>");
        return writer.toString();
    }

    void initColumnLinks(Class<?> rowType, ScaffoldHints scaffoldHints) {
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

    void initializeUrls(Question question, Iterable<WebAction> webActions) {
        for (WebAction webAction : webActions) {
            WebPage actionPage = webApp.getWebPageForController(webAction.getController());
            WebMethod actionMethod = actionPage.getWebMethodByName(webAction.getMethodName());
            webAction.setDescription(actionMethod.getName());
            webAction.setUrl(actionMethod.getUrl());
        }
    }
}
