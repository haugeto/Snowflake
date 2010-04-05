package org.snowflake.devserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Set;
import java.util.concurrent.Executors;

import org.snowflake.SnowflakeException;
import org.snowflake.WebApp;
import org.snowflake.WebMethod;
import org.snowflake.WebPage;
import org.snowflake.utils.Console;

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
    public final void run() throws SnowflakeException {
        long startupTime = System.currentTimeMillis();
        Console.br();
        Console.center("Snowflake MVC");
        initViewEngine();
        beforeStart();

        InetSocketAddress addr = new InetSocketAddress(port);
        try {
            httpServer = HttpServer.create(addr, 0);

            Console.hr();
            Set<WebPage> allContexts = initializeContexts();

            for (WebPage webPage : allContexts) {
                for (WebMethod webMethod : webPage.getWebMethods()) {
                    httpServer.createContext(webMethod.getUrl(), new DevServerRequestHandler(this, webPage, webMethod));
                }
            }
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