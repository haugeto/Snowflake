package org.snowflake.argumentinjection;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ArgumentsBuilder {

    final ArgumentProducer producer;

    public ArgumentsBuilder(ArgumentProducer producer) {
        this.producer = producer;
    }

    public Object[] buildArguments(Method method) {
        List<Object> result = new ArrayList<Object>();
        for (Class<?> type : method.getParameterTypes()) {
            result.add(producer.getArgumentOfType(type));
        }
        return result.toArray();
    }

}
