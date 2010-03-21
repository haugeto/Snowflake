package org.snowflake.devserver;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.snowflake.Answer;
import org.snowflake.Question;
import org.snowflake.SnowflakeException;

/**
 * Makes the DevServer able to serve static content.
 * 
 * @author haugeto
 */
public class StaticContentController {

    /**
     * @throws SnowflakeException
     *             If requested file is not a {@link ContentType supported type}
     */
    public Object index(Question question, Answer answer) throws FileNotFoundException, SnowflakeException {
        if (StringUtils.isEmpty(question.getUrl())) {
            throw new IllegalArgumentException("No URL specified");
        }
        String filePath = StringUtils.substringAfter(question.getUrl(), "/static/");
        URL resource;
        try {
            resource = getClass().getClassLoader().getResource(filePath);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SnowflakeException("Error loading resource \"" + filePath + "\"", e);
        }
        if (resource == null) {
            answer.setHttpCode(404);
            throw new FileNotFoundException("Could not read file: " + filePath);
        }
        File file = new File(resource.getFile());

        String extension = StringUtils.substringAfterLast(file.getName(), ".");
        ContentType contentType = ContentType.parseExtension(extension);
        if (contentType == null) {
            throw new SnowflakeException("File \"" + filePath
                    + "\" is of a type not supported by the DevServer. Supported types: "
                    + ContentType.supportedTypes());
        }
        answer.setContentType(contentType.name().toLowerCase() + "/" + extension);

        // TODO: Handle binary files
        answer.setLastModified(new Date(file.lastModified()));
        answer.setContentLength(file.length());
        answer.setTemplateFile(filePath);

        return null;
    }

}
