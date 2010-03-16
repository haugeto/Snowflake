package org.snowflake.views.velocity.scaffolding;

import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.snowflake.Answer;
import org.snowflake.Question;
import org.snowflake.ScaffoldHints;
import org.snowflake.WebAction;
import org.snowflake.WebApp;
import org.snowflake.WebMethod;
import org.snowflake.WebPage;
import org.snowflake.utils.ReflectionHelpers;
import org.snowflake.views.View;
import org.snowflake.views.scaffolding.Scaffold;
import org.snowflake.views.scaffolding.TableColumn;

/**
 * <p>
 * Intercepts view rendering and auto generates the Velocity template code
 * before the normal view logic is run.
 * <p>
 * <p>
 * The auto generated template is exchanged with the Velocity engine using a
 * {@link StringResourceRepository}
 * </p>
 * 
 * @author haugeto
 */
public class ScaffoldingViewDecorator implements View {

    View decoratedView;

    WebApp webApp;

    public ScaffoldingViewDecorator(View decoratedView, WebApp webApp) {
        this.decoratedView = decoratedView;
        this.webApp = webApp;
    }

    @Override
    public void renderView(WebMethod webMethod, Question question, Answer answer, OutputStream out) throws Exception {
        ScaffoldHints scaffoldHints = answer.getScaffoldHints();
        initializeUrls(question, scaffoldHints.getPageActions());

        String autoTemplateName = answer.getTemplateFile() + ".auto";
        String autoTemplateContent = null;
        answer.setTemplateFile(autoTemplateName);

        Scaffold scaffold;
        switch (webMethod.getType()) {
        case UPDATE_FORM:
        case CREATE_FORM:
            scaffold = new FormScaffold(webMethod.getReturnType(), webApp.getFormFieldTemplateGenerators());
            break;
        case SUBMIT:
        case INDEX:
        default:
            if (answer.hasIndexData()) {
                Collection<?> c = answer.getIndexData();
                if (!c.isEmpty()) {
                    Object firstObject = c.iterator().next();
                    if (scaffoldHints.getColumnNames().isEmpty()) {
                        scaffoldHints.columns(ReflectionHelpers.publicFieldNames(firstObject));
                    }
                    Class<?> rowType = firstObject.getClass();
                    if (rowType != null)
                        initColumnLinks(rowType, scaffoldHints);
                }
                initializeUrls(question, scaffoldHints.getRowActions());
            }
            scaffold = new IndexScaffold();
            break;
        }
        autoTemplateContent = scaffold.generate(answer);

        autoTemplateContent = "## Snowflake generated Apache Velocity Template\n"
                + "## To continue working on this template, save to\n" + "## <project_source>"
                + System.getProperty("file.separator")
                + StringUtils.substringBeforeLast(answer.getTemplateFile(), ".auto") + "\n\n" + autoTemplateContent;

        StringResourceRepository repo = StringResourceLoader.getRepository();
        repo.putStringResource(autoTemplateName, autoTemplateContent);
        webApp.setPreviouslyGeneratedScaffold(deduceTemplateFileName(autoTemplateName), autoTemplateContent);
        decoratedView.renderView(webMethod, question, answer, out);
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

    static String deduceTemplateFileName(String autoTemplateName) {
        String templateFileName = StringUtils.substringBeforeLast(autoTemplateName, ".auto");
        templateFileName = templateFileName.replace(System.getProperty("file.separator"), ".");
        String[] tokens = templateFileName.split("\\.");
        return StringUtils.join(tokens, '.', tokens.length - 3, tokens.length);
    }

}
