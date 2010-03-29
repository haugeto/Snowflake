package org.snowflake.views.scaffolding;

import org.snowflake.utils.HtmlWriter;

public interface FormFieldTemplateGenerator {

    boolean accepts(Class<?> fieldType);

    void generate(HtmlWriter writer, String fieldName, String dataObjectName, Class<?> dataObjectType);

}