package org.snowflake.views.velocity.scaffolding;

import java.io.OutputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.snowflake.Answer;
import org.snowflake.Question;
import org.snowflake.WebApp;
import org.snowflake.WebMethod;
import org.snowflake.views.View;
import org.snowflake.views.scaffolding.Scaffold;

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

    static final String AUTO_TEMPLATE_FILENAME_SUFFIX = ".auto";

    View decoratedView;

    WebApp webApp;

    public ScaffoldingViewDecorator(View decoratedView, WebApp webApp) {
        this.decoratedView = decoratedView;
        this.webApp = webApp;
    }

    @Override
    public void renderView(WebMethod webMethod, Question question, Answer answer, OutputStream out) throws Exception {
        String autoTemplateName = answer.getTemplateFile() + AUTO_TEMPLATE_FILENAME_SUFFIX;
        String autoTemplateContent = null;
        answer.setTemplateFile(autoTemplateName);

        Scaffold scaffold = createScaffold(webMethod);
        autoTemplateContent = scaffold.generate(question, answer);

        autoTemplateContent = "## Snowflake generated Apache Velocity Template\n"
                + "## To continue working on this template, save to\n" + "## <project_source>"
                + System.getProperty("file.separator")
                + StringUtils.substringBeforeLast(answer.getTemplateFile(), AUTO_TEMPLATE_FILENAME_SUFFIX) + "\n\n"
                + autoTemplateContent;

        exchangeTemplateWithVelocityEngine(autoTemplateName, autoTemplateContent);
        decoratedView.renderView(webMethod, question, answer, out);
    }

    protected void exchangeTemplateWithVelocityEngine(String autoTemplateName, String autoTemplateContent) {
        StringResourceRepository repo = StringResourceLoader.getRepository();
        repo.putStringResource(autoTemplateName, autoTemplateContent);
        webApp.setPreviouslyGeneratedScaffold(deduceTemplateFileName(autoTemplateName), autoTemplateContent);
    }

    protected Scaffold createScaffold(WebMethod webMethod) {
        switch (webMethod.getType()) {
        case UPDATE_FORM:
        case CREATE_FORM:
            return new FormScaffold(webMethod.getReturnType(), webApp.getFormFieldTemplateGenerators());
        case SUBMIT:
        case INDEX:
        default:
            return new IndexScaffold(webApp);
        }
    }

    static String deduceTemplateFileName(String autoTemplateName) {
        String templateFileName = StringUtils.substringBeforeLast(autoTemplateName, AUTO_TEMPLATE_FILENAME_SUFFIX);
        templateFileName = templateFileName.replace(System.getProperty("file.separator"), ".");
        String[] tokens = templateFileName.split("\\.");
        return StringUtils.join(tokens, '.', tokens.length - 3, tokens.length);
    }

}
