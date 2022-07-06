package com.tw.vapsi.biblioteca.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BookTest {

    private static Validator validator;
    @BeforeAll
    public static void setupValidatorInstance() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }


    @Test
    void whenEmptyBookNameThenOneConstraintViolation() {
        Book book = new Book("","Chetan","Fiction",1,true,1998);
        Set<ConstraintViolation<Book>> violations = validator.validate(book);

        assertThat(violations).hasSize(1);
    }

    @Test
    void whenYearLessThan1800ThenOneConstraintViolation() {
        Book book = new Book("Two States","Chetan","Fiction",1,true,1799);
        Set<ConstraintViolation<Book>> violations = validator.validate(book);

        assertThat(violations).hasSize(1);
    }

    @Test
    void whenNullBookNameThenOneConstraintViolation(){
        Book book = new Book(null,"Chetan","Fiction",1,true,1998);
        Set<ConstraintViolation<Book>> violations = validator.validate(book);

        assertThat(violations).hasSize(1);
    }

    @Test
    void whenEmptyBookNameAuthorAndGenreThenThreeConstraintViolation() {
        Book book = new Book("","","",1,true,1998);
        Set<ConstraintViolation<Book>> violations = validator.validate(book);

        assertThat(violations).hasSize(3);
    }


}