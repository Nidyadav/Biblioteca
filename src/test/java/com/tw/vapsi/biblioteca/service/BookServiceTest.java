package com.tw.vapsi.biblioteca.service;

import com.tw.vapsi.biblioteca.exception.*;
import com.tw.vapsi.biblioteca.exception.bookcheckout.BookNotAvailableForCheckOutException;
import com.tw.vapsi.biblioteca.exception.bookcheckout.MaximumBooksCheckedOutException;
import com.tw.vapsi.biblioteca.model.Book;
import com.tw.vapsi.biblioteca.model.User;
import com.tw.vapsi.biblioteca.repository.BooksRepository;
import com.tw.vapsi.biblioteca.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

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
                new Book("War and Peace", "Tolstoy, Leo", "General", 1, true, 1865),
                new Book("Northanger Abbey", "Austen, Jane", "General", 1, true, 1814));
        when(booksRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))).thenReturn(listOfBooks);

        List<Book> books = bookService.getBooks();

        verify(booksRepository, times(1)).findAll(Sort.by(Sort.Direction.ASC, "id"));
        assertEquals(listOfBooks, books);
    }

    @Test
    void shouldThrowExceptionWhenNoBookIsAvailable() {
        List<Book> listOfBooks = new ArrayList<>();
        when(booksRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))).thenReturn(listOfBooks);

        assertThrows(NoBooksAvailableException.class, () -> bookService.getBooks());

        verify(booksRepository, times(1)).findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    @Test
    void shouldBeAbleToSaveTheBook() throws BookAlreadyExistsException {
        Book book = new Book("War and Peace", "Tolstoy, Leo",
                "General", 1, true, 1865);
        Book createdBook = new Book("War and Peace", "Tolstoy, Leo",
                "General", 1, true, 1865);
        createdBook.setId(1L);
        when(booksRepository.findByNameAndYearOfPublish(book.getName(),book.getYearOfPublish()))
                .thenReturn(new ArrayList<>());
        when(booksRepository.save(book)).thenReturn(createdBook);

        Book actualCreated = bookService.createBook(book);

        verify(booksRepository, times(1)).save(book);
        assertEquals(createdBook, actualCreated);
    }

    @Test
    void shouldThrowExceptionWhenBookDetailsAlreadyAvailable() throws BookAlreadyExistsException {
        Book book = new Book("War and Peace", "Tolstoy, Leo",
                "General", 1, true, 1865);
        Book createdBook = new Book("War and Peace", "Tolstoy, Leo",
                "General", 1, true, 1865);
        createdBook.setId(1L);
        List<Book> bookList = Arrays.asList(book);
        when(booksRepository.findByNameAndYearOfPublish(book.getName(),book.getYearOfPublish())).thenReturn(bookList);
        //when(booksRepository.save(book)).thenReturn(createdBook);

        assertThrows(BookAlreadyExistsException.class, ()->bookService.createBook(book));

        verify(booksRepository, times(0)).save(book);
    }

    @Test
    void shouldBeAbleToCheckoutBookWhenBookIsAvailable() throws MaximumBooksCheckedOutException {
        Book book = new Book("War and Peace", "Tolstoy, Leo", "General", 1, true, 1865);
        book.setId(1L);
        Book checkedOutBook = new Book("War and Peace", "Tolstoy, Leo", "General", 1, false, 1865);
        checkedOutBook.setId(1L);

        User user = new User(1L, "admin", "admin", "admin@gmail.com", "pwd");
        when(userRepository.findByEmail("admin@gmail.com")).thenReturn(Optional.of(user));
        when(booksRepository.findById(1L)).thenReturn(Optional.of(book));
        when(userRepository.save(user)).thenReturn(user);
        when(booksRepository.save(checkedOutBook)).thenReturn(checkedOutBook);

        bookService.checkOutBook(1L,"admin@gmail.com");

        verify(booksRepository,times(1)).findById(1L);
        verify(booksRepository, times(1)).save(checkedOutBook);
        verify(userRepository, times(1)).save(user);
        assertEquals(1, userRepository.findByEmail("admin@gmail.com").get().getBooks().size());
        assertTrue(userRepository.findByEmail("admin@gmail.com").get().getBooks().contains(book));
    }

    @Test
    void shouldThrowExceptionWhenUserReachesMaximumCheckoutLimit() {
        Book book1 = new Book("War and Peace1", "Tolstoy, Leo","General", 1, false, 1865);
        book1.setId(1L);
        Book book2 = new Book("War and Peace2", "Tolstoy, Leo","General", 1, false, 1865);
        book2.setId(2L);
        Book book3 = new Book("War and Peace3", "Tolstoy, Leo","General", 1, false, 1865);
        book3.setId(3L);
        Book book4 = new Book("War and Peace4", "Tolstoy, Leo","General", 1, false, 1865);
        book4.setId(4L);
        Book book5 = new Book("War and Peace5", "Tolstoy, Leo","General", 1, false, 1865);
        book5.setId(5L);
        Book book6 = new Book("War and Peace6", "Tolstoy, Leo","General", 1, true, 1865);
        book5.setId(6L);
        User user = new User(1L, "admin", "admin", "admin@gmail.com", "pwd");
        user.getBooks().add(book1);
        user.getBooks().add(book2);
        user.getBooks().add(book3);
        user.getBooks().add(book4);
        user.getBooks().add(book5);
        when(userRepository.findByEmail("admin@gmail.com")).thenReturn(Optional.of(user));
        when(booksRepository.findById(6L)).thenReturn(Optional.of(book6));

        assertThrows(MaximumBooksCheckedOutException.class, () -> bookService.checkOutBook(6L, "admin@gmail.com"));
    }

    @Test
    void shouldThrowExceptionIfBookIdIsNotValidWhenCheckOut(){
        assertThrows(BookNotFoundException.class, () -> bookService.checkOutBook(6L, "admin@gmail.com"));
    }
    @Test
    void shouldThrowExceptionIfUserIsNotValidWhenCheckOut(){
        Book book = new Book("War and Peace1", "Tolstoy, Leo","General", 1, false, 1865);
        book.setId(1L);
        when(booksRepository.findById(1L)).thenReturn(Optional.of(book));

        assertThrows(InvalidUserException.class, () -> bookService.checkOutBook(1L, "admin@gmail.com"));
    }

    @Test
    void shouldThrowExceptionWhenBookIsNotAvailableForCheckOut(){
        Book book = new Book("War and Peace1", "Tolstoy, Leo","General", 1, false, 1865);
        book.setId(1L);
        User user = new User(1L, "admin", "admin", "admin@gmail.com", "pwd");
        when(booksRepository.findById(1L)).thenReturn(Optional.of(book));
        when(userRepository.findByEmail("admin@gmail.com")).thenReturn(Optional.of(user));


        assertThrows(BookNotAvailableForCheckOutException.class, () -> bookService.checkOutBook(1L, "admin@gmail.com"));
    }

    @Test
    void shouldReturnBooksCheckedOutByTheUser() throws NoBooksAvailableException {
        Set<Book> books = new HashSet<>();
        Book book = new Book("War and Peace", "Tolstoy, Leo",
                "General", 1, true, 1865);
        book.setId(1L);
        books.add(book);
        User user = new User(1L, "admin", "admin", "admin@gmail.com", "pwd");
        user.setBooks(books);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("admin@gmail.com")).thenReturn(Optional.of(user));

        Set<Book> checkedOutBooks = bookService.getMyBooks("admin@gmail.com");

        assertEquals(user.getBooks(),checkedOutBooks);
        verify(userRepository,times(1)).findByEmail("admin@gmail.com");
        verify(userRepository,times(1)).findById(1L);
    }

    @Test
    void shouldThrowErrorWhenNoBooksCheckedOutByTheUser() {
        User user = new User(1L, "admin", "admin", "admin@gmail.com", "pwd");
        user.setBooks(new HashSet<>());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("admin@gmail.com")).thenReturn(Optional.of(user));

        assertThrows(NoBooksAvailableException.class,()->bookService.getMyBooks("admin@gmail.com"));

        verify(userRepository,times(1)).findByEmail("admin@gmail.com");
        verify(userRepository,times(1)).findById(1L);
    }

    @Test
    void shouldGiveTheNameOfBookAndSuccessMessageOnReturn() throws Exception {
        Book book = new Book("War and Peace", "Tolstoy, Leo", "General", 1, false, 1865);
        book.setId(1L);
        Book returnedBook = new Book("War and Peace", "Tolstoy, Leo", "General", 1, true, 1865);
        returnedBook.setId(1L);

        User user = new User(1L, "admin", "admin", "admin@gmail.com", "pwd");
        user.getBooks().add(book);
        when(userRepository.findByEmail("admin@gmail.com")).thenReturn(Optional.of(user));
        when(booksRepository.findById(1L)).thenReturn(Optional.of(book));
        when(userRepository.save(user)).thenReturn(user);
        when(booksRepository.save(returnedBook)).thenReturn(returnedBook);

        bookService.returnCheckOutBook(1L,"admin@gmail.com");

        verify(booksRepository,times(1)).findById(1L);
        verify(booksRepository, times(1)).save(returnedBook);
        verify(userRepository, times(1)).save(user);
        assertEquals(0, userRepository.findByEmail("admin@gmail.com").get().getBooks().size());
        assertFalse(userRepository.findByEmail("admin@gmail.com").get().getBooks().contains(book));
    }


    @Test
    void shouldShowErrorMessageWhenNoBooksAvailableToReturn() {
        User user = new User(1L, "admin", "admin", "admin@gmail.com", "pwd");
        user.setBooks(new HashSet<>());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("admin@gmail.com")).thenReturn(Optional.of(user));

        assertThrows(NoBooksAvailableException.class,()->bookService.getMyBooks("admin@gmail.com"));

        verify(userRepository,times(1)).findByEmail("admin@gmail.com");
        verify(userRepository,times(1)).findById(1L);
    }


    @Test
    void shouldReturnBookIfBookIsAvailableInMyBooks()
    {
        User user = new User(1L, "admin", "admin", "admin@gmail.com", "pwd");
        Set<Book> books = new HashSet<>();
        Book book = new Book("War and Peace", "Tolstoy, Leo",
                "General", 1, true, 1865);
        book.setId(1L);
        books.add(book);
        user.setBooks(books);
        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        assertEquals(books,user.getBooks());
    }

    @Test
    void shouldThrowExceptionWhenBookIsAlreadyReturned(){
        Book book = new Book("War and Peace1", "Tolstoy, Leo","General", 1, true, 1865);
        book.setId(1L);
        User user = new User(1L, "admin", "admin", "admin@gmail.com", "pwd");
        when(booksRepository.findById(1L)).thenReturn(Optional.of(book));
        when(userRepository.findByEmail("admin@gmail.com")).thenReturn(Optional.of(user));


        assertThrows(BookAlreadyReturnedException.class, () -> bookService.returnCheckOutBook(1L, "admin@gmail.com"));
    }

    @Test
    void shouldReturnAllCheckedOutBooksForLibrarian() throws NoBooksAvailableException {
        List<Book> listOfBooks =new ArrayList<>();

        Book warBook = new Book("War and Peace", "Tolstoy, Leo", "General", 1, true, 1865);
        Book janeBook = new Book("Northanger Abbey", "Austen, Jane", "General", 1, true, 1814);
        warBook.setUser(new User("admin","a","admin@gmail.com","abc"));
        listOfBooks.add(warBook);
        listOfBooks.add(janeBook);
        when(booksRepository.findAll()).thenReturn(listOfBooks);

        List<Book> books = bookService.getAllCheckedOutBooks();

        verify(booksRepository, times(1)).findAll();
        assertEquals(listOfBooks, books);
    }

    @Test
    void shouldThrowExceptionWhenNoBooksAreCheckedOut()  {
        when(booksRepository.findAll()).thenReturn(new ArrayList<>());

        assertThrows(NoBooksAvailableException.class,()->bookService.getAllCheckedOutBooks());

        verify(booksRepository, times(1)).findAll();
    }

    @Test
    void shouldThrowExceptionIfBookIdIsNotValidWhenReturningBook(){
        assertThrows(BookNotFoundException.class, () -> bookService.returnCheckOutBook(6L, "admin@gmail.com"));
    }
    @Test
    void shouldThrowExceptionIfUserIsNotValidWhenReturningBook(){
        Book book = new Book("War and Peace1", "Tolstoy, Leo","General", 1, false, 1865);
        book.setId(1L);
        when(booksRepository.findById(1L)).thenReturn(Optional.of(book));

        assertThrows(InvalidUserException.class, () -> bookService.returnCheckOutBook(1L, "admin@gmail.com"));
    }
}