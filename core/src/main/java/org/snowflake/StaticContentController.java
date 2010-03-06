package org.snowflake;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

public class StaticContentController {

    public Object index(Question question, Answer answer) throws FileNotFoundException {
        String filePath = StringUtils.substringAfter(question.getUrl(), "/static/");
        URL resource;
        try {
            resource = getClass().getClassLoader().getResource(filePath);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SnowflakeException(e);
        }
        if (resource == null) {
            answer.setHttpCode(404);
            throw new FileNotFoundException("Could not read file: " + filePath);
        }
        File file = new File(resource.getFile());
        // TODO: Handle binary files
        answer.setContentType("text/" + StringUtils.substringAfterLast(file.getName(), "."));
        answer.setLastModified(new Date(file.lastModified()));
        answer.setContentLength(file.length());
        answer.setTemplateFile(filePath);

        return null;
    }
}
