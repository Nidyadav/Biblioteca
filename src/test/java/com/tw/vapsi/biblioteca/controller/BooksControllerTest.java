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
    void shouldRedirectToCheckOutPage() throws Exception {
        mockMvc.perform(get("/books/checkout/1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("checkout"));
    }

    @Test
    void shouldRedirectToCreatePage() throws Exception {
        mockMvc.perform(get("/books/create"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("book"))
                .andExpect(MockMvcResultMatchers.view().name("createbooks"));
    }

    @Test
    void shouldBeAbleToSaveTheBook() throws Exception {
        Book bookToBeAdded = new Book("War and Peace", "Tolstoy, Leo",
                "General",1, true,1865);
        bookService.createBook(bookToBeAdded);
        List<Book> bookList = Collections.singletonList(bookToBeAdded);
        when(bookService.getBooks()).thenReturn(bookList);

        mockMvc.perform(post("/books/save"))
                .andExpect(MockMvcResultMatchers.view().name("books"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("book"))
                .andExpect(MockMvcResultMatchers.model().attribute("book",bookList))
                .andExpect(status().isOk());
        verify(bookService,times(1)).getBooks();
    }
}
