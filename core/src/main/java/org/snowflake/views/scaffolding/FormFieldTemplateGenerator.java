package org.snowflake.views.scaffolding;

public interface FormFieldTemplateGenerator {

    boolean accepts(Class<?> fieldType);

    String generate(String fieldName, String dataObjectName, Class<?> dataObjectType);
    
}