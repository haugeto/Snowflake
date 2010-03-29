/**
 * 
 */
package org.snowflake.views.velocity.scaffolding;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.snowflake.utils.HtmlWriter;
import org.snowflake.utils.ReflectionHelpers;
import org.snowflake.views.scaffolding.FormFieldTemplateGenerator;

public class TextInputGenerator implements FormFieldTemplateGenerator {

    public static final Set<Class<?>> ACCEPTED_TYPES;

    static {
        ACCEPTED_TYPES = new HashSet<Class<?>>();
        ACCEPTED_TYPES.add(String.class);
        ACCEPTED_TYPES.add(Date.class);
        ACCEPTED_TYPES.addAll(ReflectionHelpers.PRIMITIVES_TO_WRAPPERS.keySet());
        ACCEPTED_TYPES.addAll(ReflectionHelpers.PRIMITIVES_TO_WRAPPERS.values());
        ACCEPTED_TYPES.remove(Boolean.class); // handled by check box
        ACCEPTED_TYPES.remove(boolean.class); // handled by check box
    }

    @Override
    public void generate(HtmlWriter writer, String fieldName, String dataObjectName, Class<?> dataObjectType) {
        writer.println("<input type=\"text\" name=\"" + fieldName + "\" value=\"$!" + dataObjectName + "."
                + StringUtils.capitalize(fieldName) + "\"/>");
    }

    @Override
    public boolean accepts(Class<?> fieldType) {
        for (Class<?> accepted : ACCEPTED_TYPES)
            if (accepted.isAssignableFrom(fieldType))
                return true;
        return false;
    }

}