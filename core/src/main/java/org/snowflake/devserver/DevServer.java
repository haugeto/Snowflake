package org.snowflake.devserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.snowflake.RequestInterceptor;
import org.snowflake.SnowflakeException;
import org.snowflake.StaticContentController;
import org.snowflake.WebApp;
import org.snowflake.WebMethod;
import org.snowflake.WebPage;
import org.snowflake.utils.Console;
import org.snowflake.views.StaticContentView;
import org.snowflake.views.View;

import com.sun.net.httpserver.HttpServer;

/**
 * The starting point for a Snowflake application under development.
 * <p />
 * TODO: Exception handling: How do we capture exceptions thrown in Threads
 * created by the HttpServer?
 * 
 * @author haugeto
 */
public class DevServer extends WebApp {

    public static final int DEFAULT_PORT = 4040;

    final int port;

    HttpServer httpServer;

    public DevServer(String title) {
        this(title, DEFAULT_PORT);
    }

    public DevServer(String title, int port) {
        super(title);
        this.port = port;
    }

    protected void beforeStart() {
    }

    /**
     * Starts your Snowflake web application.
     * 
     * @throws Exception
     */
    public final void run() throws Exception {
        long startupTime = System.currentTimeMillis();
        Console.br();
        Console.center("Snowflake MVC");

        // TODO: Thread.setDefaultUncaughtExceptionHandler(...);

        viewFactory.initialize();
        beforeStart();

        InetSocketAddress addr = new InetSocketAddress(port);
        try {
            httpServer = HttpServer.create(addr, 0);

            Console.hr();
            initializeDynamicContentContexts();
            initializeStaticContentContexts();
            Console.hr();

            httpServer.setExecutor(Executors.newCachedThreadPool());
            httpServer.start();

            afterStart(httpServer);

            double duration = ((double) System.currentTimeMillis() - (double) startupTime) / 1000;
            Console.justify("Listening on port " + port, "[" + duration + "s]", ' ');
            Console.br();
        } catch (IOException e) {
            throw new SnowflakeException(e);
        }
    }

    protected void afterStart(HttpServer httpServer) {
    }

    protected void initializeDynamicContentContexts() {
        for (WebPage webPage : webPages.values()) {
            for (WebMethod webMethod : webPage.getWebMethods()) {
                String description = webPage.getController().getClass().getSimpleName() + "." + webMethod.getName();

                if (webMethod.getNext() != null) {
                    description += " (submits to " + webMethod.getNext().getName() + ")";
                }
                if (webMethod.reusesView()) {
                    description += " (reuses view of " + webMethod.getReuseViewMethod().getName() + ")";
                }
                description += " " + webMethod.getHttpMethod();

                Console.justify(webMethod.getUrl(), description, '.');
                View view = viewFactory.createView(webMethod);
                webMethod.setView(view);
                httpServer.createContext(webMethod.getUrl(), new DevServerRequestHandler(this, webPage, webMethod));
            }
        }
    }

    protected void initializeStaticContentContexts() {
        // TODO: Is it feasible to consolidate this logic with that of
        // initializeDynamicContentContexts()?
        WebPage staticWebPage = new WebPage(new StaticContentController(), "/static");
        webPages.put("/static", staticWebPage);
        for (WebMethod staticContentMethod : staticWebPage.getWebMethods()) {
            staticContentMethod.setView(new StaticContentView());
            httpServer.createContext(staticContentMethod.getUrl(), new DevServerRequestHandler(this, staticWebPage,
                    staticContentMethod));
        }
    }

    public void shutdown() {
        if (httpServer != null)
            httpServer.stop(0);
    }

    public int getPort() {
        return port;
    }

    public void addRequestInterceptor(RequestInterceptor requestInterceptor) {
        this.requestInterceptors.add(requestInterceptor);
    }

}