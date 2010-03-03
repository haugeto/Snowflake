/**
 * 
 */
package org.snowflake.views.velocity.scaffolding;


import org.apache.commons.lang.StringUtils;
import org.snowflake.views.scaffolding.FormFieldTemplateGenerator;

public class SelectInputGenerator implements FormFieldTemplateGenerator {

    @Override
    public String generate(String fieldName, String dataObjectName, Class<?> dataObjectType) {
        String result = "";
        // FIXME: Duplicates logic in VelocityView
        String choicesVariableName = "$" + fieldName + "Choices";
        result += "<select name=\"" + fieldName + "\">\n";
        result += "#options($" + dataObjectName + "." + StringUtils.capitalize(fieldName) + " " + choicesVariableName
                + ")\n";
        result += "</select>";
        return result;
    }

    @Override
    public boolean accepts(Class<?> fieldType) {
        return Enum.class.isAssignableFrom(fieldType);
    }
}