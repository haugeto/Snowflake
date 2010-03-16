package org.snowflake.views.scaffolding;

public class TableColumn {

    String title;

    String fieldName;

    String link;

    public TableColumn(String fieldName, String title) {
        this.title = title;
        this.fieldName = fieldName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String caption) {
        this.title = caption;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public boolean hasLink() {
        return this.link != null;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

}
