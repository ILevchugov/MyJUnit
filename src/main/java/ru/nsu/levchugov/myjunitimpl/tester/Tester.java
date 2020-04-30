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
        Method[] methods;
        Object testedClassObject;
        Class<?> testedClass;

        synchronized (testedClasses) {
            while (testedClasses.isEmpty()) {
                if (Thread.currentThread().isInterrupted()) {
                    return;
                }
            }
            testedClass = testedClasses.poll();
        }

        methods = testedClass.getMethods();
        testedClassObject = testedClass.getConstructor().newInstance();

        logger.info("test for class {} started", testedClass.getName());

        invokeBeforeMethod(methods, testedClassObject);

        invokeTestMethods(methods, testedClassObject);

        invokeAfterMethod(methods, testedClassObject);

        logger.info("test for class {} ended" + System.lineSeparator() + "||{} tests passed|| ||{} tests failed||",
                testedClass.getName(), passedTestsNum, failedTestsNum);
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
                        logger.info("test {} passed", method.getName());
                    } else {
                        failedTestsNum++;
                        logger.info("test {} failed", method.getName());
                    }
                } catch (Exception e) {
                    Class<? extends Throwable> thrownException = e.getCause().getClass();
                    if (thrownException == TestAssertionError.class) { //checking on assertion error
                        logger.info("test {} failed", method.getName());
                        failedTestsNum++;
                    } else if (!expectedException.isAssignableFrom(thrownException)) { //checking on expected exception
                        logger.info("test {} failed", method.getName());
                        failedTestsNum++;
                    } else {
                        logger.info("test {} passed", method.getName());
                        passedTestsNum++;
                    }
                }
            }
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
}



