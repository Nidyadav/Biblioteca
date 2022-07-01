package com.tw.vapsi.biblioteca.controller;

import com.tw.vapsi.biblioteca.exception.NoBooksAvailableException;
import com.tw.vapsi.biblioteca.model.Book;
import com.tw.vapsi.biblioteca.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/books")
public class BooksController {

    @Autowired
    private BookService bookService;

    private static final String ERROR_MESSAGE = "errorMessage";

    @GetMapping("/list")
    public String books(Model model) {
        List<Book> book;
        try {
            book = bookService.getBooks();
            model.addAttribute("book", book);
        } catch (NoBooksAvailableException noBooksAvailableException) {
            model.addAttribute(ERROR_MESSAGE, noBooksAvailableException.getMessage());
        }
        return "books";
    }

    @GetMapping("/checkout/{id}")
    public String checkOut(Model model) {
        model.addAttribute(ERROR_MESSAGE, "Log in to Continue");
        return "checkout";
    }

    @PreAuthorize("hasRole('LIBRARIAN')")
    @GetMapping("/create")
    public String goToCreatePage(Model model) {
        model.addAttribute("book", new Book());
        return "createbooks";
    }

    @PostMapping("/save")
    public String createBooks(@ModelAttribute("book") Book book, Model model) {
        if (!checkCreateBookAttributes(book, model)) {
            return "createbooks";
        }
        bookService.createBook(book);
        List<Book> books = bookService.getBooks();
        model.addAttribute("book", books);

        return "books";
    }

    private boolean checkCreateBookAttributes(Book book, Model model) {
        boolean isValidAttribute = true;
        if (book.getName() == null || book.getName().trim().isEmpty()) {
            model.addAttribute("nameErrorMessage", "Invalid Book Name");
            isValidAttribute = false;
        }
        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            model.addAttribute("authorErrorMessage", "Invalid Author Name");
            isValidAttribute = false;
        }
        if (book.getGenre() == null || book.getGenre().trim().isEmpty()) {
            model.addAttribute("genreErrorMessage", "Invalid Genre Name");
            isValidAttribute = false;
        }
        if (book.getYearOfPublish() == 0 || book.getYearOfPublish() < 0) {
            model.addAttribute("yearOfPublishErrorMessage", "Invalid Year Of Publish");
            isValidAttribute = false;
        }
        return isValidAttribute;
    }
}
