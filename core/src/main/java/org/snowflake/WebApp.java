package org.snowflake;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.snowflake.fieldconverters.FieldConverter;
import org.snowflake.utils.Console;
import org.snowflake.views.ViewFactory;
import org.snowflake.views.scaffolding.FormFieldTemplateGenerator;
import org.snowflake.views.velocity.VelocityViewFactory;

/**
 * A WebApp has a number of pages which in turn have methods, bound to URLs.
 * Page objects can be of any type, and are registered with
 * {@link WebApp#registerController(String, Object)}.
 * <p />
 * Ideas:
 * <ul>
 * <li />Introduce a notion of Feature Set, as in MMF?
 * </ul>
 * Backlog:
 * <ul>
 * <li />TODO: Interceptors before and after handling a HTTP request
 * <li />TODO: Support boolean types with checkboxes
 * <li />TODO: Pluggable mechanism for determining what field is an ID (must be
 * based on type introspection and variable name)
 * </ul>
 * 
 * @author haugeto
 */
public class WebApp {

    protected final Map<Object, WebPage> webPages = new LinkedHashMap<Object, WebPage>();

    protected final Set<FormFieldTemplateGenerator> formFieldTemplateGenerators = new LinkedHashSet<FormFieldTemplateGenerator>();

    protected final List<FieldConverter> fieldConverters = new ArrayList<FieldConverter>();

    protected ViewFactory viewFactory = new VelocityViewFactory(this);

    protected String name;

    public WebApp() {
        this("Snowflake");
    }

    public WebApp(String name) {
        this.name = name;
        this.fieldConverters.addAll(Arrays.asList(FieldConverter.DEFAULT_CONVERTERS));
    }

    /**
     * Register a page as a part of the web application run by this Server.
     * 
     * @param url
     *            Suffix for URLs to be handled by the given controller page
     * @param controller
     *            The client object to handle incoming HTTP requests. NB:
     *            Overloaded methods not supported yet.
     */
    public void registerController(String url, Object controller) {
        if (controller == null) {
            throw new IllegalArgumentException("Cannot map URL \"" + url + "\" to a null reference");
        }
        if (url == null || "/".equals(url))
            url = "";
        if (url.length() > 0 && !url.startsWith("/")) {
            url = "/" + url;
        }
        if (url.startsWith("/static")) {
            throw new IllegalArgumentException("URL prefix \"/static\" is reserved for static content");
        }

        WebPage webPage = new WebPage(controller, url);
        this.webPages.put(controller, webPage);
    }

    public void setViewFactory(ViewFactory viewFactory) {
        this.viewFactory = viewFactory;
    }

    public void setLayoutTemplate(String classPathResource) {
        this.viewFactory.setLayoutTemplate(classPathResource);
    }

    public WebPage getWebPageForController(Object controller) {
        for (WebPage webPage : webPages.values()) {
            if (webPage.getController().equals(controller)) {
                return webPage;
            }
        }
        throw new IllegalArgumentException("Unregistered controller: " + controller);
    }

    public WebPage getWebPageByName(String name) {
        return this.webPages.get(name);
    }

    public Map<Object, WebPage> getWebPages() {
        return new LinkedHashMap<Object, WebPage>(this.webPages);
    }

    public void setPreviouslyGeneratedScaffold(String name, String content) {
        Console.put("previouslyGeneratedScaffold.name", name);
        Console.put("previouslyGeneratedScaffold.content", content);
    }

    public void add(FormFieldTemplateGenerator generator) {
        formFieldTemplateGenerators.add(generator);
    }

    public Set<FormFieldTemplateGenerator> getFormFieldTemplateGenerators() {
        return new LinkedHashSet<FormFieldTemplateGenerator>(this.formFieldTemplateGenerators);
    }

    public void addFieldConverter(FieldConverter fieldConverter) {
        this.fieldConverters.add(fieldConverter);
    }

    public List<FieldConverter> getFieldConverters() {
        return fieldConverters;
    }

    protected FieldConverter findConverterForType(Class<?> type) {
        for (FieldConverter fieldConverter : this.fieldConverters) {
            if (fieldConverter.accepts(type))
                return fieldConverter;
        }
        return null;
    }

    public WebRequest createWebRequest(WebPage webPage, WebMethod webMethod, Question question, Answer answer) {
        return new WebRequest(this, webPage, webMethod, question, answer);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

}