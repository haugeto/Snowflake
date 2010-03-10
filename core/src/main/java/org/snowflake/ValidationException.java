package org.snowflake;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class ValidationException extends SnowflakeException {

    final Map<String, String> validationMessages = new LinkedHashMap<String, String>();

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException() {
        super("Form validation failed");
    }

    public ValidationException(String fieldName, String message) {
        this();
        invalidateField(fieldName, message);
    }

    public void invalidateField(String fieldName, String message) {
        validationMessages.put(fieldName, message);
    }

    public boolean isInvalidated() {
        return !validationMessages.isEmpty();
    }

    public Map<String, String> getValidationMessages() {
        return new LinkedHashMap<String, String>(this.validationMessages);
    }

    @Override
    public String toString() {
        StringWriter result = new StringWriter();
        PrintWriter writer = new PrintWriter(result);
        if (isInvalidated()) {
            writer.println("Validation Errors:");
            for (String fieldName : validationMessages.keySet())
                writer.println("\t" + fieldName + ": " + validationMessages.get(fieldName));

        } else {
            writer.print("No validation errors");
        }
        return result.toString();
    }

}
