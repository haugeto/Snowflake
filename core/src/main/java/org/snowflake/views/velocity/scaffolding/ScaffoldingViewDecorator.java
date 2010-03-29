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
import org.snowflake.views.scaffolding.ScaffoldGenerator;

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
        ScaffoldGenerator scaffoldGenerator = createScaffold(webMethod);
        String scaffoldContent = scaffoldGenerator.generate(question, answer);
        scaffoldContent = "## Snowflake generated Apache Velocity Template\n"
                + "## To continue working on this template, save to\n" + "## <project_source>"
                + System.getProperty("file.separator")
                + StringUtils.substringBeforeLast(answer.getTemplateFile(), AUTO_TEMPLATE_FILENAME_SUFFIX) + "\n\n"
                + scaffoldContent;
        answer.putTemplateVariable("view_scaffold", scaffoldContent);

        String scaffoldName = answer.getTemplateFile() + AUTO_TEMPLATE_FILENAME_SUFFIX;
        answer.setTemplateFile(scaffoldName);

        exchangeTemplateWithVelocityEngine(scaffoldName, scaffoldContent);
        decoratedView.renderView(webMethod, question, answer, out);
    }

    protected void exchangeTemplateWithVelocityEngine(String name, String template) {
        StringResourceRepository repo = StringResourceLoader.getRepository();
        repo.putStringResource(name, template);
    }

    protected ScaffoldGenerator createScaffold(WebMethod webMethod) {
        switch (webMethod.getType()) {
        case UPDATE_FORM:
        case CREATE_FORM:
            return new FormScaffoldGenerator(webMethod.getReturnType(), webApp.getFormFieldTemplateGenerators());
        case SUBMIT:
        case INDEX:
        default:
            return new IndexScaffoldGenerator(webApp);
        }
    }

    static String deduceTemplateFileName(String autoTemplateName) {
        String templateFileName = StringUtils.substringBeforeLast(autoTemplateName, AUTO_TEMPLATE_FILENAME_SUFFIX);
        templateFileName = templateFileName.replace(System.getProperty("file.separator"), ".");
        String[] tokens = templateFileName.split("\\.");
        return StringUtils.join(tokens, '.', tokens.length - 3, tokens.length);
    }

}
