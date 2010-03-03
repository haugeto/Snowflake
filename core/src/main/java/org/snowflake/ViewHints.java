package org.snowflake;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * <p>
 * Has information about how auto views should be generated. Is initialized by
 * the framework and can be manipulated by client code.
 * </p>
 * 
 * @author haugeto
 */
public class ViewHints {

    Collection<?> data = null;

    List<String> columnNames = new ArrayList<String>();

    final List<WebAction> rowActions = new ArrayList<WebAction>();

    final List<WebAction> pageActions = new ArrayList<WebAction>();

    public ViewHints() {

    }

    public ViewHints(Collection<?> data) {
        this.data = data;
    }

    public Collection<?> getData() {
        return data;
    }

    public void columns(String... columnNames) {
        this.columnNames = new ArrayList<String>(Arrays.asList(columnNames));
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(List<String> columnNames) {
        this.columnNames = columnNames;
    }

    public void addRowAction(WebAction webAction) {
        rowActions.add(webAction);
    }

    public void addRowAction(Object controller, String methodName) {
        rowActions.add(new WebAction(controller, methodName));
    }

    public void addPageAction(WebAction webAction) {
        pageActions.add(webAction);
    }

    public void addPageAction(Object controller, String methodName) {
        pageActions.add(new WebAction(controller, methodName));
    }

    public List<WebAction> getRowActions() {
        return rowActions;
    }

    public List<WebAction> getPageActions() {
        return pageActions;
    }

}
