package com.tw.vapsi.biblioteca.model.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
@Constraint(validatedBy = AfterThisYearValidator.class)
public @interface After1800AndBeforeNextYear{
    String message() default "{After1800AndBeforeNextYear.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
