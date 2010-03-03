package org.snowflake;

@SuppressWarnings("serial")
public class SnowflakeException extends RuntimeException {

    public SnowflakeException(String message) {
        super(message);
    }

    public SnowflakeException(String message, Throwable cause) {
        super(message, cause);
    }

    public SnowflakeException(Throwable cause) {
        super(cause);
    }

}
