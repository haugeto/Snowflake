package org.snowflake.devserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.concurrent.Executors;

import org.snowflake.SnowflakeException;
import org.snowflake.WebApp;
import org.snowflake.WebMethod;
import org.snowflake.WebPage;
import org.snowflake.utils.Console;
import org.snowflake.views.View;

import com.sun.net.httpserver.HttpServer;

/**
 * The development server we use while developing a Snowflake application
 * 
 * @author haugeto
 */
public class DevServer extends WebApp {

    public static final String SERVER_NAME = "Snowflake DevServer 1.0";

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
        warnAboutOverloading();
    }

    public void warnAboutOverloading() {
        for (WebPage webPage : webPages.values()) {
            for (WebMethod webMethod : webPage.getWebMethods()) {
                if (webMethod.isOverloaded()) {
                    Console.println("Warning: Overloaded methods not supported (methods \"" + webMethod.getName()
                            + "\" of " + webMethod.getMethod().getDeclaringClass() + ")");
                }
            }
        }
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
        WebPage staticWebPage = new WebPage(new StaticContentController(), "/static");
        staticWebPage.createWebMethods(new HashSet<Class<?>>());
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

    public void setPreviouslyGeneratedScaffold(String name, String content) {
        Console.put("previouslyGeneratedScaffold.name", name);
        Console.put("previouslyGeneratedScaffold.content", content);
    }

}