package org.snowflake.views.scaffolding;

import org.snowflake.WebMethod;

public class Scaffold {

    final String name;

    final String content;

    final WebMethod webMethod;

    public Scaffold(String name, String content, WebMethod webMethod) {
        this.name = name;
        this.content = content;
        this.webMethod = webMethod;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public WebMethod getWebMethod() {
        return webMethod;
    }

}
