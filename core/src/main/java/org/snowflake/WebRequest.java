package org.snowflake;

import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.snowflake.argumentinjection.ArgumentProducer;
import org.snowflake.argumentinjection.ArgumentsBuilder;
import org.snowflake.fieldconverters.FieldConverter;
import org.snowflake.fieldconverters.FieldValidationException;
import org.snowflake.utils.Console;
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
        if (answer.isAutoGenerated()) {
            ScaffoldHints scaffoldHints = answer.getScaffoldHints();
            for (WebMethod webMethod : webPage.rowActionWebMethods()) {
                scaffoldHints.addRowAction(webPage.getController(), webMethod.getName());
            }
            for (WebMethod webMethod : webPage.pageActionWebMethods()) {
                if (WebMethodType.INDEX != webMethod.getType())
                    scaffoldHints.addPageAction(webPage.getController(), webMethod.getName());
            }
        }
        Object[] args = new ArgumentsBuilder(this).buildArguments(webMethod.getMethod());
        Object result;
        try {
            result = webMethod.getMethod().invoke(webPage.getController(), args);
        } catch (Exception e) {
            Console.println("\tException invoking \"" + webMethod.getMethod().toGenericString()
                    + "\"\n\tArgs:\n\t" + StringUtils.join(args, "\n\t\t") + "");

            Throwable t;
            if (e instanceof InvocationTargetException) {
                t = ((InvocationTargetException) e).getTargetException();
            } else {
                t = e;
            }
            throw t;
        }
        answer.setData(result);
    }

    public void delegateToView(OutputStream responseBody) throws Exception {
        View view;
        if (answer.getViewForMethod() != null) {
            WebMethod otherMethod = webPage.getWebMethodByName(answer.getViewForMethod());
            answer.setTemplateFile(otherMethod.getTemplateFileName());
            view = otherMethod.getView();
        } else {
            view = webMethod.getView();
        }
        if (view == null) {
            String message = "Could not find or generate view for \"" + this.webMethod.getMethod().toGenericString()
                    + "\" (" + this.webMethod.getMethod().getDeclaringClass() + ")";
            throw new SnowflakeException(message);
        }
        view.renderView(webMethod, question, answer, responseBody);
    }

    @Override
    public Object getArgumentOfType(Class<?> type) {
        if (type == Question.class)
            return this.question;

        if (type == Answer.class)
            return this.answer;

        if (WebMethod.isBuiltInType(type)) {

            if (Map.class.isAssignableFrom(type))
                return question.getParameters();

            if (webPage.isIdType(type)) {
                Object intValue = question.getId();
                if (intValue == null) {
                    throw new SnowflakeException("Attempt to assign null value to primitive: Id argument of method \""
                            + webMethod.getMethod().toGenericString() + "\"");
                }
                return intValue;
            }
            throw new SnowflakeException("Unable to create argument of type " + type);
        }
        for (Object customArg : this.customArgs) {
            if (customArg != null && customArg.getClass() == type) {
                return customArg;
            }
        }

        Object modelObject = createModelObject(webMethod.getHttpArgType());
        validateAndPopulate(question.getParameters(), modelObject);
        return modelObject;
    }

    protected void validateAndPopulate(Map<String, String> parameters, Object httpArg) throws ValidationException {
        Map<String, Class<?>> publicFields = ReflectionHelpers.publicFields(httpArg.getClass());
        ValidationException validationException = new ValidationException();
        for (String fieldName : publicFields.keySet()) {
            String formValue = parameters.remove(fieldName);
            // Missing required fields to be handled by business logic
            if (formValue != null && !formValue.isEmpty()) {
                Class<?> fieldType = publicFields.get(fieldName);
                FieldConverter fieldConverter = webApp.findConverterForType(fieldType);
                if (fieldConverter == null) {
                    throw new SnowflakeException("No field converter found for type " + fieldType);
                }
                try {
                    Object value = fieldConverter.convert(formValue, fieldType);
                    if (value != null)
                        ReflectionHelpers.invokeSetterForVariable(fieldName, value, fieldType, httpArg);
                } catch (FieldValidationException e) {
                    validationException.invalidateField(fieldName, e.getMessage());
                }
            }
        }
        if (!parameters.isEmpty())
            for (String param : parameters.keySet())
                validationException.invalidateField(param, "Unmatched input \"" + parameters.get(param) + "\"");

        if (validationException.isInvalidated())
            throw validationException;
    }


    /**
     * Create the object that will be populated with data from HTML form
     * 
     * @param type
     *            Type of object to be created
     */
    protected <T> T createModelObject(Class<T> type) {
        T model;
        try {
            model = type.newInstance();
        } catch (Exception e) {
            throw new SnowflakeException(e);
        }
        return model;
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
