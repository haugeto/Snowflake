package org.snowflake;

/**
 * Thrown when the framework identifies redundant code, for instance when
 * scaffolding hints are given although no scaffolding takes place.
 * 
 * @author haugeto
 */
@SuppressWarnings("serial")
public class TechnicalDebtException extends SnowflakeException {

    public TechnicalDebtException(String message, Throwable cause) {
        super(message, cause);
    }

    public TechnicalDebtException(String message) {
        super(message);
    }

    public TechnicalDebtException(Throwable cause) {
        super(cause);
    }

}