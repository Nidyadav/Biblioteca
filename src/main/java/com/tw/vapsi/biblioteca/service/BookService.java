package com.tw.vapsi.biblioteca.service;

import com.tw.vapsi.biblioteca.exception.NoBooksAvailableException;
import com.tw.vapsi.biblioteca.model.Book;
import com.tw.vapsi.biblioteca.model.User;
import com.tw.vapsi.biblioteca.repository.BooksRepository;
import com.tw.vapsi.biblioteca.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    @Autowired
    private BooksRepository booksRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Book> getBooks() throws NoBooksAvailableException {
        List<Book> bookList = booksRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        if (bookList.isEmpty()) {
            throw new NoBooksAvailableException("No Books Available to display");
        }
        return bookList;
    }

    public boolean isBookAvailableForCheckout(long bookId) {
        Book book = booksRepository.findById(bookId).get();
        return book.isAvailable();
    }

    public Book createBook(Book book) {
        return booksRepository.save(book);
    }

    public void checkOutBook(long bookId, String userEmail) {
        User user = userRepository.findByEmail(userEmail).get();
        Book book = booksRepository.findById(bookId).get();
        booksRepository.checkOutBook(user.getUser_id(),bookId);
        book.setAvailable(false);
        booksRepository.save(book);
    }
}
