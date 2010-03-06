package org.snowflake;

import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.snowflake.argumentinjection.ArgumentProducer;
import org.snowflake.argumentinjection.ArgumentsBuilder;
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
public class WebRequest implements ArgumentProducer {

    public static final int HTTP_OK = 200;

    Question question;

    Answer answer;

    WebMethod webMethod;

    WebPage webPage;

    WebApp webApp;

    Set<Object> customArgs = new HashSet<Object>();

    public WebRequest(WebApp webApp, WebPage webPage, WebMethod webMethod, Question question, Answer answer) {
        this.webApp = webApp;
        this.webPage = webPage;
        this.webMethod = webMethod;
        this.question = question;
        this.answer = answer;
    }

    public void delegateToController() throws Throwable {
        ViewHints viewHints = answer.getViewHints();
        for (WebMethod webMethod : webPage.rowActionWebMethods()) {
            viewHints.addRowAction(webPage.getController(), webMethod.getName());
        }
        for (WebMethod webMethod : webPage.pageActionWebMethods()) {
            if (WebMethodType.INDEX != webMethod.getType())
                viewHints.addPageAction(webPage.getController(), webMethod.getName());
        }
        answer.setTitle(createTitle());

        Object[] args = new ArgumentsBuilder(this).buildArguments(webMethod.getMethod());
        Object result;
        try {
            result = webMethod.getMethod().invoke(webPage.getController(), args);
        } catch (Exception e) {
            Throwable t;
            if (e instanceof InvocationTargetException) {
                t = ((InvocationTargetException) e).getTargetException();
            } else {
                t = e;
            }
            throw t;
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

    @Override
    public Object getArgumentOfType(Class<?> type) {
        for (Object customArg : this.customArgs) {
            if (customArg != null && customArg.getClass() == type) {
                return customArg;
            }
        }
        if (type == Question.class)
            return this.question;

        if (type == Answer.class)
            return this.answer;

        if (Map.class.isAssignableFrom(type))
            return question.getParameters();

        if (type == Integer.class || type == int.class)
            return question.getId();

        if (WebMethod.isCustomArgument(type)) {
            Object result = createPostDataObject(webMethod.getHttpArgType());
            validateAndPopulate(question.getParameters(), result);
            return result;
        }

        throw new IllegalArgumentException("Unable to create argument of type " + type);
    }

    protected void validateAndPopulate(Map<String, String> parameters, Object httpArg) throws ValidationException {
        Map<String, Class<?>> publicFields = ReflectionHelpers.publicFields(httpArg.getClass());
        ValidationException validationException = new ValidationException();
        for (String fieldName : publicFields.keySet()) {
            String formValue = parameters.remove(fieldName);
            try {
                Class<?> fieldType = publicFields.get(fieldName);
                FieldConverter fieldConverter = webApp.findConverterForType(fieldType);
                if (fieldConverter == null) {
                    throw new SnowflakeException("No field converter found for type " + fieldType);
                }
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

    public void setCustomArgs(Set<Object> customArgs) {
        this.customArgs.addAll(customArgs);
    }

}
