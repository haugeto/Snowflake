/**
 * 
 */
package org.snowflake.devserver;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

/**
 * Enumerates the file types (extensions) supported by the {@link DevServer}.
 * Note that limitations here would not affect the future ability to deploy
 * Snowflake applications on Java EE containers.
 * 
 * @author haugeto
 */
public enum ContentType {
    TEXT(false, "css", "html", "htm", "txt", "vm", "csv"), IMAGE(true, "png", "gif", "jpg");

    final boolean binary;

    final Set<String> extensions = new HashSet<String>();

    ContentType(boolean binary, String... extensions) {
        this.binary = binary;
        if (extensions != null)
            this.extensions.addAll(new HashSet<String>(Arrays.asList(extensions)));
    }

    public static ContentType parseExtension(String extension) {
        for (ContentType type : ContentType.values()) {
            if (type.supports(extension))
                return type;
        }
        return null;
    }

    public static String supportedTypes() {
        Set<String> result = new LinkedHashSet<String>();
        for (ContentType type : ContentType.values())
            result.addAll(type.extensions);
        return StringUtils.join(result.toArray(), ',');
    }

    public boolean supports(String extension) {
        if (extension == null)
            return false;
        return this.extensions.contains(extension.toLowerCase());
    }

    public boolean isBinary() {
        return binary;
    }

}