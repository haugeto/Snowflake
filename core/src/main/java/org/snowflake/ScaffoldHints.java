package org.snowflake;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.snowflake.views.scaffolding.TableColumn;

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

    // TODO: columnNames probably redundant:
    final List<String> columnNames = new ArrayList<String>();

    final List<TableColumn> tableColumns = new ArrayList<TableColumn>();

    final List<WebAction> rowActions = new ArrayList<WebAction>();

    final List<WebAction> pageActions = new ArrayList<WebAction>();

    public ScaffoldHints() {
    }

    /**
     * Instruct which columns should be displayed in an index table
     */
    public void columns(String... fieldNames) {
        columns(Arrays.asList(fieldNames));

    }

    public void setColumnLink(String fieldName, String link) {
        for (TableColumn column : this.tableColumns) {
            if (fieldName.equals(column.getFieldName())) {
                column.setLink(link);
                return;
            }
        }
    }

    /**
     * Instruct which columns should be displayed in an index table
     */
    public void columns(Collection<String> fieldNames) {
        this.columnNames.clear();
        this.columnNames.addAll(fieldNames);
        for (String name : fieldNames) {
            tableColumns.add(new TableColumn(name, StringUtils.capitalize(name)));
        }
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

    public List<TableColumn> getTableColumns() {
        return new ArrayList<TableColumn>(tableColumns);
    }

    public TableColumn getTableColumnByFieldName(String name) {
        for (TableColumn tableColumn : this.tableColumns) {
            if (name.equals(tableColumn.getFieldName()))
                return tableColumn;
        }
        return null;
    }

}
