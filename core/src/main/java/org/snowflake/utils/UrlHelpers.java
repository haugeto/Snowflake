package org.snowflake.utils;

public class UrlHelpers {

    public static String normalize(String url) {
        if (url == null || url.isEmpty())
            return "";

        String result = url;
        if (!url.startsWith("/"))
            result = "/" + result;
        if (url.endsWith("/"))
            result = result.substring(0, result.length() - 1);
        return result;
    }

}
