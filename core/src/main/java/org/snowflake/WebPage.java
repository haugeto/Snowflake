package org.snowflake;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.snowflake.utils.HttpHelpers;
import org.snowflake.utils.ReflectionHelpers;

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
    }

    public void createWebMethods(Set<Class<?>> argumentTypesToIgnore) {
        List<WebMethod> webMethods = new ArrayList<WebMethod>();
        for (Method method : getController().getClass().getMethods()) {
            if (WebMethod.isWebMethod(method)) {
                WebMethod.validate(method, argumentTypesToIgnore);
                WebMethod webMethod = new WebMethod(method);
                webMethod.initializeArgs(argumentTypesToIgnore);
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

        moveOverloadsToDefaultMethod(webMethods);

        this.webMethods.addAll(webMethods);
    }

    protected void moveOverloadsToDefaultMethod(List<WebMethod> webMethods) {
        Map<String, Set<WebMethod>> methodMap = new HashMap<String, Set<WebMethod>>();

        for (WebMethod webMethod : webMethods) {
            String name = webMethod.getName();
            if (!methodMap.containsKey(name)) {
                methodMap.put(name, new HashSet<WebMethod>());
            }
            methodMap.get(name).add(webMethod);
        }

        for (String name : methodMap.keySet()) {
            Set<WebMethod> overloadingMethodSet = methodMap.get(name);
            String controllerClass = overloadingMethodSet.iterator().next().getMethod().getDeclaringClass().getName();
            if (overloadingMethodSet.size() > 1) {
                WebMethod defaultMethod = null;
                for (WebMethod webMethod : overloadingMethodSet) {
                    if (!webMethod.hasHttpArg()) {
                        defaultMethod = webMethod;
                        break;
                    }
                }
                if (defaultMethod == null) {
                    throw new SnowflakeException("Cannot deduce default method for overloaded methods \"" + name
                            + "\" (class " + controllerClass + "): There must be one \"" + name
                            + "\" method without custom HTTP arguments");
                }

                overloadingMethodSet.remove(defaultMethod);
                webMethods.removeAll(overloadingMethodSet);
                defaultMethod.addOverloadingMethods(overloadingMethodSet);
            }
        }
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

            if (isIdType(httpArgType)) {
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

    public Question parseRequest(InputStream requestBody, String httpMethod, URI requestURI) throws IOException {
        Question result = createQuestion(httpMethod, requestURI);
        String incomingUrl = requestURI.getPath();
        result.setUrl(incomingUrl);

        // parse URL get parameters, if any
        Map<String, String> urlVariables = new LinkedHashMap<String, String>();
        String urlParameters = requestURI.getQuery();
        if (!StringUtils.isEmpty(urlParameters)) {
            urlVariables.putAll(HttpHelpers.parseHttpParameters(urlParameters, Question.DEFAULT_ENCODING));
            result.setParameters(urlVariables);
            result.setQueryString(urlParameters);
        }
        Map<String, String> httpVariables = new LinkedHashMap<String, String>();
        if (WebMethod.HttpMethod.POST.name().equalsIgnoreCase(httpMethod)) {
            BufferedReader r = new BufferedReader(new InputStreamReader(requestBody));
            String requestBodyContent = r.readLine();
            if (requestBodyContent != null) {
                // TODO: Figure out the clients enc
                httpVariables.putAll(HttpHelpers.parseHttpParameters(requestBodyContent, Question.DEFAULT_ENCODING));
                // FIXME: This overwrites possible URL parameters
                result.setParameters(httpVariables);
            }
        }
        resolveId(result, incomingUrl, urlVariables, httpVariables);
        return result;
    }

    protected void resolveId(Question question, String requestUrl, Map<String, String> urlParams,
            Map<String, String> formData) {
        List<String> candidates = new ArrayList<String>();

        String urlFragment = StringUtils.substringBefore(requestUrl, "?");
        String idStr = StringUtils.substringAfterLast(urlFragment, "/");
        if (idStr.length() > 0)
            candidates.add(idStr);

        if (formData.containsKey("id"))
            candidates.add(urlParams.get("id"));

        if (urlParams.containsKey("id"))
            candidates.add(urlParams.get("id"));

        for (String candidate : candidates)
            try {
                question.setId(Long.parseLong(candidate));
                return;
            } catch (NumberFormatException e) {
            }
    }

    protected Class<?> getIdType() {
        return Long.class;
    }

    protected boolean isIdType(Class<?> type) {
        if (type.isPrimitive()) {
            return getIdType() == ReflectionHelpers.wrapperForPrimitive(type);
        } else {
            return type == getIdType();
        }
    }

    protected Question createQuestion(String httpMethod, URI requestURI) {
        return new Question();
    }
}
