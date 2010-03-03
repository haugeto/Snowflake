package org.snowflake.utils;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * Prints readable and pretty messages to the developers console.
 * 
 * @author haugeto
 */
public class Console {

    public static final int CONSOLE_WIDTH = 100;

    public static final int CONSOLE_LONG_WIDTH = 120;

    public static final String INDENT = "    ";
    
    static final char HR_ELEMENT = '-';

    static final String HR;

    public static PrintStream out = System.out;

    static Map<String, Object> variables = new HashMap<String, Object>();

    static {
        String temp = "";
        for (int i = 0; i < CONSOLE_WIDTH; i++) {
            temp += HR_ELEMENT;
        }
        HR = temp;

        ConsoleListener inputListener = new ConsoleListener();
        new Thread(inputListener).start();
    }

    public static synchronized void hr() {
        out.println(HR);
    }

    public static synchronized void br() {
        out.println();
    }

    public static synchronized void center(String str) {
        out.println(StringUtils.center(str, CONSOLE_WIDTH));
    }

    public static synchronized void justify(String left, String right) {
        justify(left, right, ' ');
    }

    public static synchronized void justify(String left, String right, char padChar) {
        justify(left, right, padChar, CONSOLE_WIDTH);
    }

    public static synchronized void justifyWide(String left, String right, char padChar) {
        justify(left, right, padChar, CONSOLE_LONG_WIDTH);
    }

    public static synchronized void justify(String left, String right, char padChar, int width) {
        String result;
        if (left == null)
            left = "";
        if (right == null)
            right = "";
        if (left.length() + right.length() > width)
            result = left + ' ' + right;
        else {
            result = left + StringUtils.leftPad(right, width - left.length(), padChar);
        }
        out.println(result);
    }

    public static synchronized void println(String str) {
        out.println(str);
    }

    public static void put(String key, Object value) {
        variables.put(key, value);
    }

}
