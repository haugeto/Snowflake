package org.snowflake;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents an HTTP request
 * 
 * @author haugeto
 */
public class Question {

    Integer id = null;

    String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    final Map<String, String> parameters = new LinkedHashMap<String, String>();

    public Map<String, String> getParameters() {
        return new LinkedHashMap<String, String>(parameters);
    }

    public int nrOfParameters() {
        return parameters.size();
    }

    public List<String> getParameterValues() {
        return new ArrayList<String>(parameters.values());
    }

    public String getParameter(String key) {
        return parameters.get(key);
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters.clear();
        this.parameters.putAll(parameters);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}
