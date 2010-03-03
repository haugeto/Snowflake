package org.snowflake.views;

import org.snowflake.WebApp;
import org.snowflake.WebMethod;
import org.snowflake.views.velocity.scaffolding.ScaffoldingViewDecorator;

/**
 * Abstract factory for view creation. Views are either auto generated or
 * handled by client defined views. The logic for delegating to the right view
 * is handled here.
 * 
 * @author haugeto
 */
public abstract class ViewFactory {

    final WebApp devServer;
    
    protected String layoutTemplate;
    
    public ViewFactory(WebApp devServer) {
        this.devServer = devServer;
    }

    public abstract void initialize();

    /** Implemented according to view technology in use (e.g. Velocity) */
    public abstract View createClientView(WebMethod webMethod);


    public View createView(WebMethod webMethod) {
        if (webMethod.hasViewTemplateFile()) {
            return createClientView(webMethod);
        } else {
            return new ScaffoldingViewDecorator(createClientView(webMethod), devServer);
        }
    }

    public void setLayoutTemplate(String layoutTemplate) {
        this.layoutTemplate = layoutTemplate;
    }

}