package org.snowflake.views.velocity.scaffolding;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;


import org.apache.commons.lang.StringUtils;
import org.snowflake.Answer;
import org.snowflake.SnowflakeException;
import org.snowflake.utils.ReflectionHelpers;
import org.snowflake.views.scaffolding.FormFieldTemplateGenerator;
import org.snowflake.views.scaffolding.Scaffold;
import org.snowflake.views.scaffolding.ScaffoldingHelper;

public class FormScaffold implements Scaffold {

    final static FormFieldTemplateGenerator[] DEFAULT_GENERATORS = { new TextInputGenerator(),
            new SelectInputGenerator() };

    List<FormFieldTemplateGenerator> generators = new ArrayList<FormFieldTemplateGenerator>(Arrays
            .asList(DEFAULT_GENERATORS));

    Class<?> dataObjectType;

    public FormScaffold(Class<?> dataObjectType, Set<FormFieldTemplateGenerator> generators) {
        if (dataObjectType == null)
            throw new IllegalArgumentException("Cannot auto generate form without knowing the backing data object type");
        this.dataObjectType = dataObjectType;
        this.generators.addAll(generators);
    }

    public String generate(Answer answer) throws Exception {
        StringWriter result = new StringWriter();
        PrintWriter writer = new PrintWriter(result);

        String title = "Edit " + ScaffoldingHelper.createSingularTitle(dataObjectType);
        writer.println("<div id=\"hd\" role=\"navigation\">\n<h1>" + title + "</h1>\n</div>");
        writer.println("<div id=\"bd\" role=\"main\">\n<div class=\"yui-g\">");
        writer.println("<form method=\"post\" action=\"$postBackUrl\">");
        writer.println("<fieldset>");

        String dataObjectName = StringUtils.uncapitalize(dataObjectType.getSimpleName());
        Map<String, Class<?>> publicFields = ReflectionHelpers.publicFields(dataObjectType);
        for (String fieldName : publicFields.keySet()) {
            Class<?> fieldType = publicFields.get(fieldName);
            FormFieldTemplateGenerator generator = resolveGenerator(fieldType);
            if (generator == null) {
                throw new SnowflakeException("Cannot generate HTML input field for " + fieldName
                        + " (a public field in " + dataObjectType + ")");
            }
            writer.println("<p><label for=\"" + fieldName + "\">" + fieldName + "</label>"
                    + generator.generate(fieldName, dataObjectName, fieldType)
                    + "<span class=\"validationerror\">$!validationErrors." + fieldName + "</span></p>");
        }

        writer.println("\t<p><label for=\"submit\">&nbsp;</label><input type=\"submit\" /></p>");
        writer.println("</fieldset>");
        writer.println("</form>");
        writer.print("</div>\n</div>");
        return result.toString();
    }

    private FormFieldTemplateGenerator resolveGenerator(Class<?> fieldType) {
        for (FormFieldTemplateGenerator generator : generators)
            if (generator.accepts(fieldType))
                return generator;
        return null;
    }

}
