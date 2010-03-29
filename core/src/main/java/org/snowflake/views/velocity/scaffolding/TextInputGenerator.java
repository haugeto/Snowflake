/**
 * 
 */
package org.snowflake.views.velocity.scaffolding;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.snowflake.utils.HtmlWriter;
import org.snowflake.views.scaffolding.FormFieldTemplateGenerator;

public class TextInputGenerator implements FormFieldTemplateGenerator {

    static final Class<?>[] ACCEPTED_TYPES = { String.class, Integer.class, Date.class };

    List<Class<?>> acceptedTypes = Arrays.asList(ACCEPTED_TYPES);

    @Override
    public void generate(HtmlWriter writer, String fieldName, String dataObjectName, Class<?> dataObjectType) {
        writer.println("<input type=\"text\" name=\"" + fieldName + "\" value=\"$!" + dataObjectName + "."
                + StringUtils.capitalize(fieldName) + "\"/>");
    }

    @Override
    public boolean accepts(Class<?> fieldType) {
        return acceptedTypes.contains(fieldType);
    }

}