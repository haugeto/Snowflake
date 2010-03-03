package org.snowflake.views.scaffolding;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;

public class ScaffoldingHelper {

    static final char SPACE = ' ';

    public static String createSingularTitle(Class<?> target) {
        String className = target.getSimpleName();
        return StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(className), SPACE);
    }

    public static String createPluralTitle(Collection<?> target) {
        if (target != null && !target.isEmpty()) {
            Object firstIndexObject = target.iterator().next();
            return createSingularTitle(firstIndexObject.getClass()) + "s";
        } else {
            return null;
        }
    }

}
