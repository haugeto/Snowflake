package org.snowflake;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.snowflake.utils.ReflectionHelpers;
import org.snowflake.views.View;

/**
 * <p>
 * Created at application startup time. Provides the server with meta data
 * needed to process an incoming HTTP request.
 * </p>
 * 
 * <p>
 * The actual delegation from a HTTP request takes place in {@link WebRequest}
 * </p>
 * 
 * @author haugeto
 */
public class WebMethod implements Comparable<WebMethod> {

    public enum HttpMethod {
        GET, POST
    };

    public static final Set<Class<?>> BUILT_IN_TYPES = new HashSet<Class<?>>();

    static {
        BUILT_IN_TYPES.add(String.class);
        BUILT_IN_TYPES.add(Map.class);
        BUILT_IN_TYPES.addAll(ReflectionHelpers.PRIMITIVES_TO_WRAPPERS.keySet());
        BUILT_IN_TYPES.addAll(ReflectionHelpers.PRIMITIVES_TO_WRAPPERS.values());
    }

    public static final String DEFAULT_METHOD_NAME = "index";

    final Set<WebMethod> overloadingMethods = new HashSet<WebMethod>();

    WebMethodType type;

    /** Typically the submit method in a show-form-submit-form cycle */
    WebMethod next;

    /** Set to reuse other WebMethod to render the result of this */
    WebMethod reuseViewMethod = null;

    String url;

    HttpMethod httpMethod = HttpMethod.GET;

    Method method;

    boolean hasHttpArg;

    boolean hasViewTemplateFile;

    Class<?> httpArgType;

    String templateFileName;

    View view;

    public WebMethod(Method method) {
        this.method = method;
        this.templateFileName = resolveTemplateFileName(method.getDeclaringClass());
        this.hasViewTemplateFile = getClass().getClassLoader().getResource(this.templateFileName) != null;
    }

    protected String resolveTemplateFileName(Class<?> controllerClass) {
        String className = controllerClass.getName().toLowerCase();
        String methodName = getName();
        return className.replace(".", System.getProperty("file.separator")) + "." + methodName + ".vm";
    }

    public boolean isVoidMethod() {
        return method.getReturnType() == Void.TYPE;
    }

    public void initializeArgs(Set<Class<?>> argTypesToIgnore) {
        List<Class<?>> parameterTypes = new ArrayList<Class<?>>(Arrays.asList(method.getParameterTypes()));
        parameterTypes.remove(Question.class);
        parameterTypes.remove(Answer.class);
        if (argTypesToIgnore != null) {
            parameterTypes.removeAll(argTypesToIgnore);
        }
        if (!parameterTypes.isEmpty()) {
            hasHttpArg = true;
            this.httpArgType = parameterTypes.get(0);
            if (!isBuiltInType(this.httpArgType)) {
                this.httpMethod = HttpMethod.POST;
            }
        }
    }

    public static boolean isBuiltInType(Class<?> t) {
        if (t.isPrimitive())
            return true;

        for (Class<?> argType : BUILT_IN_TYPES)
            if (argType == t)
                return true;

        return false;
    }

    // TODO: Consider making method validate non-static. Tradeoffs?
    public static void validate(Method method, Set<Class<?>> argumentTypesToIgnore) {
        if (argumentTypesToIgnore == null)
            argumentTypesToIgnore = new HashSet<Class<?>>();

        argumentTypesToIgnore.add(Question.class);
        argumentTypesToIgnore.add(Answer.class);

        boolean foundSimpleType = false;
        boolean foundNonSimpleType = false;
        for (int i = 0; i < method.getParameterTypes().length; i++) {
            Class<?> type = method.getParameterTypes()[i];
            if (isBuiltInType(type))
                foundSimpleType = true;
            else if (!argumentTypesToIgnore.contains(type))
                foundNonSimpleType = true;
        }
        if (foundSimpleType && foundNonSimpleType) {
            throw new SnowflakeException("Method \"" + method.getName() + "\" of " + method.getDeclaringClass()
                    + " has both simple types and self defined types as arguments "
                    + "(we wouldn't know how to invoke this method)");
        }

    }

    public static boolean isWebMethod(Method m) {
        return (!m.getDeclaringClass().equals(Object.class) && !(Modifier.isStatic(m.getModifiers())) && !(m.getName()
                .startsWith("get") || m.getName().startsWith("set")));
    }

    public String getTemplateFileName() {
        return templateFileName;
    }

    public String getName() {
        return method.getName();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public boolean hasViewTemplateFile() {
        return this.hasViewTemplateFile;
    }

    public WebMethodType getType() {
        return type;
    }

    public Method getMethod() {
        return this.method;
    }

    public void setType(WebMethodType type) {
        this.type = type;
    }

    public boolean hasHttpArg() {
        return this.hasHttpArg;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
        if (isOverloaded()) {
            for (WebMethod webMethod : overloadingMethods) {
                webMethod.view = view;
            }
        }
    }

    public Class<?> getHttpArgType() {
        return httpArgType;
    }

    public Class<?> getReturnType() {
        return method.getReturnType();
    }

    @Override
    public int compareTo(WebMethod other) {
        String thisUrl = (this.url == null) ? "" : this.url;
        String otherUrl = (other.url == null) ? "" : other.url;
        return thisUrl.compareTo(otherUrl);
    }

    @Override
    public String toString() {
        String viewDescription;
        if (hasViewTemplateFile)
            viewDescription = StringUtils.substringAfterLast(getTemplateFileName(), "/");
        else
            viewDescription = "AUTO";
        return method.getDeclaringClass().getSimpleName() + "." + method.getName() + " [" + viewDescription + "]";
    }

    public WebMethod getNext() {
        return next;
    }

    public void setNext(WebMethod next) {
        this.next = next;
    }

    public void initializeUrl(String baseUrl) {
        if (baseUrl == null || "/".equals(url))
            baseUrl = "";
        if (baseUrl.length() > 0 && !baseUrl.startsWith("/")) {
            baseUrl = "/" + baseUrl;
        }
        String urlSuffix;
        if (WebMethodType.INDEX == getType()) {
            urlSuffix = (baseUrl.isEmpty()) ? "/" : "";
        } else {
            urlSuffix = "/" + getName();
        }
        setUrl(baseUrl + urlSuffix);
    }

    public void initializeOperation() {
        if (getType() == null) {
            if (getName().equalsIgnoreCase(WebMethodType.INDEX.name())) {
                setType(WebMethodType.INDEX);
            } else {
                Class<?> returnType = getReturnType();
                if (WebMethod.HttpMethod.POST == getHttpMethod()) {
                    setType(WebMethodType.SUBMIT);
                } else if (!Collection.class.isAssignableFrom(returnType)) {
                    if (hasHttpArg) {
                        setType(WebMethodType.UPDATE_FORM);
                    } else {
                        setType(WebMethodType.CREATE_FORM);
                    }

                } else {
                    setType(WebMethodType.CUSTOM);
                }
            }
        }
    }

    public boolean reusesView() {
        return reuseViewMethod != null;
    }

    public WebMethod getReuseViewMethod() {
        return reuseViewMethod;
    }

    public void setReuseViewMethod(WebMethod viewMethod) {
        this.reuseViewMethod = viewMethod;
    }

    public void addOverloadingMethods(Collection<WebMethod> methods) {
        this.overloadingMethods.addAll(methods);
    }

    public Set<WebMethod> getOverloadingMethods() {
        return new HashSet<WebMethod>(overloadingMethods);
    }

    public boolean isOverloaded() {
        return !this.overloadingMethods.isEmpty();
    }

    public boolean isDefaultMethod() {
        return getType() == WebMethodType.INDEX;
    }
}
