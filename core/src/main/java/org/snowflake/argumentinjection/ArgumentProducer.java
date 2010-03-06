package org.snowflake.argumentinjection;

public interface ArgumentProducer {

    public Object getArgumentOfType(Class<?> type);

}
