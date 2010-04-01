package org.snowflake.devserver;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

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

    public DevServerRequestHandler(DevServer devServer, WebPage webPage, WebMethod webMethod) {
        super(devServer, webPage, webMethod);
    }

    public void handle(HttpExchange exchange) throws IOException {
        Console.justify(exchange.getRequestMethod() + " " + exchange.getRequestURI(), "=> " + webMethod.toString());
        Answer answer = webApp.createAnswer(webMethod);
        try {
            Question question = webPage.parseRequest(exchange.getRequestBody(), exchange.getRequestMethod(), exchange
                    .getRequestURI());
            OutputStream responseBody = exchange.getResponseBody();
            WebMethod webMethod = chooseOverloadedMethod(question);
            WebRequest webRequest = webApp.createWebRequest(webPage, webMethod, question, answer);
            Map<RequestInterceptor<?>, Object> customArgs = new HashMap<RequestInterceptor<?>, Object>();
            try {
                invokeBeforeInterceptors(question, answer, customArgs);
                webRequest.setCustomArgs(new HashSet<Object>(customArgs.values()));
                processController(webRequest);
                sendSuccessfulResponseHeaders(exchange, webRequest.getAnswer());
                processViewOnSuccess(webRequest, responseBody);
            } catch (ValidationException e) {
                sendSuccessfulResponseHeaders(exchange, webRequest.getAnswer());
                processViewOnValidationFailure(webRequest, responseBody, (ValidationException) e);
            } catch (SnowflakeException e) {
                e.printStackTrace(Console.err);
                sendFailureResponseHeaders(exchange, answer);
            } finally {
                invokeAfterInterceptors(question, answer, customArgs);
            }
        } catch (Throwable t) {
            t.printStackTrace(Console.err);
            // sendFailureResponseHeaders(exchange, answer);
            throw new IOException(t);
        } finally {
            exchange.close();
        }
    }

    void sendFailureResponseHeaders(HttpExchange exchange, Answer answer) throws IOException {
        exchange.sendResponseHeaders(answer.getHttpCode(), 0);
    }

    void sendSuccessfulResponseHeaders(HttpExchange exchange, Answer answer) throws IOException {
        Map<String, String> headers = answer.createHeaders();
        Headers responseHeaders = exchange.getResponseHeaders();
        for (String key : headers.keySet()) {
            responseHeaders.set(key, headers.get(key));
        }
        exchange.sendResponseHeaders(WebRequest.HTTP_OK, 0);
    }

}