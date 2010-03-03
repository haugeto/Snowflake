package org.snowflake.utils;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;
import org.snowflake.utils.ReflectionHelpers;

public class ReflectionHelpersTest {

    @Test
    public void testDeduceFieldNameFromGetter() {
        assertEquals("myField1", ReflectionHelpers.deduceFieldNameFromGetter("getMyField1"));
        assertEquals("a", ReflectionHelpers.deduceFieldNameFromGetter("getA"));
    }

    @Test
    public void testDeduceFieldName() {

        String fieldName2 = ReflectionHelpers.deduceFieldNameFromVariable("AnotherSimilarField");
        // assertEquals("someField", fieldName1);
        assertEquals("anotherSimilarField", fieldName2);
    }

    @Test
    public void testDeduceSetterName() {
        String fieldName2 = ReflectionHelpers.deduceSetterName("anotherSimilarField");
        assertEquals("setAnotherSimilarField", fieldName2);
    }

    @Test
    public void testDeduceGetterName() {
        String fieldName2 = ReflectionHelpers.deduceGetterName("anotherSimilarField");
        assertEquals("getAnotherSimilarField", fieldName2);
    }

    @Test
    public void testInvokeSetterForVariable() throws Exception {
        String variableName = "AnotherSimilarField";
        String value = "heyHEY!";

        TestDTO testDTO = new TestDTO();

        ReflectionHelpers.invokeSetterForVariable(variableName, value, String.class, testDTO);
        assertEquals(value, testDTO.anotherSimilarField);
    }

    @Test
    public void testFieldType() throws Exception {
        assertEquals(Date.class, ReflectionHelpers.fieldType(TestDTO.class, "dateField"));
        assertEquals(String.class, ReflectionHelpers.fieldType(TestDTO.class, "someField"));
        assertEquals(Integer.class, ReflectionHelpers.fieldType(TestDTO.class, "id"));
    }

    @Test
    public void testResolveId() throws Exception {
        TestDTO dto = new TestDTO();
        dto.setId(42);
        assertEquals(42, ReflectionHelpers.resolveId(dto));
    }

    class TestDTO {

        Integer id;

        String someField;

        String anotherSimilarField;

        Date dateField;

        public Date getDateField() {
            return dateField;
        }

        public void setDateField(Date dateField) {
            this.dateField = dateField;
        }

        public String getSomeField() {
            return someField;
        }

        public void setSomeField(String someField) {
            this.someField = someField;
        }

        public String getAnotherSimilarField() {
            return anotherSimilarField;
        }

        public void setAnotherSimilarField(String anotherSimilarField) {
            this.anotherSimilarField = anotherSimilarField;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

    }

}
