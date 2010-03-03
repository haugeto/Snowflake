package org.snowflake;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.snowflake.utils.ReflectionHelpers;


/**
 * Represents the answer a web controller emits to the client on an HTTP
 * request.
 * 
 * @author haugeto
 */
public class Answer {

    public static final String DEFAULT_CONTENT_TYPE = "text/html";

    final Map<String, Object> templateVariables = new HashMap<String, Object>();

    final Set<InputOptions> inputOptions = new HashSet<InputOptions>();

    final ViewHints viewHints = new ViewHints();

    /** Field values will be displayed in HTML form */
    Map<String, String> formData;

    Map<String, String> validationErrors;

    Class<?> formDataType;

    /** Objects will be listed in HTML table */
    Collection<?> indexData;

    String indexDataName;

    String formDataName;

    String contentType = DEFAULT_CONTENT_TYPE;

    Date lastModified;

    String templateFile;

    String viewForMethod;

    Long contentLength;

    Integer httpCode = WebRequest.HTTP_OK;

    String nextUrl;

    boolean layoutDecorated = true;

    public Answer() {
    }

    public void putAll(Map<String, Object> templateVariables) {
        this.templateVariables.putAll(templateVariables);
    }

    public boolean hasIndexData() {
        return this.indexData != null && !this.indexData.isEmpty();
    }

    public boolean hasFormData() {
        return this.formData != null;
    }

    public void addInputOptions(InputOptions inputOptions) {
        this.inputOptions.add(inputOptions);
    }

    protected void setInputOptions(Collection<InputOptions> inputOptionsCollection) {
        this.inputOptions.clear();
        this.inputOptions.addAll(inputOptionsCollection);
    }

    public void setData(Object value) {
        if (value == null)
            return;

        if (value.getClass().isArray()) {
            value = new ArrayList<Object>(Arrays.asList((Object[]) value));
        }

        if (value instanceof Collection<?>) {
            Collection<?> c = (Collection<?>) value;
            if (!c.isEmpty()) {
                setIndexData(StringUtils.uncapitalize(c.iterator().next().getClass().getSimpleName()) + "s", c);
            }
        } else {
            setFormData(value);
        }
    }

    public void setIndexData(String name, Collection<?> indexData) {
        this.indexDataName = name;
        this.indexData = indexData;
    }

    private void setFormData(Object formData) {
        this.formData = ReflectionHelpers.fieldValues(formData);
        setFormDataType(formData.getClass());
    }

    void createInputOptionsForEnums(Class<?> formDataType) {
        Map<String, Class<?>> publicFields = ReflectionHelpers.publicFields(formDataType);
        for (String name : publicFields.keySet()) {
            Class<?> fieldType = publicFields.get(name);
            if (Enum.class.isAssignableFrom(fieldType)) {
                addInputOptions(new InputOptions(name, ReflectionHelpers.enumValues(fieldType)));
            }
        }
    }

    public Map<String, String> getFormData() {
        return formData;
    }

    public void put(String name, Object value) {
        templateVariables.put(name, value);
    }

    public Map<String, Object> getTemplateVariables() {
        return new HashMap<String, Object>(templateVariables);
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        if (contentType == null)
            throw new IllegalArgumentException("content type cannot be null");
        this.contentType = contentType;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public void useViewForMethod(String string) {
        this.viewForMethod = string;
    }

    public String getViewForMethod() {
        return viewForMethod;
    }

    public String getTemplateFile() {
        return templateFile;
    }

    public void setTemplateFile(String templateFile) {
        this.templateFile = templateFile;
    }

    public void setContentLength(Long length) {
        this.contentLength = length;
    }

    public Long getContentLength() {
        return contentLength;
    }

    public void setHttpCode(Integer httpCode) {
        this.httpCode = httpCode;
    }

    public Integer getHttpCode() {
        return httpCode;
    }

    public Collection<?> getIndexData() {
        return this.indexData;
    }

    public ViewHints getViewHints() {
        return viewHints;
    }

    public boolean isLayoutDecorated() {
        return layoutDecorated;
    }

    public void setLayoutDecorated(boolean layoutDecorated) {
        this.layoutDecorated = layoutDecorated;
    }

    public void addRowAction(Object controller, String methodName) {
        viewHints.addRowAction(controller, methodName);
    }

    public void columns(String... columnNames) {
        viewHints.columns(columnNames);
    }

    public String getNextUrl() {
        return nextUrl;
    }

    public void setNextUrl(String postbackUrl) {
        this.nextUrl = postbackUrl;
    }

    public Set<InputOptions> getInputOptions() {
        return new HashSet<InputOptions>(this.inputOptions);
    }

    public String getIndexDataName() {
        return indexDataName;
    }

    public String getFormDataName() {
        return formDataName;
    }

    protected void setFormDataType(Class<?> type) {
        this.formDataType = type;
        createInputOptionsForEnums(type);
        this.formDataName = StringUtils.uncapitalize(type.getSimpleName());
    }

    protected void setFormData(Map<String, String> formData) {
        this.formData = formData;
    }

    public Map<String, String> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(Map<String, String> validationErrors) {
        this.validationErrors = validationErrors;
    }

}
