package org.snowflake.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

public class HttpHelpers {

    public static Map<String, String> parseHttpParameters(String requestBody, String encoding)
            throws UnsupportedEncodingException {
        Map<String, String> result = new LinkedHashMap<String, String>();
        String[] keyValuePairs = requestBody.split("&");
        for (String keyValuePair : keyValuePairs) {
            String key = StringUtils.substringBefore(keyValuePair, "=");
            String value = StringUtils.substringAfter(keyValuePair, "=");

            key = URLDecoder.decode(key, encoding);
            value = URLDecoder.decode(value, encoding);

            result.put(key, value);
        }
        return result;
    }

    public static String describeRequest(HttpExchange exchange) {
        StringBuilder result = new StringBuilder();
        Headers requestHeaders = exchange.getRequestHeaders();
        Set<String> keySet = requestHeaders.keySet();
        Iterator<String> iter = keySet.iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            List<String> values = requestHeaders.get(key);

            result.append(key + ": " + StringUtils.join(values, ','));
        }
        return result.toString();
    }

}
