package com.tw.vapsi.biblioteca.service;

import com.tw.vapsi.biblioteca.exception.NoBooksAvailableException;
import com.tw.vapsi.biblioteca.model.Book;
import com.tw.vapsi.biblioteca.model.User;
import com.tw.vapsi.biblioteca.repository.BooksRepository;
import com.tw.vapsi.biblioteca.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class BookServiceTest {
    @Autowired
    private BookService bookService;

    @MockBean
    private BooksRepository booksRepository;

    @MockBean
    private UserRepository userRepository;


    @Test
    void shouldReturnListOfBooks() throws NoBooksAvailableException {
        List<Book> listOfBooks = Arrays.asList(
                new Book("War and Peace", "Tolstoy, Leo", "General",1, true,1865),
                new Book("Northanger Abbey", "Austen, Jane","General",1, true,1814));
        when(booksRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))).thenReturn(listOfBooks);

        List<Book> books = bookService.getBooks();

        verify(booksRepository,times(1)).findAll(Sort.by(Sort.Direction.ASC, "id"));
        assertEquals(listOfBooks,books);
    }

    @Test
    void shouldThrowExceptionWhenNoBookIsAvailable() {
        List<Book> listOfBooks = new ArrayList<>();
        when(booksRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))).thenReturn(listOfBooks);

        assertThrows(NoBooksAvailableException.class,()->bookService.getBooks());

        verify(booksRepository,times(1)).findAll(Sort.by(Sort.Direction.ASC, "id"));
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

    @Test
    void shouldBeAbleToCheckoutBookWhenBookIsAvailable() {
        Book book = new Book("War and Peace", "Tolstoy, Leo",
                "General", 1, true, 1865);
        book.setId(1L);
        Book checkedOutBook = new Book("War and Peace", "Tolstoy, Leo",
                "General", 1, false, 1865);
        checkedOutBook.setId(1L);

        User user = new User(1L, "admin", "admin", "admin@gmail.com", "pwd");
        when(userRepository.findByEmail("admin@gmail.com")).thenReturn(Optional.of(user));
        when(booksRepository.findById(1L)).thenReturn(Optional.of(book));

        doNothing().when(booksRepository).checkOutBook(1, 1L);
        when(booksRepository.save(book)).thenReturn(checkedOutBook);

        Book actualCheckedOutBook = bookService.checkOutBook(1L, "admin@gmail.com");

        verify(booksRepository, times(1)).save(book);
        assertEquals(checkedOutBook, actualCheckedOutBook);
    }

    @Test
    void shouldReturnTrueIfBookIsAvailableForCheckout() {
        Book book = new Book("War and Peace", "Tolstoy, Leo",
                "General", 1, true, 1865);
        book.setId(1L);
        when(booksRepository.findById(1L)).thenReturn(Optional.of(book));

        assertTrue(bookService.isBookAvailableForCheckout(1L));
    }

    @Test
    void shouldReturnFalseIfBookIsAvailableForCheckout() {
        Book book = new Book("War and Peace", "Tolstoy, Leo",
                "General", 1, false, 1865);
        book.setId(1L);
        when(booksRepository.findById(1L)).thenReturn(Optional.of(book));
        assertFalse(bookService.isBookAvailableForCheckout(1L));
    }

    @Test
    void shouldReturnABookWhenGetBookByIdIsCalled() {
        Book book = new Book("War and Peace", "Tolstoy, Leo",
                "General", 1, true, 1865);
        book.setId(1L);
        when(booksRepository.findById(1L)).thenReturn(Optional.of(book));
        assertEquals(bookService.getBookById(1L),book);
    }
}