package ru.nsu.levchugov.myjunitimpl.tests;

import ru.nsu.levchugov.myjunitimpl.annotations.Before;
import ru.nsu.levchugov.myjunitimpl.annotations.Test;

import java.io.IOException;

import static ru.nsu.levchugov.myjunitimpl.assertions.Assert.assertEquals;
import static ru.nsu.levchugov.myjunitimpl.assertions.Assert.assertTrue;

public class Test1 {

    private double a;

    @Before
    public void beforeMethod() {
        a = 1.0 + 2.0 + 3.44444001;
    }

    //should waiting 10 seconds and then passed
    @Test
    public void test1() {
        try {
            Thread.currentThread().sleep(10000);
            assertEquals(4, 4);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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

    //should be failed
    @Test
    public void test7() {
        assertEquals(a, 6.44444, 0.000000001);
    }

    //should be passed
    @Test
    public void test8() {
        assertEquals(a, 6.44444, 0.00000001);
    }

}
