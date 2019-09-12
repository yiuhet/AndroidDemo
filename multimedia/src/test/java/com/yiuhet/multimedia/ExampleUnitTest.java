package com.yiuhet.multimedia;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        StringBuilder builder = new StringBuilder();
        builder.append("1123213");
        builder.setLength(0);
        builder.append("1123213");
        builder.setLength(0);
        builder.append("1123213");
        System.out.println(builder.toString());
        assertEquals(4, 2 + 2);
    }
}