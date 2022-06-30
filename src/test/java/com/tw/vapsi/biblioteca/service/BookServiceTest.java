package com.tw.vapsi.biblioteca.service;

import com.tw.vapsi.biblioteca.exception.NoBooksAvailableException;
import com.tw.vapsi.biblioteca.model.Book;
import com.tw.vapsi.biblioteca.repository.BooksRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class BookServiceTest {
    @Autowired
    private BookService bookService;

    @MockBean
    private BooksRepository booksRepository;

    @Test
    void shouldReturnListOfBooks() throws NoBooksAvailableException {
        List<Book> listOfBooks = Arrays.asList(
                new Book("War and Peace", "Tolstoy, Leo", "General",1, true,1865),
                new Book("Northanger Abbey", "Austen, Jane","General",1, true,1814));
        when(booksRepository.findAll()).thenReturn(listOfBooks);

        List<Book> books = bookService.getBooks();

        verify(booksRepository,times(1)).findAll();
        assertEquals(listOfBooks,books);
    }

    @Test
    void shouldThrowExceptionWhenNoBookIsAvailable() {
        List<Book> listOfBooks = new ArrayList<>();
        when(booksRepository.findAll()).thenReturn(listOfBooks);

        assertThrows(NoBooksAvailableException.class,()->bookService.getBooks());

        verify(booksRepository,times(1)).findAll();
    }

    @Test
    void shouldBeAbleToSaveTheBook() {
        Book book = new Book("War and Peace", "Tolstoy, Leo",
                "General",1, true,1865);
        Book createdBook = new Book("War and Peace", "Tolstoy, Leo",
                "General",1, true,1865);
        createdBook.setId(1L);
        when(booksRepository.save(book)).thenReturn(createdBook);

        Book actualCreated = bookService.createBook(book);

        verify(booksRepository,times(1)).save(book);
        assertEquals(createdBook,actualCreated);
    }
}