/**
 * 
 */
package org.shoppingassistant.advanced;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.snowflake.utils.Console;

/**
 * Part of the example in {@link InterceptedShoppingAssistant}. Instances of this
 * class will be passed to controller methods by activating
 * {@link SessionRequestInterceptor} as a request interceptor.
 * 
 * @author haugeto
 */
public class Session {

    int id;

    public Session(int id) {
        this.id = id;
    }

    public void open() {
        Console.println("Session #" + id + " opened");
    }

    public void close() {
        Console.println("Session #" + id + " closed");
    }

    public String toString() {
        return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
    }
}