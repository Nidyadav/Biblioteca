package com.tw.vapsi.biblioteca.service;

import com.tw.vapsi.biblioteca.exception.NoBooksAvailableException;
import com.tw.vapsi.biblioteca.model.Book;
import com.tw.vapsi.biblioteca.repository.BooksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    @Autowired
    private BooksRepository booksRepository;

    public List<Book> getBooks() throws NoBooksAvailableException {
        List<Book> bookList = booksRepository.findAll();
        if (bookList.isEmpty()) {
            throw new NoBooksAvailableException("No Books Available to display");
        }
        return bookList;
    }

    public Book createBook(Book book) {
        return booksRepository.save(book);
    }
}
