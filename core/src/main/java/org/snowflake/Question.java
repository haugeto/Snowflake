package org.snowflake;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Represents an HTTP request
 * 
 * @author haugeto
 */
public class Question {

    Integer id = null;

    String url;

    final Map<String, String> parameters = new LinkedHashMap<String, String>();

    final Map<String, Object> attributes = new HashMap<String, Object>();

    public void setAttribute(String name, Object value) {
        this.attributes.put(name, value);
    }

    public Object removeAttribute(String name) {
        return this.attributes.remove(name);
    }

    public void clearAttributes() {
        this.attributes.clear();
    }

    public Map<String, Object> getAttributes() {
        return new HashMap<String, Object>(this.attributes);
    }

    public Map<String, String> getParameters() {
        return new LinkedHashMap<String, String>(parameters);
    }

    public Map<String, Object> getParametersAsObjects() {
        return new LinkedHashMap<String, Object>(parameters);
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
        if (parameters.containsKey("id")) {
            String idStr = parameters.get("id");
            try {
                this.id = Integer.parseInt(idStr);
            } catch (NumberFormatException e) {
            }
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("url", url).append("id", id).append(
                "parameters", parameters).append("attributes", attributes).toString();
    }

}
