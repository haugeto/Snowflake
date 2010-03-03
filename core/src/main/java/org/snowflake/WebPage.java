package org.snowflake;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * Created at application startup time. A WebPage represents a client controller
 * class, and has a number of {@link WebMethod WebMethods} invoked on HTTP
 * requests from a browser
 * </p>
 * 
 * 
 * @author haugeto
 */
public class WebPage {

    final Set<WebMethod> webMethods = new LinkedHashSet<WebMethod>();

    WebMethod indexMethod;

    /** The client page object */
    final Object controller;

    final String baseUrl;

    public WebPage(Object controller) {
        this(controller, "");
    }

    public WebPage(Object controller, String baseUrl) {
        this.controller = controller;
        this.baseUrl = baseUrl;
        createWebMethods(baseUrl);
    }

    void createWebMethods(String baseUrl) {
        List<WebMethod> webMethods = new ArrayList<WebMethod>();
        for (Method method : getController().getClass().getMethods()) {
            if (WebMethod.isWebMethod(method)) {
                WebMethod.validate(method);
                WebMethod webMethod = new WebMethod(method);
                webMethod.initializeArgs();
                webMethod.initializeOperation();
                webMethod.initializeUrl(baseUrl);
                webMethods.add(webMethod);
                if (webMethod.getType() == WebMethodType.INDEX)
                    this.indexMethod = webMethod;
            }
        }
        initializeViewMethods(webMethods, this.indexMethod);
        initializeShowSubmitFormCycle(webMethods);
        Collections.sort(webMethods);

        this.webMethods.addAll(webMethods);
    }

    void initializeViewMethods(Collection<WebMethod> webMethods, WebMethod reuseViewMethod) {
        for (WebMethod webMethod : webMethods) {
            if (webMethod.isVoidMethod()) {
                webMethod.setReuseViewMethod(reuseViewMethod);
            }
        }
    }

    void initializeShowSubmitFormCycle(Collection<WebMethod> allWebMethods) {
        for (Iterator<WebMethod> outer = allWebMethods.iterator(); outer.hasNext();) {
            WebMethod webMethod = outer.next();
            if (WebMethodType.UPDATE_FORM == webMethod.getType() || WebMethodType.CREATE_FORM == webMethod.getType()) {
                Class<?> returnType = webMethod.getReturnType();
                if (!Collection.class.isAssignableFrom(returnType)) {
                    for (Iterator<WebMethod> inner = allWebMethods.iterator(); inner.hasNext();) {
                        WebMethod target = inner.next();
                        if (WebMethodType.SUBMIT == target.getType() && returnType.equals(target.getHttpArgType())) {
                            webMethod.setNext(target);
                            break;
                        }
                    }
                }
            }
        }
    }

    public WebMethod getWebMethodByName(String name) {
        for (WebMethod webMethod : this.webMethods) {
            if (webMethod.getName().equals(name)) {
                return webMethod;
            }
        }
        return null;
    }

    public List<WebMethod> pageActionWebMethods() {
        List<WebMethod> result = new ArrayList<WebMethod>();
        for (WebMethod webMethod : webMethods) {
            if (webMethod.getHttpArgType() == null)
                result.add(webMethod);
        }
        return result;
    }

    public List<WebMethod> rowActionWebMethods() {
        List<WebMethod> result = new ArrayList<WebMethod>();
        for (WebMethod webMethod : webMethods) {
            Class<?> httpArgType = webMethod.getHttpArgType();
            if (httpArgType == null)
                continue;

            if (int.class.equals(httpArgType) || Integer.class.equals(httpArgType)) {
                result.add(webMethod);
            }
        }
        return result;
    }

    public WebMethod findPrevious(WebMethod webMethod) {
        WebMethod previous = null;
        for (WebMethod each : webMethods) {
            WebMethod next = each.getNext();
            if (next != null && next.equals(webMethod)) {
                previous = each;
                break;
            }
        }
        return previous;
    }

    public Set<WebMethod> getWebMethods() {
        return new LinkedHashSet<WebMethod>(this.webMethods);
    }

    public Object getController() {
        return controller;
    }

    public String getBaseUrl() {
        return baseUrl;
    }
}
