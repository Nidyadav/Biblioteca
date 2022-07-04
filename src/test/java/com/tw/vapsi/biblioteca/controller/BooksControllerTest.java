package com.tw.vapsi.biblioteca.controller;


import com.tw.vapsi.biblioteca.controller.helper.ControllerTestHelper;
import com.tw.vapsi.biblioteca.exception.NoBooksAvailableException;
import com.tw.vapsi.biblioteca.model.Book;
import com.tw.vapsi.biblioteca.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = BooksController.class)
class BooksControllerTest extends ControllerTestHelper {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @Test
    @WithMockUser
    void shouldReturnListOfBooks() throws Exception {
        List<Book> listOfBooks = Arrays.asList(
                new Book("War and Peace", "Tolstoy, Leo", "General",1, true,1865),
                new Book("Northanger Abbey", "Austen, Jane","General",1, true,1814));
        when(bookService.getBooks()).thenReturn(listOfBooks);

        mockMvc.perform(get("/books/list")).andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("books"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("book"))
                .andExpect(MockMvcResultMatchers.model().attribute("book",listOfBooks));
        verify(bookService,times(1)).getBooks();
    }

    @Test
    void shouldThrowExceptionWhenNoBooksFound() throws Exception {
        NoBooksAvailableException noBooksAvailableException = new NoBooksAvailableException("No Books Available to display");
        when(bookService.getBooks()).thenThrow(noBooksAvailableException);

        mockMvc.perform(get("/books/list")).andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("books"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("errorMessage"))
                .andExpect(MockMvcResultMatchers.model().attribute("errorMessage",noBooksAvailableException.getMessage()));
        verify(bookService,times(1)).getBooks();
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "LIBRARIAN" })
    void shouldRedirectToCreatePage() throws Exception {
        mockMvc.perform(get("/books/create"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("book"))
                .andExpect(MockMvcResultMatchers.view().name("createbooks"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "LIBRARIAN" })
    void shouldBeAbleToSaveTheBook() throws Exception {
        Book bookToBeAdded = new Book("War and Peace", "Tolstoy, Leo",
                "General",1, true,1865);
        List<Book> bookList = Collections.singletonList(bookToBeAdded);
        when(bookService.getBooks()).thenReturn(bookList);

        mockMvc.perform(post("/books/save")
                        .param("name","War and Peace")
                        .param("author","Tolstoy, Leo")
                        .param("genre","General")
                        .param("yearOfPublish","1865"))
                .andExpect(MockMvcResultMatchers.view().name("books"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("book"))
                .andExpect(MockMvcResultMatchers.model().attribute("book",bookList))
                .andExpect(status().isOk());
        verify(bookService,times(1)).getBooks();
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "LIBRARIAN" })
    void shouldThrowExceptionForInvalidAttributeToSaveBook() throws Exception {

        mockMvc.perform(post("/books/save")
                        .param("name","")
                        .param("author","        ")
                        .param("genre","   ")
                        .param("yearOfPublish","0"))
                .andExpect(MockMvcResultMatchers.view().name("createbooks"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("nameErrorMessage"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("authorErrorMessage"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("genreErrorMessage"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("yearOfPublishErrorMessage"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldRedirectToLoginPageIfUserIsNotLoggedInWithErrorMessage() throws Exception {
        mockMvc.perform(get("/books/checkout/1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("login"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("errorMessage"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "USER" })
    void shouldRedirectToBooksPageWithErrorMessageIfBookIsNotAvailableToCheckout() throws Exception {
        Book book = new Book("War and Peace", "Tolstoy, Leo", "General",0, false,1865);
        book.setId(1L);
        when(bookService.getBookById(book.getId())).thenReturn(book);
        when(bookService.isBookAvailableForCheckout(1L)).thenReturn(false);

        mockMvc.perform(get("/books/checkout/1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("books"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("errorMessage"));

        verify(bookService, times(1)).isBookAvailableForCheckout(1L);
        verify(bookService, times(0)).checkOutBook(1,"admin");

    }

    @Test
    @WithMockUser(username = "admin", authorities = {"USER"})
    void shouldRedirectToBooksPageOnSuccessfulCheckout() throws Exception {
        Book book = new Book("War and Peace", "Tolstoy, Leo", "General", 1, true, 1865);
        when(bookService.isBookAvailableForCheckout(1)).thenReturn(true);
        when(bookService.checkOutBook(1l, "admin")).thenReturn(book);

        mockMvc.perform(get("/books/checkout/1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("successCheckoutMessage"))
                .andExpect(MockMvcResultMatchers.view().name("books"));

        verify(bookService, times(1)).checkOutBook(1,"admin");
    }
}
