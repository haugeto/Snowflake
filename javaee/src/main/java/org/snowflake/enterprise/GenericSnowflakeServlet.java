package org.snowflake.enterprise;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.snowflake.Answer;
import org.snowflake.Question;
import org.snowflake.RequestInterceptor;
import org.snowflake.SnowflakeException;
import org.snowflake.ValidationException;
import org.snowflake.WebApp;
import org.snowflake.WebMethod;
import org.snowflake.WebPage;
import org.snowflake.WebRequest;
import org.snowflake.WebRequestDispatcher;
import org.snowflake.utils.Console;

/**
 * Generic Snowflake servlet, delegating the creation of controller objects to
 * sub classes. If you need custom mechanisms for creating controller objects
 * (e.g. you use Spring or some other object container), extend this Servlet.
 * <p/>
 * If no custom object creation mechanism is used, see {@link SnowflakeServlet}
 * 
 * @author haugeto
 */
@SuppressWarnings("serial")
public abstract class GenericSnowflakeServlet extends HttpServlet {

    public static final String PARAM_KEY_CONTROLLERS = "controllers";

    protected WebApp webApp;

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String contextUrl = req.getRequestURI();
        WebMethod target = webApp.resolveTargetMethod(contextUrl);
        if (target == null) {
            throw new ServletException("No controller mapped to URL " + contextUrl);
        }

        WebPage webPage = webApp.getWebPageForControllerClass(target.getMethod().getDeclaringClass());
        URI requestURI;
        try {
            requestURI = new URI(req.getRequestURI());
        } catch (URISyntaxException e) {
            throw new ServletException(e);
        }
        Question question = webPage.parseRequest(req.getInputStream(), req.getMethod(), requestURI);
        Answer answer = webApp.createAnswer(target);
        WebRequest webRequest = webApp.createWebRequest(webPage, target, question, answer);
        Map<RequestInterceptor<?>, Object> customArgs = new HashMap<RequestInterceptor<?>, Object>();
        OutputStream responseBody = resp.getOutputStream();
        WebRequestDispatcher dispatcher = new WebRequestDispatcher(webApp, webPage, target);
        try {
            try {
                dispatcher.invokeBeforeInterceptors(question, answer, customArgs);
                webRequest.setCustomArgs(new HashSet<Object>(customArgs.values()));
                dispatcher.processController(webRequest);
                dispatcher.processViewOnSuccess(webRequest, responseBody);
            } catch (ValidationException e) {
                dispatcher.processViewOnValidationFailure(webRequest, responseBody, (ValidationException) e);
            } catch (SnowflakeException e) {
                e.printStackTrace(Console.err);
                throw new ServletException(e);
            } finally {
                dispatcher.invokeAfterInterceptors(question, answer, customArgs);
            }
        } catch (Throwable t) {
            throw new ServletException(t);
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        ServletContext servletContext = config.getServletContext();
        createWebApp(servletContext);
        this.webApp.initViewEngine();

        for (Object controller : createControllers(config)) {
            this.webApp.registerController("", controller);
        }
        Console.hr();
        this.webApp.initializeContexts();
        Console.hr();
    }

    protected void createWebApp(ServletContext servletContext) {
        this.webApp = new WebApp(servletContext.getServletContextName(), servletContext.getContextPath());
    }

    /**
     * Return the controller objects that will handle requests to this Snowflake
     * web application.
     */
    protected abstract Set<Object> createControllers(ServletConfig config) throws ServletException;
}
