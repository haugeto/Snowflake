package org.snowflake.enterprise;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.snowflake.devserver.DevServer;

/**
 * Use this servlet when you deploy your Snowflake application to test or
 * production. Use {@link DevServer} in your local development environment.
 * <p/>
 * Sample web.xml:
 * 
 * <pre>
 * {@code
 * <web-app>
 *     <display-name>Snowflake Application Production Example</display-name>
 * 
 *     <servlet>
 *         <servlet-name>shopping</servlet-name>
 *         <servlet-class>org.snowflake.enterprise.SnowflakeServlet</servlet-class>
 *         <init-param>
 *             <param-name>controllers</param-name>
 *             <!-- Comma separated list of controller class names -->
 *             <param-value>org.shoppingassistant.ShoppingAssistant</param-value>
 *         </init-param>
 *     </servlet>
 *     <servlet-mapping>
 *         <servlet-name>shopping</servlet-name>
 *         <url-pattern>/</url-pattern>
 *     </servlet-mapping>
 *     <servlet-mapping>
 *         <servlet-name>shopping</servlet-name>
 *         <url-pattern>/static/*</url-pattern>
 *     </servlet-mapping>
 * </web-app>
 * }
 * </pre>
 * 
 * @author haugeto
 */
@SuppressWarnings("serial")
public class SnowflakeServlet extends GenericSnowflakeServlet {

    /**
     * Creates the controller objects by parsing comma separated fully qualified
     * class names given in deployment descriptor.
     * 
     * @throws ServletException
     *             If controller objects cannot be instantiated for some reason
     */
    protected Set<Object> createControllers(ServletConfig config) throws ServletException {
        String controllersParam = config.getInitParameter(PARAM_KEY_CONTROLLERS);
        if (controllersParam == null)
            throw new ServletException("Missing required init-param \"" + PARAM_KEY_CONTROLLERS + "\"");

        Set<Object> controllers = new LinkedHashSet<Object>();
        String[] controllerClassNames = controllersParam.split(",");
        for (String controllerClassName : controllerClassNames) {
            String clazz = controllerClassName.trim();
            Object controller;
            try {
                controller = Class.forName(clazz).newInstance();
            } catch (Exception e) {
                throw new ServletException("Could not load controller of class \"" + clazz + "\"", e);
            }
            controllers.add(controller);
        }
        return controllers;
    }

}
