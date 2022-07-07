package com.tw.vapsi.biblioteca.controller;


import com.tw.vapsi.biblioteca.controller.helper.ControllerTestHelper;
import com.tw.vapsi.biblioteca.exception.*;
import com.tw.vapsi.biblioteca.exception.bookcheckout.BookNotAvailableForCheckOutException;
import com.tw.vapsi.biblioteca.exception.bookcheckout.MaximumBooksCheckedOutException;
import com.tw.vapsi.biblioteca.model.Book;
import com.tw.vapsi.biblioteca.model.User;
import com.tw.vapsi.biblioteca.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    @WithMockUser(username = "admin@gmail.com", authorities = { "LIBRARIAN" })
    void shouldRedirectToCreatePage() throws Exception {
        mockMvc.perform(get("/books/create"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("book"))
                .andExpect(MockMvcResultMatchers.view().name("createbooks"));
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", authorities = { "LIBRARIAN" })
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
    @WithMockUser(username = "admin@gmail.com", authorities = { "LIBRARIAN" })
    void shouldBeAbleToSaveTheBookButUnableToView() throws Exception {
        Book bookToBeAdded = new Book("War and Peace", "Tolstoy, Leo",
                "General",1, true,1865);
        bookToBeAdded.setId(1L);
        when(bookService.createBook(bookToBeAdded)).thenReturn(bookToBeAdded);
        when(bookService.getBooks()).thenThrow(new NoBooksAvailableException("No books available"));

        mockMvc.perform(post("/books/save")
                        .param("name","War and Peace")
                        .param("author","Tolstoy, Leo")
                        .param("genre","General")
                        .param("yearOfPublish","1865"))
                .andExpect(MockMvcResultMatchers.view().name("books"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("errorMessage"))
                .andExpect(status().isOk());
        verify(bookService,times(1)).getBooks();
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", authorities = { "LIBRARIAN" })
    void shouldThrowExceptionWhenBookNameAndYearAlreadyExistsInSystem() throws Exception {
        Book bookToBeAdded = new Book("War and Peace", "Tolstoy, Leo",
                "General",1, true,1865);
        List<Book> bookList = Collections.singletonList(bookToBeAdded);

        when(bookService.createBook(bookToBeAdded)).thenThrow(new BookAlreadyExistsException("Book details already exists"));

        mockMvc.perform(post("/books/save")
                        .param("name","War and Peace")
                        .param("author","Tolstoy, Leo")
                        .param("genre","General")
                        .param("yearOfPublish","1865"))
                .andExpect(MockMvcResultMatchers.view().name("createbooks"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("errorMessage"))
                .andExpect(status().isOk());
        verify(bookService,times(1)).createBook(bookToBeAdded);
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", authorities = { "LIBRARIAN" })
    void shouldThrowExceptionForInvalidAttributeToSaveBook() throws Exception {

        mockMvc.perform(post("/books/save")
                        .param("name","")
                        .param("author","        ")
                        .param("genre","   ")
                        .param("yearOfPublish","0"))
                .andExpect(MockMvcResultMatchers.view().name("createbooks"))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("book","name","NotBlank"))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("book","author","NotBlank"))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("book","genre","NotBlank"))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("book","yearOfPublish","After1800AndBeforeNextYear"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldRedirectToLoginIfUserIsNotLoggedInWhenCheckOut() throws Exception {
        mockMvc.perform(get("/books/checkout/1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("login"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("errorMessage"));
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", authorities = { "USER" })
    void shouldRedirectToBooksPageWithErrorMessageIfBookIsNotAvailableToCheckout() throws Exception {
        Book book = new Book("War and Peace", "Tolstoy, Leo", "General",1, false,1865);
        book.setId(1L);
        BookNotAvailableForCheckOutException bookNotAvailableForCheckOutException =
                new BookNotAvailableForCheckOutException("Book: \""+book.getName()+"\" Not Available For Checkout.");
        when(bookService.checkOutBook(1L,"admin@gmail.com")).thenThrow(bookNotAvailableForCheckOutException);

        mockMvc.perform(get("/books/checkout/1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("books"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("errorMessage"))
                .andExpect(MockMvcResultMatchers.model().attribute("errorMessage",bookNotAvailableForCheckOutException.getMessage()));

        verify(bookService, times(1)).checkOutBook(1,"admin@gmail.com");

    }

    @Test
    @WithMockUser(username = "admin@gmail.com", authorities = {"USER"})
    void shouldRedirectToBooksPageOnSuccessfulCheckout() throws Exception {
        Book book = new Book("War and Peace", "Tolstoy, Leo", "General", 1, true, 1865);
        when(bookService.checkOutBook(1L, "admin@gmail.com")).thenReturn(book);

        mockMvc.perform(get("/books/checkout/1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("successCheckoutMessage"))
                .andExpect(MockMvcResultMatchers.view().name("books"));

        verify(bookService, times(1)).checkOutBook(1,"admin@gmail.com");
    }

    @Test
    @WithMockUser(username = "user1", authorities = {"USER"})
    void givenOneBookCheckedOutByUserShouldSeeOneBookUnderMyBooks() throws Exception {
        Set<Book> books = new HashSet<>();
            books.add(new Book("War and Peace", "Tolstoy, Leo", "General", 1, true, 1865));
        when(bookService.getMyBooks("user1")).thenReturn(books);

        mockMvc.perform(get("/books/mybooks"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("mybooks"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("myBook"));

        verify(bookService, times(1)).getMyBooks("user1");
    }

    @Test
    @WithMockUser(username = "user1", authorities = {"USER"})
    void givenFiveBookCheckedOutByUserShouldSeeFiveBooksUnderMybooks() throws Exception {
        Set<Book> books = new HashSet<>();
        books.add(new Book("War and Peace", "Tolstoy, Leo", "General", 1, true, 1865));
        books.add(new Book("Northanger Abbey", "Austen, Jane","General",1, true,1814));
        books.add(new Book("Harry Potter", "JK Rowling","Fiction",1, true,1990));
        books.add(new Book("Games Of Strategy", "Van alys","Management",1, true,1994));
        books.add(new Book("Harry Potter", "JK Rowling","Fiction",1, true,1992));
        when(bookService.getMyBooks("user1")).thenReturn(books);

        mockMvc.perform(get("/books/mybooks"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("mybooks"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("myBook"));

        assertEquals(5,books.size());
        verify(bookService, times(1)).getMyBooks("user1");
    }

    @Test
    @WithMockUser(username = "user1", authorities = {"USER"})
    void shouldSeeMessageWhenNoBooksCheckedOutByUser() throws Exception {
        Set<Book> books = new HashSet<>();
        NoBooksAvailableException noBooksAvailableException = new NoBooksAvailableException("No Books Available to display");
        when(bookService.getMyBooks("user1")).thenReturn(books);
        when(bookService.getMyBooks("user1")).thenThrow(noBooksAvailableException);

        mockMvc.perform(get("/books/mybooks"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("mybooks"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("errorMessage"));

        verify(bookService, times(1)).getMyBooks("user1");
    }
    @WithMockUser(username = "admin@gmail.com", authorities = { "USER" })
    void shouldRedirectToBooksPageWithErrorMessageIfUserReachesMaximumCheckoutLimit() throws Exception {
        MaximumBooksCheckedOutException maximumBooksCheckedOutException = new MaximumBooksCheckedOutException("You can check out "+ BookService.MAX_CHECK_OUT_BOOK_LIMIT +" maximum  books");
        when(bookService.checkOutBook(1,"admin@gmail.com")).thenThrow(maximumBooksCheckedOutException);
        mockMvc.perform(get("/books/checkout/1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("books"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("errorMessage"))
                .andExpect(MockMvcResultMatchers.model().attribute("errorMessage",maximumBooksCheckedOutException.getMessage()));

        verify(bookService, times(1)).checkOutBook(1,"admin@gmail.com");

    }
    @Test
    @WithMockUser(username = "admin@gmail.com", authorities = {"USER"})
    void shouldSeeMessageWhenNoBooksInMyBooks() throws Exception {
        Book book = new Book("War and Peace", "Tolstoy, Leo", "General",0, true,1865);
        book.setId(1L);
        BookAlreadyReturnedException bookAlreadyReturnedException = new BookAlreadyReturnedException("Book "+book.getName()+" is already returned");
        when(bookService.returnCheckOutBook(1L,"admin@gmail.com")).thenThrow(bookAlreadyReturnedException);

        mockMvc.perform(get("/books/returnbook/1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("mybooks"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("errorMessage"))
                .andExpect(MockMvcResultMatchers.model().attribute("errorMessage",bookAlreadyReturnedException.getMessage()));

        verify(bookService, times(1)).returnCheckOutBook(1,"admin@gmail.com");

    }

    @Test
    @WithMockUser(username = "admin@gmail.com", authorities = {"USER"})
    void shouldRedirectToMyBooksPageOnSuccessfulReturn() throws Exception {
        Book book = new Book("War and Peace", "Tolstoy, Leo", "General", 1, false, 1865);
        when(bookService.returnCheckOutBook(1L, "admin@gmail.com")).thenReturn(book);

        mockMvc.perform(get("/books/returnbook/1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("successCheckoutMessage"))
                .andExpect(MockMvcResultMatchers.view().name("mybooks"));

        verify(bookService, times(1)).returnCheckOutBook(1,"admin@gmail.com");
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", authorities = {"LIBRARIAN"})
    void shouldReturnAllBooksCheckedOutByTheUser() throws Exception {
        Book book = new Book("War and Peace", "Tolstoy, Leo",
                "General",1, true,1865);
        book.setUser(new User("admin","A","admin@gmail.com","abc"));
        List<Book> bookList = Collections.singletonList(book);
        when(bookService.getAllCheckedOutBooks()).thenReturn(bookList);

        mockMvc.perform(get("/books/allcheckedOutBooks"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("book"))
                .andExpect(MockMvcResultMatchers.view().name("allcheckedoutbooks"));

        verify(bookService, times(1)).getAllCheckedOutBooks();
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", authorities = {"LIBRARIAN"})
    void shouldShowMessageWhenNoBooksCheckedOut() throws Exception {
        when(bookService.getAllCheckedOutBooks()).thenThrow(new NoBooksAvailableException("No books checked out by user"));

        mockMvc.perform(get("/books/allcheckedOutBooks"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("errorMessage"))
                .andExpect(MockMvcResultMatchers.view().name("allcheckedoutbooks"));
        verify(bookService, times(1)).getAllCheckedOutBooks();
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", authorities = { "USER" })
    void shouldRedirectToBooksPageWithErrorMessageIfBookIsNotAvailableWhenCheckout() throws Exception {
        BookNotFoundException bookNotFoundException =
                new BookNotFoundException("Book Not Found");
        when(bookService.checkOutBook(1L,"admin@gmail.com")).thenThrow(bookNotFoundException);

        mockMvc.perform(get("/books/checkout/1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("books"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("errorMessage"))
                .andExpect(MockMvcResultMatchers.model().attribute("errorMessage",bookNotFoundException.getMessage()));

        verify(bookService, times(1)).checkOutBook(1,"admin@gmail.com");

    }
    @Test
    @WithMockUser(username = "admin@gmail.com", authorities = { "USER" })
    void shouldRedirectToBooksPageWithErrorMessageIfBookIsNotAvailableWhenReturn() throws Exception {
        BookNotFoundException bookNotFoundException =
                new BookNotFoundException("Book Not Found");
        when(bookService.returnCheckOutBook(1L,"admin@gmail.com")).thenThrow(bookNotFoundException);

        mockMvc.perform(get("/books/returnbook/1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("mybooks"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("errorMessage"))
                .andExpect(MockMvcResultMatchers.model().attribute("errorMessage",bookNotFoundException.getMessage()));

        verify(bookService, times(1)).returnCheckOutBook(1,"admin@gmail.com");

    }

}
