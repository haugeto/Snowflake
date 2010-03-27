package org.snowflake.examples;

import java.util.Arrays;
import java.util.Collection;

import org.snowflake.SnowflakeException;
import org.snowflake.devserver.DevServer;

public class Controller {

    public Collection<String> index() {
        return Arrays.asList("Blodstrupmoen", "Ben Reddik", "Emanuell Deperados", "Solan");
    }
    
    public static void main(String[] args) throws SnowflakeException {
        DevServer devServer = new DevServer("Example controller");
        devServer.registerController("/", new Controller());
        devServer.run();
    }
    
}
