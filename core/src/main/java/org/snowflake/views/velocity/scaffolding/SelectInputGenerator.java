/**
 * 
 */
package org.snowflake.views.velocity.scaffolding;

import org.apache.commons.lang.StringUtils;
import org.snowflake.utils.HtmlWriter;
import org.snowflake.views.scaffolding.FormFieldTemplateGenerator;

public class SelectInputGenerator implements FormFieldTemplateGenerator {

    @Override
    public void generate(HtmlWriter writer, String fieldName, String dataObjectName, Class<?> dataObjectType) {
        // FIXME: Duplicates logic in VelocityView
        String choicesVariableName = "$" + fieldName + "Choices";
        writer.startTag("<select name=\"" + fieldName + "\">");
        writer.println("#options($" + dataObjectName + "." + StringUtils.capitalize(fieldName) + " "
                + choicesVariableName + ")", false);
        writer.endTag("</select>");
    }

    @Override
    public boolean accepts(Class<?> fieldType) {
        return Enum.class.isAssignableFrom(fieldType);
    }
}