package ru.nsu.levchugov.myjunitimpl.tester;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.nsu.levchugov.myjunitimpl.annotations.After;
import ru.nsu.levchugov.myjunitimpl.annotations.Before;
import ru.nsu.levchugov.myjunitimpl.annotations.Test;
import ru.nsu.levchugov.myjunitimpl.assertions.exceptions.EmptyException;
import ru.nsu.levchugov.myjunitimpl.assertions.exceptions.TestAssertionError;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Queue;


public class Tester implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(Tester.class);
    private static final String FAILED_MESSAGE = "test {} failed";
    private static final String PASSED_MESSAGE = "test {} passed";

    private final Queue<Class<?>> testedClasses;

    private int passedTestsNum = 0;
    private int failedTestsNum = 0;

    public Tester(Queue<Class<?>> testedClasses) {
        this.testedClasses = testedClasses;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                testClass();
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
                logger.error("Testing class problems", e);
                Thread.currentThread().interrupt();
            }
        }
    }

    private void testClass() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<?> testedClass;

        synchronized (testedClasses) {
            if (testedClasses.isEmpty()) {
                Thread.currentThread().interrupt();
                return;
            } else {
                testedClass = testedClasses.poll();
            }
        }

        Method[] methods = testedClass.getMethods();
        Object testedClassObject = testedClass.getConstructor().newInstance();

        logger.info("test for class {} started", testedClass.getName());

        if (!isValid(methods)) {
            logger.info("test for class {} declined because num of after or before methods > 1", testedClass.getName());
            return;
        }

        invokeBeforeMethod(methods, testedClassObject);

        invokeTestMethods(methods, testedClassObject);

        invokeAfterMethod(methods, testedClassObject);


        logger.info("test for class {} ended ", testedClass.getName());
        logger.info("||{} tests passed|| ||{} tests failed||",
                passedTestsNum, failedTestsNum);
    }

    private void invokeTestMethods(Method[] methods, Object object) {
        for (Method method : methods) {
            if (method.isAnnotationPresent(Test.class)) {
                Test test = method.getAnnotation(Test.class);
                Class<?> expectedException = test.expectedException();
                try {
                    method.invoke(object);
                    if (expectedException == EmptyException.class) {
                        passedTestsNum++;
                        logger.info(PASSED_MESSAGE, method.getName());
                    } else {
                        failedTestsNum++;
                        logger.info(FAILED_MESSAGE, method.getName());
                    }
                } catch (Exception e) {
                    processException(e, expectedException, method.getName());
                }
            }
        }

    }

    private void processException(Exception e, Class<?> expectedException, String methodName) {
        Class<? extends Throwable> thrownException = e.getCause().getClass();
        if (thrownException == TestAssertionError.class ||
                !expectedException.isAssignableFrom(thrownException)) {
            logger.info(FAILED_MESSAGE, methodName);
            failedTestsNum++;
        } else {
            logger.info(PASSED_MESSAGE, methodName);
            passedTestsNum++;
        }
    }

    /**
     * This method invoke the first @Before method that was found
     */
    private void invokeBeforeMethod(Method[] methods, Object object) throws InvocationTargetException, IllegalAccessException {
        for (Method method : methods) {
            if (method.isAnnotationPresent(Before.class)) {
                method.invoke(object);
                return;
            }
        }
    }

    /**
     * This method invoke the first @After method that was found
     */
    private void invokeAfterMethod(Method[] methods, Object object) throws InvocationTargetException, IllegalAccessException {
        for (Method method : methods) {
            if (method.isAnnotationPresent(After.class)) {
                method.invoke(object);
                return;
            }
        }
    }

    private boolean isValid(Method[] methods) {
        int afterMethodsCounter = 0;
        int beforeMethodsCounter = 0;
        for (Method method : methods) {
            if (method.isAnnotationPresent(After.class)) {
                afterMethodsCounter++;
            }
            if (method.isAnnotationPresent(Before.class)) {
                beforeMethodsCounter++;
            }
        }
        return afterMethodsCounter < 2 && beforeMethodsCounter < 2;
    }
}



