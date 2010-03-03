package org.snowflake;

import java.io.File;
import java.net.URL;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

public class StaticContentController {

    public Object index(Question question, Answer answer) {
        String filePath = StringUtils.substringAfter(question.getUrl(), "/static/");
        URL resource;
        try {
            resource = getClass().getClassLoader().getResource(filePath);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SnowflakeException(e);
        }
        if (resource != null) {
            File file = new File(resource.getFile());
            // TODO: Handle binary files
            answer.setContentType("text/" + StringUtils.substringAfterLast(file.getName(), "."));
            answer.setLastModified(new Date(file.lastModified()));
            answer.setContentLength(file.length());
            answer.setTemplateFile(filePath);
        }
        else {
            answer.setHttpCode(404);
        }
        return null;
    }
}
