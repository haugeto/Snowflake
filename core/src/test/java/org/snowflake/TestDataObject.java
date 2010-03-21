package org.snowflake;

import java.util.Date;

import org.junit.Ignore;

@Ignore
public class TestDataObject {

    Long id;

    String strField;

    Integer intField;

    Date dateField;

    public TestDataObject() {

    }

    public TestDataObject(Long id, String strField, Integer intField, Date dateField) {
        this.id = id;
        this.strField = strField;
        this.intField = intField;
        this.dateField = dateField;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStrField() {
        return strField;
    }

    public void setStrField(String strField) {
        this.strField = strField;
    }

    public Integer getIntField() {
        return intField;
    }

    public void setIntField(Integer intField) {
        this.intField = intField;
    }

    public Date getDateField() {
        return dateField;
    }

    public void setDateField(Date dateField) {
        this.dateField = dateField;
    }

}
