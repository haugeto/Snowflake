package org.snowflake.utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


import org.apache.commons.lang.StringUtils;
import org.snowflake.SnowflakeException;

public class ReflectionHelpers {

    public static String deduceFieldNameFromGetter(String getterName) {
        if (!(getterName.startsWith("get") || getterName.startsWith("set")) || getterName.length() <= 3) {
            throw new IllegalArgumentException("Invalid name for getter or setter: \"" + getterName + "\"");
        }
        String firstChar = getterName.toLowerCase().substring(3, 4);
        if (getterName.length() == 4)
            return firstChar;
        else
            return firstChar + getterName.substring(4);

    }

    public static String deduceFieldNameFromVariable(String fieldName) {
        if (fieldName == null)
            return null;
        if (fieldName.length() == 0)
            return fieldName;
        String firstChar = Character.toString(fieldName.charAt(0)).toLowerCase();

        return (fieldName.length() == 1 ? firstChar : firstChar + fieldName.substring(1));
    }

    public static String deduceSetterName(String fieldName) {
        if (fieldName == null || fieldName.length() == 0)
            throw new IllegalArgumentException("Invalid fieldName: " + fieldName);

        String firstChar = Character.toString(fieldName.charAt(0)).toUpperCase();

        return "set" + (fieldName.length() == 1 ? firstChar : firstChar + fieldName.substring(1));
    }

    public static String deduceGetterName(String fieldName) {
        if (fieldName == null || fieldName.length() == 0)
            throw new IllegalArgumentException("Invalid fieldName: " + fieldName);

        String firstChar = Character.toString(fieldName.charAt(0)).toUpperCase();

        return "get" + (fieldName.length() == 1 ? firstChar : firstChar + fieldName.substring(1));
    }

    public static void invokeSetterForVariable(String variableName, Object value, Class<?> type, Object target)
            throws SnowflakeException {
        if (target == null)
            throw new IllegalArgumentException("object argument is null");
        String setterMethodName = deduceSetterName(deduceFieldNameFromVariable(variableName));

        try {
            Method method = target.getClass().getMethod(setterMethodName, type);
            method.invoke(target, value);
        } catch (Exception e) {
            throw new SnowflakeException(e);
        }
    }

    public static Object invokeGetterForVariable(String variableName, Object object) throws SnowflakeException {
        if (object == null)
            throw new IllegalArgumentException("object argument is null");
        String getterMethodName = deduceGetterName(deduceFieldNameFromVariable(variableName));
        try {
            Method method = object.getClass().getMethod(getterMethodName);
            return method.invoke(object);
        } catch (Exception e) {
            throw new SnowflakeException(e);
        }
    }

    public static Class<?> fieldType(Class<?> type, String fieldName) throws SnowflakeException {
        String getterName = deduceGetterName(fieldName);
        Method getter;
        try {
            getter = type.getMethod(getterName);
        } catch (Exception e) {
            throw new SnowflakeException(e);
        }
        if (getter.getName().equals(getterName) && getter.getParameterTypes().length == 0)
            return getter.getReturnType();

        throw new IllegalArgumentException("No getter method could be found in " + type + " for field \"" + fieldName
                + "\"");
    }

    public static LinkedHashMap<String, String> fieldValues(Object dataObject) throws SnowflakeException {
        return fieldValues(dataObject, false);
    }

    public static LinkedHashMap<String, String> fieldValues(Object dataObject, boolean capitalizeKeys)
            throws SnowflakeException {
        LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
        for (String fieldName : publicFields(dataObject.getClass()).keySet()) {
            Object returnValue;
            try {
                Method method = dataObject.getClass().getMethod(deduceGetterName(fieldName));
                returnValue = method.invoke(dataObject);
            } catch (Exception e) {
                throw new SnowflakeException(e);
            }
            String value = (returnValue == null) ? "" : returnValue.toString();
            String key = fieldName;
            if (capitalizeKeys)
                key = StringUtils.capitalize(key);
            result.put(key, value);
        }
        return result;
    }

    public static Object resolveId(Object indexObject) throws SnowflakeException {
        return invokeGetterForVariable("id", indexObject);
    }

    public static List<String> publicFieldNames(Object target) {
        if (target == null)
            return Collections.emptyList();

        return publicFieldNames(target.getClass());
    }

    public static List<String> publicFieldNames(Class<?> target) {
        return new ArrayList<String>(publicFields(target).keySet());
    }

    public static Map<String, Class<?>> publicFields(Class<?> target) {
        Map<String, Class<?>> result = new LinkedHashMap<String, Class<?>>();
        for (Method m : target.getMethods()) {
            if (m.getName().startsWith("get") && m.getName().length() > 3 && m.getDeclaringClass().equals(target)) {
                result.put(deduceFieldNameFromGetter(m.getName()), m.getReturnType());
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static Set<String> enumValues(Class<?> enumClass) {
        Set<String> result = new LinkedHashSet<String>();
        Class<Enum> t = (Class<Enum>) enumClass;
        EnumSet s = EnumSet.allOf(t);
        for (Iterator<Enum> i = s.iterator(); i.hasNext();) {
            result.add(i.next().name());
        }
        return result;
    }

}
