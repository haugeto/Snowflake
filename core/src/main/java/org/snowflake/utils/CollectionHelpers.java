package org.snowflake.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class CollectionHelpers {

    public static <V> void capitalizeKeys(Map<String, V> source) {
        Map<String, V> copy = new HashMap<String, V>(source);
        for (String key : copy.keySet()) {
            V value = source.remove(key);
            source.put(StringUtils.capitalize(key), value);
        }
    }

}
