package org.snowflake.views.velocity;

import java.util.Properties;


import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.snowflake.SnowflakeException;
import org.snowflake.WebApp;
import org.snowflake.WebMethod;
import org.snowflake.views.View;
import org.snowflake.views.ViewFactory;

public class VelocityViewFactory extends ViewFactory {

    public static final String DEFAULT_LAYOUT_TEMPLATE = "org/snowflake/views/velocity/default_layout.vm";

    public static final String SNOWFLAKE_VELOCITY_MACROS = "org/snowflake/views/velocity/snowflake_velocity_macros.vm";

    VelocityEngine velocityEngine;

    public VelocityViewFactory(WebApp devServer) {
        super(devServer);
        this.layoutTemplate = DEFAULT_LAYOUT_TEMPLATE;
    }

    @Override
    public void initialize() {
        velocityEngine = new VelocityEngine();
        Properties props = new Properties();
        props.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogSystem");

        props.setProperty("classpath." + RuntimeConstants.RESOURCE_LOADER + ".class", ClasspathResourceLoader.class
                .getName());

        props.setProperty("string.resource.loader.description", "Velocity StringResource loader");
        props.setProperty("string.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.StringResourceLoader");
        props.setProperty("string.resource.loader.repository.class",
                "org.apache.velocity.runtime.resource.util.StringResourceRepositoryImpl");

        props.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath, string");

        props.setProperty("velocimacro.library", SNOWFLAKE_VELOCITY_MACROS);

        try {
            velocityEngine.init(props);
        } catch (Exception e) {
            throw new SnowflakeException(e);
        }
    }

    @Override
    public View createClientView(WebMethod webMethod) {
        return new VelocityView(velocityEngine, layoutTemplate);
    }

}
