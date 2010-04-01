package org.snowflake.views.scaffolding;

import org.apache.commons.lang.StringUtils;

public class ScaffoldingHelper {

    static final char SPACE = ' ';

    public static String createSingularTitle(Class<?> target) {
        if (target == null)
            return null;
        String className = target.getSimpleName();
        return StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(className), SPACE);
    }

    public static String createPluralTitle(Class<?> target) {
        if (target == null)
            return null;
        return createSingularTitle(target) + "s";
    }

}
