package org.snowflake;

import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.snowflake.fieldconverters.FieldConverter;
import org.snowflake.fieldconverters.FieldValidationException;
import org.snowflake.utils.ReflectionHelpers;
import org.snowflake.views.View;

/**
 * <p>
 * Created for each incoming HTTP request. Handles the requests by invoking the
 * client controller method and view logic.
 * </p>
 * 
 * @author haugeto
 */
public class WebRequest {

    public static final int HTTP_OK = 200;

    Question question;

    Answer answer;

    WebMethod webMethod;

    WebPage webPage;

    WebApp webApp;

    public WebRequest(WebApp webApp, WebPage webPage, WebMethod webMethod, Question question, Answer answer) {
        this.webApp = webApp;
        this.webPage = webPage;
        this.webMethod = webMethod;
        this.question = question;
        this.answer = answer;
    }

    /**
     * Invoked before a request is processed (default implementation does
     * nothing)
     * 
     * @param question
     *            The request
     * @param answer
     *            The response
     */
    public void before(Question question, Answer answer) throws Exception {

    }

    /**
     * Invoked after a request is processed (default implementation does
     * nothing)
     * 
     * @param question
     *            The request
     * @param answer
     *            The response
     */
    public void after(Question question, Answer answer) throws Exception {

    }

    public void delegateToController() throws SnowflakeException {
        ViewHints viewHints = answer.getViewHints();
        for (WebMethod webMethod : webPage.rowActionWebMethods()) {
            viewHints.addRowAction(webPage.getController(), webMethod.getName());
        }
        for (WebMethod webMethod : webPage.pageActionWebMethods()) {
            if (WebMethodType.INDEX != webMethod.getType())
                viewHints.addPageAction(webPage.getController(), webMethod.getName());
        }
        answer.setTitle(createTitle());

        Object[] args = buildArguments(question, answer);
        Object result;
        try {
            result = webMethod.getMethod().invoke(webPage.getController(), args);
        } catch (InvocationTargetException invocationTargetException) {
            Throwable e = invocationTargetException.getTargetException();
            if (e instanceof SnowflakeException)
                throw (SnowflakeException) e;
            else
                throw new SnowflakeException(e);
        } catch (Exception e) {
            throw new SnowflakeException(e);
        }
        answer.setData(result);

        if (answer.hasIndexData()) {
            if (viewHints.getColumnNames().isEmpty()) {
                Collection<?> c = answer.getIndexData();
                if (!c.isEmpty()) {
                    Object firstObject = c.iterator().next();
                    viewHints.setColumnNames(ReflectionHelpers.publicFieldNames(firstObject));
                }
            }
            initUrls(viewHints.getRowActions());
        }
        initUrls(viewHints.getPageActions());
    }

    public void delegateToView(OutputStream responseBody) throws Exception {
        View view;
        if (answer.getViewForMethod() != null) {
            WebMethod otherMethod = webPage.getWebMethodByName(answer.getViewForMethod());
            answer.setTemplateFile(otherMethod.getTemplateFileName());
            view = otherMethod.getView();
        } else {
            // TODO: fail with a meaningful exception if webmethod has no view
            view = webMethod.getView();
        }
        view.renderView(webMethod, answer, responseBody);
    }

    void initUrls(Iterable<WebAction> webActions) {
        for (WebAction webAction : webActions) {
            WebPage actionPage = webApp.getWebPageForController(webAction.getController());
            WebMethod actionMethod = actionPage.getWebMethodByName(webAction.getMethodName());
            webAction.setDescription(actionMethod.getName());
            webAction.setUrl(actionMethod.getUrl());
        }
    }

    Object[] buildArguments(Question question, Answer answer) throws SnowflakeException {
        List<Object> result = new ArrayList<Object>();

        if (webMethod.hasQuestionArg)
            result.add(question);

        if (webMethod.hasAnswerArg)
            result.add(answer);

        if (webMethod.hasHttpArg) {
            Object httpArg;
            Class<?> httpArgType = webMethod.getHttpArgType();
            if (httpArgType.isAssignableFrom(Map.class)) {
                httpArg = question.getParameters();
            } else if (WebMethod.isCustomArgument(httpArgType)) {
                httpArg = createPostDataObject(webMethod.getHttpArgType());
                validateAndPopulate(question.getParameters(), httpArg);
                // ReflectionHelpers.map2Fields(question.getParameters(),
                // httpArg);
            } else {
                if (question.nrOfParameters() == 0) {
                    if ((httpArgType.equals(Integer.class) || httpArgType.equals(int.class))
                            && question.getId() != null) {
                        httpArg = question.getId();
                    } else {
                        throw new SnowflakeException("Wrong number of HTTP parameters (expected 1, got "
                                + question.nrOfParameters() + ")");
                    }
                } else {
                    String httpArgStr = question.getParameterValues().get(0);
                    httpArg = httpArgStr;
                    if (httpArgType.equals(Integer.class) || httpArgType.equals(int.class)) {
                        if (StringUtils.isEmpty(httpArgStr)) {
                            httpArg = question.getId();
                        } else {
                            httpArg = Integer.parseInt(httpArgStr);
                        }
                    }
                }
            }
            result.add(httpArg);
        }
        return result.toArray();
    }

    protected void validateAndPopulate(Map<String, String> parameters, Object httpArg) throws ValidationException {
        Map<String, Class<?>> publicFields = ReflectionHelpers.publicFields(httpArg.getClass());
        ValidationException validationException = new ValidationException();
        for (String fieldName : publicFields.keySet()) {
            String formValue = parameters.remove(fieldName);
            try {
                Class<?> fieldType = publicFields.get(fieldName);
                FieldConverter fieldConverter = webApp.findConverterForType(fieldType);
                Object value = fieldConverter.convert(formValue, fieldType);
                if (value != null)
                    ReflectionHelpers.invokeSetterForVariable(fieldName, value, fieldType, httpArg);

            } catch (FieldValidationException e) {
                validationException.putErrorMessage(fieldName, e.getMessage());
            }
        }
        if (!parameters.isEmpty())
            for (String param : parameters.keySet())
                validationException.putErrorMessage(param, "Unmatched input \"" + parameters.get(param) + "\"");

        if (validationException.hasValidationErrors())
            throw validationException;
    }

    protected String createTitle() {
        String title = webApp.getName();
        if (webMethod.getType() != WebMethodType.INDEX) {
            title += " - " + webMethod.getName();
        }
        return title;
    }

    /**
     * Create the object that will be populated with data from HTML form
     * 
     * @param type
     *            Type of object to be created
     */
    protected <T> T createPostDataObject(Class<T> type) {
        T postData;
        try {
            postData = type.newInstance();
        } catch (Exception e) {
            throw new SnowflakeException(e);
        }
        return postData;
    }

    public Answer getAnswer() {
        return this.answer;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    public void setWebMethod(WebMethod webMethod) {
        this.webMethod = webMethod;
    }

    public Question getQuestion() {
        return question;
    }
}
