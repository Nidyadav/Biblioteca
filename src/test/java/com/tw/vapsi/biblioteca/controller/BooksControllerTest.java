package com.tw.vapsi.biblioteca.controller;


import com.tw.vapsi.biblioteca.controller.helper.ControllerTestHelper;
import com.tw.vapsi.biblioteca.exception.NoBooksAvailableException;
import com.tw.vapsi.biblioteca.model.Book;
import com.tw.vapsi.biblioteca.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


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

        mockMvc.perform(get("/books/list")).andExpect(status().isOk());

        verify(bookService,times(1)).getBooks();
    }

    @Test
    void shouldRedirectToCheckOutPage() throws Exception {
        mockMvc.perform(get("/books/checkout/1")).andExpect(status().isOk());
    }

    @Test
    void shouldRedirectToCreatePage() throws Exception {
        mockMvc.perform(get("/books/create")).andExpect(status().isOk());
    }

    @Test
    void shouldBeAbleToSaveTheBook() throws Exception {
        Book bookToBeAdded = new Book("War and Peace", "Tolstoy, Leo",
                "General",1, true,1865);
        bookToBeAdded.setId(1L);
        when(bookService.createBook(bookToBeAdded)).thenReturn(bookToBeAdded);

        mockMvc.perform(post("/books/save")).andExpect(status().isOk());
    }
}
