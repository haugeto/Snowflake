package org.snowflake;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.snowflake.devserver.DevServer;
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

    final ScaffoldHints scaffoldHints = new ScaffoldHints();

    /** Field values will be displayed in HTML form */
    Map<String, Object> formData;

    Map<String, String> validationMessages;

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

    String title;

    String footer;

    Integer httpCode = WebRequest.HTTP_OK;

    String nextUrl;

    boolean layoutDecorated = true;

    String viewCss;

    boolean autoGenerated = true;

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

    public Set<InputOptions> getInputOptions() {
        return new HashSet<InputOptions>(this.inputOptions);
    }

    public boolean hasInputOptionsForField(String fieldName) {
        for (InputOptions inputOptions : this.inputOptions) {
            if (fieldName.equals(inputOptions.getFieldName())) {
                return true;
            }
        }
        return false;
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

    public void setFormData(Object formData) {
        this.formData = ReflectionHelpers.fieldValues(formData);
        setFormDataType(formData.getClass());
    }

    protected void setFormData(Map<String, Object> formData, Class<?> formDataType) {
        this.formData = formData;
        setFormDataType(formDataType);
    }

    private void setFormDataType(Class<?> type) {
        this.formDataType = type;
        this.formDataName = StringUtils.uncapitalize(type.getSimpleName());
        createInputOptionsForEnums(type);
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

    public Map<String, String> createHeaders() {
        Map<String, String> headers = new LinkedHashMap<String, String>();
        headers.put("Server", DevServer.SERVER_NAME);
        headers.put("Content-Type", getContentType());
        if (getContentLength() != null) {
            headers.put("Content-Length", Long.toString(getContentLength()));
        }
        if (getLastModified() != null) {
            headers.put("Last-Modified", getLastModified().toString());
        }
        headers.put("Date", new Date().toString());
        return headers;
    }

    public Map<String, Object> getFormData() {
        return new LinkedHashMap<String, Object>(formData);
    }

    public void putTemplateVariable(String name, Object value) {
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

    public ScaffoldHints getScaffoldHints() throws TechnicalDebtException {
        if (isAutoGenerated())
            return scaffoldHints;
        else
            throw new TechnicalDebtException("No Scaffold view will for this answer; ScaffoldHints can't help you");
    }

    public boolean isLayoutDecorated() {
        return layoutDecorated;
    }

    public void setLayoutDecorated(boolean layoutDecorated) {
        this.layoutDecorated = layoutDecorated;
    }

    public void addRowAction(Object controller, String methodName) {
        getScaffoldHints().addRowAction(controller, methodName);
    }

    public void columns(String... columnNames) {
        getScaffoldHints().columns(columnNames);
    }

    public String getNextUrl() {
        return nextUrl;
    }

    public void setNextUrl(String postbackUrl) {
        this.nextUrl = postbackUrl;
    }

    public String getIndexDataName() {
        return indexDataName;
    }

    public Class<?> getIndexDataType() {
        if (indexData == null || indexData.isEmpty())
            return null;

        Object firstObject = indexData.iterator().next();
        if (firstObject == null)
            return null;

        return firstObject.getClass();
    }

    public String getFormDataName() {
        return formDataName;
    }

    public Map<String, String> getValidationMessages() {
        return validationMessages;
    }

    public void setValidationMessages(Map<String, String> validationMessages) {
        this.validationMessages = validationMessages;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }

    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("httpCode", this.httpCode).append(
                "contentType", this.contentType).append("formDataType", this.formDataType).append("indexDataObjects",
                this.hasIndexData() ? this.indexData.size() : 0).append("templateFile", this.templateFile).append(
                "title", this.title).append("templateVariables", this.templateVariables).toString();
    }

    public String getViewCss() {
        return viewCss;
    }

    public void setViewCss(String viewCss) {
        this.viewCss = viewCss;
    }

    public boolean isAutoGenerated() {
        return autoGenerated;
    }

    public void setAutoGenerated(boolean autoGenerated) {
        this.autoGenerated = autoGenerated;
    }

    public String getFooter() {
        return footer;
    }

}
