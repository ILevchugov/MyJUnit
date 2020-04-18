package ru.nsu.levchugov.myjunitimpl.annotations;

import ru.nsu.levchugov.myjunitimpl.assertions.exceptions.EmptyException;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({METHOD})
@Retention(RUNTIME)
public @interface Test {
    Class<? extends Throwable> expectedException() default EmptyException.class;
}
