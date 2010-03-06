package org.snowflake.argumentinjection;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.snowflake.Answer;
import org.snowflake.Question;

public class ArgumentsBuilderTest {

    Question question;
    Answer answer;
    Map<?, ?> map;
    int i;
    CustomType customType;

    @Before
    public void createArgumentInstances() {
        this.question = new Question();
        this.answer = new Answer();
        this.map = new HashMap<Object, Object>();
        this.i = 42;
        this.customType = new CustomType();
    }

    @Test
    public void testBuildArguments() throws Exception {
        Method method = TargetClass.class.getMethod("targetMethod", Question.class, Answer.class, Map.class, int.class,
                CustomType.class);
        ArgumentsBuilder builder = new ArgumentsBuilder(new TestProducer());
        Object[] args = builder.buildArguments(method);
        assertNotNull(args);
        assertEquals(5, args.length);
        assertSame(question, args[0]);
        assertSame(answer, args[1]);
        assertSame(map, args[2]);
        assertSame(i, args[3]);
        assertSame(customType, args[4]);
    }

    class TargetClass {

        public void targetMethod(Question q, Answer a, Map<?, ?> m, int id, CustomType ct) {

        }

    }

    class CustomType {
    }

    class TestProducer implements ArgumentProducer {

        @Override
        public Object getArgumentOfType(Class<?> type) {
            if (type == Question.class)
                return question;
            if (type == Answer.class)
                return answer;
            if (type == Map.class)
                return map;
            if (type == int.class)
                return i;
            if (type == CustomType.class)
                return customType;

            return null;
        }

    }
}
