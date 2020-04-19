package ru.nsu.levchugov.myjunitimpl.tests;

import ru.nsu.levchugov.myjunitimpl.annotations.Test;

import java.io.IOException;

import static ru.nsu.levchugov.myjunitimpl.assertions.Assert.assertEquals;
import static ru.nsu.levchugov.myjunitimpl.assertions.Assert.assertTrue;

public class Test1 {

    @Test
    public void test1() {
        assertEquals(4, 4);
    }

    @Test
    public void test2() {
        assertEquals(4, 5);
    }

    @Test(expectedException = IOException.class)
    public void test3() throws IOException {
        throw new IOException();
    }

    @Test(expectedException = IOException.class)
    public void test4() {
    }

    @Test
    public void test5() {
        assertTrue(5==5);
    }

    @Test
    public void test6() {
        assertTrue(5==7);
    }

}
