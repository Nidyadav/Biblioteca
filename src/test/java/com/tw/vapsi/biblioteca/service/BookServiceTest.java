package com.tw.vapsi.biblioteca.service;

import com.tw.vapsi.biblioteca.model.Book;
import com.tw.vapsi.biblioteca.repository.BooksRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
class BookServiceTest {
    @Autowired
    private BookService bookService;

    @MockBean
    private BooksRepository booksRepository;

    @Test
    void shouldReturnListOfBooks () throws Exception {
        List<Book> listOfBooks = Arrays.asList (
                new Book ("War and Peace", "Tolstoy, Leo", "General", 1, true, 1865),
                new Book ("Northanger Abbey", "Austen, Jane", "General", 1, true, 1814));
        when (booksRepository.findAll ()).thenReturn (listOfBooks);

        List<Book> books = bookService.getBooks ();

        verify (booksRepository, times (1)).findAll ();
        assertEquals (listOfBooks, books);
    }
}