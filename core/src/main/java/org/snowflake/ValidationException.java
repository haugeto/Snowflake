package org.snowflake;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class ValidationException extends SnowflakeException {

    final Map<String, String> errorMessages = new LinkedHashMap<String, String>();

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException() {
        super("Form validation failed");
    }

    public ValidationException(String fieldName, String message) {
        this();
        putErrorMessage(fieldName, message);
    }

    public void putErrorMessage(String fieldName, String message) {
        errorMessages.put(fieldName, message);
    }

    public boolean hasValidationErrors() {
        return !errorMessages.isEmpty();
    }

    public Map<String, String> getErrorMessages() {
        return new LinkedHashMap<String, String>(this.errorMessages);
    }

    @Override
    public String toString() {
        StringWriter result = new StringWriter();
        PrintWriter writer = new PrintWriter(result);
        if (hasValidationErrors()) {
            writer.println("Validation Errors:");
            for (String fieldName : errorMessages.keySet())
                writer.println("\t" + fieldName + ": " + errorMessages.get(fieldName));

        } else {
            writer.print("No validation errors");
        }
        return result.toString();
    }

}
