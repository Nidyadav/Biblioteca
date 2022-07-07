package com.tw.vapsi.biblioteca.service;

import com.tw.vapsi.biblioteca.exception.*;
import com.tw.vapsi.biblioteca.exception.bookcheckout.BookCheckOutException;
import com.tw.vapsi.biblioteca.exception.bookcheckout.BookNotAvailableForCheckOutException;
import com.tw.vapsi.biblioteca.exception.bookcheckout.MaximumBooksCheckedOutException;
import com.tw.vapsi.biblioteca.model.Book;
import com.tw.vapsi.biblioteca.model.User;
import com.tw.vapsi.biblioteca.repository.BooksRepository;
import com.tw.vapsi.biblioteca.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class BookService {

    @Autowired
    private BooksRepository booksRepository;

    @Autowired
    private UserRepository userRepository;

    public static final int MAX_CHECK_OUT_BOOK_LIMIT = 5;

    public List<Book> getBooks() throws NoBooksAvailableException {
        List<Book> bookList = booksRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        if (bookList.isEmpty()) {
            throw new NoBooksAvailableException("No Books Available to display");
        }
        return bookList;
    }

    public Book createBook(Book book) throws BookAlreadyExistsException {
        List<Book> booksWithSameName = booksRepository.findByNameAndYearOfPublish(book.getName(), book.getYearOfPublish());
        if (!booksWithSameName.isEmpty()) {
            throw new BookAlreadyExistsException("Book Detail already Exist");
        }
        return booksRepository.save(book);
    }

    public Book checkOutBook(long bookId, String userEmail) throws BookCheckOutException {
        Optional<Book> bookOptional = booksRepository.findById(bookId);
        Optional<User> userOptional = userRepository.findByEmail(userEmail);
        if (!bookOptional.isPresent())
            throw new BookNotFoundException("Book Not Found");
        if (!userOptional.isPresent())
            throw new InvalidUserException("Invalid User");
        Book book = bookOptional.get();
        User user = userOptional.get();
        if (!book.isAvailable())
            throw new BookNotAvailableForCheckOutException("Book: \"" + book.getName() + "\" Not Available For Checkout.");

        if (user.getBooks().size() >= MAX_CHECK_OUT_BOOK_LIMIT)
            throw new MaximumBooksCheckedOutException("You can check out maximum " + MAX_CHECK_OUT_BOOK_LIMIT + " books");

        book.setAvailable(false);
        user.getBooks().add(book);
        userRepository.save(user);
        return booksRepository.save(book);
    }

    public Set<Book> getMyBooks(String userEmail) throws NoBooksAvailableException {
        User user = userRepository.findById(
                userRepository.findByEmail(userEmail).get().getUserId()).get();
        Set<Book> checkedOutBooks =user.getBooks();
        if (checkedOutBooks.size()==0) {
            throw new NoBooksAvailableException("No books checked out by the user.");
        }
        return checkedOutBooks;
    }

    public Book returnCheckOutBook(long bookId, String userEmail) {
        Optional<User> userOptional = userRepository.findByEmail(userEmail);
        Optional<Book> bookOptional = booksRepository.findById(bookId);
        if (!bookOptional.isPresent())
            throw new BookNotFoundException("Book Not Found");
        if (!userOptional.isPresent())
            throw new InvalidUserException("Invalid User");
        User user = userOptional.get();
        Book book = bookOptional.get();
        if(book.isAvailable())
            throw new BookAlreadyReturnedException("Book "+book.getName()+" is already returned");
        user.getBooks().remove(book);
        userRepository.save(user);
        book.setAvailable(true);
        return booksRepository.save(book);

    }

    public List<Book> getAllCheckedOutBooks() throws NoBooksAvailableException {
        List<Book> books = booksRepository.findAll();
        books.removeIf(book -> book.getUser()==null);
        if (books.isEmpty()) {
            throw new NoBooksAvailableException("No books checked out by the user");
        }
        return books;
    }
}
