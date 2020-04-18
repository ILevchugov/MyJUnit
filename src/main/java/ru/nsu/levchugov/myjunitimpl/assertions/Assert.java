package ru.nsu.levchugov.myjunitimpl.assertions;

import ru.nsu.levchugov.myjunitimpl.assertions.exceptions.TestAssertionError;

public final class Assert {

    public static void assertTrue(boolean trueCondition) {
        if (!trueCondition) {
            throw new TestAssertionError();
        }
    }

    public static void assertEquals(int a, int b) {
        if (a!=b) {
            throw new TestAssertionError();
        }
    }

}
