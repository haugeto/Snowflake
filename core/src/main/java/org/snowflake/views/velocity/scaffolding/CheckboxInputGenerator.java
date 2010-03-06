package org.snowflake.views.velocity.scaffolding;

import org.apache.commons.lang.StringUtils;
import org.snowflake.views.scaffolding.FormFieldTemplateGenerator;

public class CheckboxInputGenerator implements FormFieldTemplateGenerator {

    @Override
    public boolean accepts(Class<?> fieldType) {
        return fieldType == boolean.class || fieldType == Boolean.class;
    }

    @Override
    public String generate(String fieldName, String dataObjectName, Class<?> dataObjectType) {
        return "#checkbox('" + fieldName + "', $" + dataObjectName + "." + StringUtils.capitalize(fieldName) + ")";
    }
}
