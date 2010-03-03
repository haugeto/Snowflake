package org.snowflake.views;

import java.io.OutputStream;

import org.snowflake.Answer;
import org.snowflake.WebMethod;


/**
 * Responsible for rendering the result from a web method to some format
 * readable by the client browser.
 * 
 * @author haugeto
 */
public interface View {

    public void renderView(WebMethod webMethod, Answer answer, OutputStream out) throws Exception;

}