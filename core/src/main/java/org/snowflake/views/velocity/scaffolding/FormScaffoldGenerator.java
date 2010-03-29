package org.snowflake.views.velocity.scaffolding;

import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.snowflake.Answer;
import org.snowflake.Question;
import org.snowflake.utils.Console;
import org.snowflake.utils.HtmlWriter;
import org.snowflake.utils.ReflectionHelpers;
import org.snowflake.views.scaffolding.FormFieldTemplateGenerator;
import org.snowflake.views.scaffolding.ScaffoldGenerator;
import org.snowflake.views.scaffolding.ScaffoldingHelper;

public class FormScaffoldGenerator implements ScaffoldGenerator {

    public final static FormFieldTemplateGenerator[] DEFAULT_GENERATORS = { new TextInputGenerator(),
            new SelectInputGenerator(), new CheckboxInputGenerator() };

    final Set<FormFieldTemplateGenerator> generators;

    Class<?> dataObjectType;

    public FormScaffoldGenerator(Class<?> dataObjectType, Set<FormFieldTemplateGenerator> generators) {
        if (dataObjectType == null)
            throw new IllegalArgumentException("Cannot auto generate form without knowing the backing data object type");
        this.dataObjectType = dataObjectType;
        this.generators = generators;
    }

    public String generate(Question question, Answer answer) throws Exception {

        HtmlWriter writer = new HtmlWriter(3);

        String title = "Edit " + ScaffoldingHelper.createSingularTitle(dataObjectType);
        writer.tags("<div id=\"hd\">", "<h1>" + title + "</h1>", "</div>");
        writer.startTag("<div id=\"bd\">");
        writer.startTag("<div class=\"yui-g\">");
        writer.startTag("<form method=\"post\" action=\"$postBackUrl\">");
        writer.startTag("<fieldset>");

        String dataObjectName = StringUtils.uncapitalize(dataObjectType.getSimpleName());
        Map<String, Class<?>> publicFields = ReflectionHelpers.publicFields(dataObjectType);
        for (String fieldName : publicFields.keySet()) {
            Class<?> fieldType = publicFields.get(fieldName);
            FormFieldTemplateGenerator generator;
            if (answer.hasInputOptionsForField(fieldName)) {
                generator = new SelectInputGenerator();
            } else {
                generator = resolveGenerator(fieldType);
            }
            if (generator == null) {
                Console.println("Warning: Cannot generate HTML input field for " + fieldName + " (a public field in "
                        + dataObjectType + ")");
                continue;
            }
            writer.startTag("<p>");
            writer.println("<label for=\"" + fieldName + "\">" + fieldName + "</label>");
            generator.generate(writer, fieldName, dataObjectName, fieldType);
            writer.println("<span class=\"validationerror\">$!validationErrors." + fieldName + "</span>");
            writer.endTag("</p>");
        }

        writer.tags("<p>", "<label for=\"submit\">&nbsp;</label>", "<input type=\"submit\" />", "</p>");
        writer.endTag("</fieldset>");
        writer.endTag("</form>");
        writer.endTag("</div>");
        writer.endTag("</div>");
        return writer.toString();
    }

    private FormFieldTemplateGenerator resolveGenerator(Class<?> fieldType) {
        for (FormFieldTemplateGenerator generator : generators)
            if (generator.accepts(fieldType))
                return generator;
        return null;
    }

}
