package org.snowflake.devserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.snowflake.Answer;
import org.snowflake.Question;
import org.snowflake.RequestInterceptor;
import org.snowflake.SnowflakeException;
import org.snowflake.ValidationException;
import org.snowflake.WebMethod;
import org.snowflake.WebPage;
import org.snowflake.WebRequest;
import org.snowflake.WebRequestDispatcher;
import org.snowflake.utils.Console;
import org.snowflake.utils.HttpHelpers;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * Handles incoming HTTP requests from the {@link DevServer}. Each instance of
 * this class is bound to a particular URL. The HttpHandler is a specific
 * implementation for the Java 6 built-in HTTP server, which should be used for
 * development only.
 * 
 * @author haugeto
 */
public class DevServerRequestHandler extends WebRequestDispatcher implements HttpHandler {

    public static final String DEFAULT_ENCODING = "UTF-8";

    public DevServerRequestHandler(DevServer devServer, WebPage webPage, WebMethod webMethod) {
        super(devServer, webPage, webMethod);
    }

    public void handle(HttpExchange exchange) throws IOException {
        Console.justify(exchange.getRequestMethod() + " " + exchange.getRequestURI(), "=> " + webMethod.toString());
        Answer answer = webMethod.createAnswer(webApp.getDefaultViewCss());
        try {
            Question question = parseRequest(exchange);
            WebRequest webRequest = webApp.createWebRequest(webPage, webMethod, question, answer);
            Map<RequestInterceptor<?>, Object> customArgs = new HashMap<RequestInterceptor<?>, Object>();
            try {
                invokeBeforeInterceptors(question, answer, customArgs);
                webRequest.setCustomArgs(new HashSet<Object>(customArgs.values()));
                processController(webRequest);
                sendSuccessfulResponseHeaders(exchange, webRequest.getAnswer());
                processViewOnSuccess(webRequest, exchange.getResponseBody());

            } catch (SnowflakeException e) {
                if (!(e instanceof ValidationException)) {
                    e.printStackTrace(Console.err);
                }
                sendSuccessfulResponseHeaders(exchange, webRequest.getAnswer());
                processViewOnFailure(webRequest, exchange.getResponseBody(), e);
            } finally {
                invokeAfterInterceptors(question, answer, customArgs);
            }
        } catch (Throwable t) {
            sendFailureResponseHeaders(exchange, answer);
            t.printStackTrace(Console.err);
            throw new IOException(t);
        } finally {
            exchange.close();
        }
    }

    protected void invokeBeforeInterceptors(Question question, Answer answer,
            Map<RequestInterceptor<?>, Object> customArgs) throws Exception {
        for (RequestInterceptor<?> requestInterceptor : webApp.getRequestInterceptors()) {
            customArgs.put(requestInterceptor, requestInterceptor.before(question, answer));
        }
    }

    @SuppressWarnings("unchecked")
    protected void invokeAfterInterceptors(Question question, Answer answer,
            Map<RequestInterceptor<?>, Object> customArgs) throws Exception {
        for (RequestInterceptor requestInterceptor : webApp.getRequestInterceptors()) {
            requestInterceptor.after(question, answer, customArgs.get(requestInterceptor));
        }
    }

    void sendFailureResponseHeaders(HttpExchange exchange, Answer answer) throws IOException {
        exchange.sendResponseHeaders(answer.getHttpCode(), 0);
    }

    void sendSuccessfulResponseHeaders(HttpExchange exchange, Answer answer) throws IOException {
        Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.set("Server", DevServer.SERVER_NAME);
        responseHeaders.set("Content-Type", answer.getContentType());
        if (answer.getContentLength() != null) {
            responseHeaders.set("Content-Length", Long.toString(answer.getContentLength()));
        }
        if (answer.getLastModified() != null) {
            responseHeaders.set("Last-Modified", answer.getLastModified().toString());
        }
        responseHeaders.set("Date", new Date().toString());
        exchange.sendResponseHeaders(WebRequest.HTTP_OK, 0);
    }

    Question parseRequest(HttpExchange exchange) throws Exception {
        Question result = new Question();

        URI requestURI = exchange.getRequestURI();
        String incomingUrl = requestURI.getPath();
        result.setUrl(incomingUrl);
        String boundUrl = webMethod.getUrl();

        if (incomingUrl.length() > boundUrl.length()) {
            // assume restful lookup
            String urlFragment = StringUtils.substringBefore(incomingUrl, "?");

            String idStr = StringUtils.substringAfterLast(urlFragment, "/");
            if (idStr.length() > 0) {
                int id;
                try {
                    id = Integer.parseInt(idStr);
                    result.setId(id);
                } catch (NumberFormatException e) {
                    // happens when there is no ID embedded in the URL, which is
                    // perfectly fine.
                }
            }
        }
        // parse URL get parameters, if any
        String urlParameters = requestURI.getQuery();
        if (!StringUtils.isEmpty(urlParameters)) {
            Map<String, String> urlVariables = HttpHelpers.parseHttpParameters(urlParameters, DEFAULT_ENCODING);
            result.setParameters(urlVariables);
        }
        if (WebMethod.HttpMethod.POST.name().equalsIgnoreCase(exchange.getRequestMethod())) {
            BufferedReader r = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
            String requestBody = r.readLine();
            if (requestBody != null) {
                // TODO: Figure out the clients enc
                Map<String, String> httpVariables = HttpHelpers.parseHttpParameters(requestBody, DEFAULT_ENCODING);
                // FIXME: This overwrites possible URL parameters
                result.setParameters(httpVariables);
            }
        }
        return result;
    }

}