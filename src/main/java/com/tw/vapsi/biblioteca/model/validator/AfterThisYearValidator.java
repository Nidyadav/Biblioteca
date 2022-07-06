package com.tw.vapsi.biblioteca.model.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Calendar;
import java.util.Date;

public class AfterThisYearValidator implements ConstraintValidator<After1800AndBeforeNextYear, Integer> {
    @Override
    public void initialize(After1800AndBeforeNextYear constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        return value != null && (value >= 1800 && value <= (calendar.get(Calendar.YEAR)));
    }

}
