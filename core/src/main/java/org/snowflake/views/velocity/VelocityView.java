/**
 * 
 */
package org.snowflake.views.velocity;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.snowflake.Answer;
import org.snowflake.InputOptions;
import org.snowflake.Question;
import org.snowflake.WebMethod;
import org.snowflake.utils.CollectionHelpers;
import org.snowflake.views.View;

public class VelocityView implements View {

    final VelocityEngine velocityEngine;

    final String layoutTemplate;

    public VelocityView(VelocityEngine velocityEngine, String layoutTemplate) {
        this.velocityEngine = velocityEngine;
        this.layoutTemplate = layoutTemplate;
    }

    @Override
    public void renderView(WebMethod webMethod, Question question, Answer answer, OutputStream out) throws Exception {
        VelocityContext viewContext = createViewContext(answer);
        Template template = null;
        template = velocityEngine.getTemplate(answer.getTemplateFile());

        Writer viewContent;
        if (answer.isLayoutDecorated()) {
            viewContent = new StringWriter();
        } else {
            viewContent = new BufferedWriter(new OutputStreamWriter(out));
        }
        template.merge(viewContext, viewContent);
        viewContent.flush();

        if (answer.isLayoutDecorated()) {
            Template mainTemplate = velocityEngine.getTemplate(layoutTemplate);
            OutputStreamWriter mainContent = new OutputStreamWriter(out);
            VelocityContext layoutContext = new VelocityContext(viewContext);
            layoutContext.put("view_css", answer.getViewCss());
            layoutContext.put("view_content", viewContent.toString());
            layoutContext.put("view_title", answer.getTitle());
            mainTemplate.merge(layoutContext, mainContent);
            mainContent.flush();
        }
    }

    protected VelocityContext createViewContext(Answer answer) throws Exception {
        VelocityContext result = new VelocityContext();
        Map<String, Object> templateVariables = answer.getTemplateVariables();
        for (String name : templateVariables.keySet()) {
            result.put(name, templateVariables.get(name));
        }
        if (answer.getValidationMessages() != null) {
            result.put("validationErrors", answer.getValidationMessages());
        }
        if (answer.getNextUrl() != null) {
            result.put("postBackUrl", answer.getNextUrl());
        }
        if (answer.hasFormData()) {
            Map<String, Object> fieldValues = new LinkedHashMap<String, Object>(answer.getFormData());
            CollectionHelpers.capitalizeKeys(fieldValues);
            result.put(answer.getFormDataName(), fieldValues);
        }
        if (answer.hasIndexData()) {
            result.put(answer.getIndexDataName(), answer.getIndexData());
        }
        for (InputOptions inputOptions : answer.getInputOptions()) {
            result.put(inputOptions.getFieldName() + "Choices", inputOptions.getNameValues());
        }
        return result;
    }
}