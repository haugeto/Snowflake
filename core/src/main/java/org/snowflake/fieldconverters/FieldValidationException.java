package org.snowflake.fieldconverters;

import org.snowflake.SnowflakeException;

@SuppressWarnings("serial")
public class FieldValidationException extends SnowflakeException {

    public FieldValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public FieldValidationException(String message) {
        super(message);
    }

    public FieldValidationException(Throwable cause) {
        super(cause);
    }

}
