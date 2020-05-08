package ru.nsu.levchugov.myjunitimpl.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 *Invoke only 1 method with this annotation
 *If there are more methods the tests will be canceled
 */
@Target({METHOD})
@Retention(RUNTIME)
public @interface Before {
}