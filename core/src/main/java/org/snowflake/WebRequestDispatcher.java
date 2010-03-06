package org.snowflake;

import java.io.OutputStream;
import java.util.Map;

import org.snowflake.utils.Console;

/**
 * <p>
 * Integration point when implementing Snowflake support for an application
 * server.
 * </p>
 * <p>
 * This class should be instantiated for each incoming HTTP request.
 * </p>
 * <p>
 * The {@link #processController(Question, Answer) process} method will invoke
 * the Snowflake mechanisms that in turn invokes the controller logic.
 * </p>
 * <p>
 * Similar semantics for the
 * {@link #processViewOnSuccess(WebRequest, OutputStream) processView} method.
 * </p>
 * 
 * @author haugeto
 */
public abstract class WebRequestDispatcher {

    protected final WebMethod webMethod;

    protected final WebApp webApp;

    protected final WebPage webPage;

    public WebRequestDispatcher(WebApp webApp, WebPage webPage, WebMethod webMethod) {
        this.webApp = webApp;
        this.webPage = webPage;
        this.webMethod = webMethod;
    }

    /**
     * Processes the controller logic for a web request.
     * 
     * @param question
     *            Data about the request
     * @param answer
     *            Data about the response
     * @return The WebRequest that will handle rendering of the view
     */
    protected void processController(WebRequest webRequest) throws Throwable {
        webRequest.delegateToController();
        if (webMethod.reusesView()) {
            WebMethod index = webMethod.getReuseViewMethod();
            Question indexQuestion = new Question();
            indexQuestion.setUrl(webPage.getBaseUrl());
            Answer indexAnswer = index.createAnswer(webRequest.getAnswer().getViewCss());
            indexAnswer.putAll(webRequest.getAnswer().getTemplateVariables());
            webRequest.setWebMethod(index);
            webRequest.setAnswer(indexAnswer);
            webRequest.setQuestion(indexQuestion);
            webRequest.delegateToController();
        }
    }

    /**
     * <p>
     * Process the view logic for a successful web request.
     * </p>
     * <p>
     * This method should be invoked after
     * {@link #processController(Question, Answer)}.
     * </p>
     * 
     * @param webRequest
     *            The WebRequest instance returned by processController
     * @param responseBody
     *            The response stream to the client
     */
    protected void processViewOnSuccess(WebRequest webRequest, OutputStream responseBody) throws Exception {
        webRequest.delegateToView(responseBody);
    }

    /**
     * <p>
     * Process the view logic for an unsuccessful web request. Optional.
     * </p>
     */
    protected void processViewOnFailure(WebRequest failedRequest, OutputStream responseBody,
            SnowflakeException exception) throws Exception {
        if (exception instanceof ValidationException) {
            ValidationException validationException = (ValidationException) exception;
            Map<String, String> errorMessages = validationException.getErrorMessages();
            for (String fieldName : errorMessages.keySet()) {
                Console.justify(Console.INDENT + "Validation error [" + errorMessages.get(fieldName) + "]",
                        "For field \"" + fieldName + "\"", '.');
            }
            showFormWithValidationErrors(failedRequest, validationException, responseBody);
        }
    }

    protected void showFormWithValidationErrors(WebRequest failedRequest, ValidationException validationException,
            OutputStream responseBody) throws Exception {
        Question failedQuestion = failedRequest.getQuestion();
        WebMethod showFormMethod = webPage.findPrevious(this.webMethod);
        Answer answer = showFormMethod.createAnswer(failedRequest.getAnswer().getViewCss());
        answer.setFormData(failedQuestion.getParameters());
        answer.setFormDataType(showFormMethod.getReturnType());
        answer.setValidationErrors(validationException.getErrorMessages());
        Question question = new Question();
        question.setId(failedQuestion.getId());
        WebRequest showFormRequest = webApp.createWebRequest(webPage, showFormMethod, question, answer);
        showFormRequest.delegateToView(responseBody);
    }

}