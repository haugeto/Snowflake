package org.snowflake;

public class WebAction {

    Object controller;

    String methodName;

    String url;

    String description;

    public WebAction(Object controller, String methodName) {
        this.controller = controller;
        this.methodName = methodName;
    }

    public WebAction(String url, String description) {
        this.url = url;
        this.description = description;
    }

    public Object getController() {
        return controller;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
