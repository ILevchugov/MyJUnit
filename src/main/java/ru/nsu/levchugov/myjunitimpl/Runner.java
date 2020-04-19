package ru.nsu.levchugov.myjunitimpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.nsu.levchugov.myjunitimpl.tester.Tester;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;

public class Runner {
    private static final Logger logger = LoggerFactory.getLogger(Runner.class);

    public static void main(String[] args) {
        ArrayList<Thread> threads = new ArrayList<>();
        Queue<Class<?>> classes = new ArrayDeque<>();

        int threadsNum = 0;
        try {
            threadsNum = Integer.parseInt(args[0]);
            for (int i = 1; i < args.length; i++) {
                classes.add(Class.forName(args[i]));
            }
        } catch (ClassNotFoundException e) {
            logger.error("classes error", e);
            usage();
        } catch (NumberFormatException e) {
            logger.error("check num of threads", e);
            usage();
        }

        for (int i = 0; i < threadsNum; i++) {
            threads.add(new Thread(new Tester(classes)));
        }

        for (Thread thread : threads) {
            thread.start();
        }

        try {
            synchronized (Thread.currentThread()) {
                while (!classes.isEmpty()) {
                    Thread.currentThread().wait(100);
                }
                for (Thread t : threads) {
                    t.interrupt();
                }
            }
        } catch (Exception e) {
            logger.error("Some error, i dont know why", e);
        }

    }

    private static void usage() {
        logger.info(System.lineSeparator() + "Usage:" + System.lineSeparator() +
                "java -cp MyJUnit.jar;<tested-classes> ru.nsu.levchugov.myjunitimpl.Runner N class-name [class-name]*");
        System.exit(0);
    }
}
