package ru.nsu.levchugov.myjunitimpl.tests;

import ru.nsu.levchugov.myjunitimpl.annotations.Test;

import java.io.IOException;

public class Test2 {

    //passed
    @Test(expectedException = Exception.class)
    public void test1() throws Exception {
        throw new Exception();
    }

    //failed
    @Test(expectedException = Exception.class)
    public void test2()  {
    }

    //passed
    @Test(expectedException = Exception.class)
    public void test3() throws IOException {
        throw new IOException();
    }

}
