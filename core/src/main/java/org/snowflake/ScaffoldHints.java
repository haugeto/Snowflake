package org.snowflake;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Has information about how auto views should be generated. Is initialized by
 * the framework and can be manipulated by client code.
 * <p>
 * It does not make sense to invoke methods of this class in controller methods
 * delegating to custom views.
 * 
 * @author haugeto
 */
public class ScaffoldHints {

    final List<String> columnNames = new ArrayList<String>();

    final List<WebAction> rowActions = new ArrayList<WebAction>();

    final List<WebAction> pageActions = new ArrayList<WebAction>();

    public ScaffoldHints() {
    }

    /**
     * Instruct which columns should be displayed in an index table
     */
    public void columns(String... columnNames) {
        columns(Arrays.asList(columnNames));
    }

    /**
     * Instruct which columns should be displayed in an index table
     */
    public void columns(Collection<String> columnNames) {
        this.columnNames.clear();
        this.columnNames.addAll(columnNames);
    }

    public List<String> getColumnNames() {
        return new ArrayList<String>(columnNames);
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
        return new ArrayList<WebAction>(rowActions);
    }

    public List<WebAction> getPageActions() {
        return new ArrayList<WebAction>(pageActions);
    }

}
