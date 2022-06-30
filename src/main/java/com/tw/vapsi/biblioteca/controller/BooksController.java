package com.tw.vapsi.biblioteca.controller;

import com.tw.vapsi.biblioteca.exception.NoBooksAvailableException;
import com.tw.vapsi.biblioteca.model.Book;
import com.tw.vapsi.biblioteca.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
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
            model.addAttribute("book",book);
        } catch (NoBooksAvailableException noBooksAvailableException) {
            model.addAttribute(ERROR_MESSAGE,noBooksAvailableException.getMessage());
        }
        return "books";
    }

    @GetMapping("/checkout/{id}")
    public String checkOut(Model model) {
        model.addAttribute(ERROR_MESSAGE,"Log in to Continue");
        return "checkout";
    }

    @GetMapping("/create")
    public String goToCreatePage(Model model) {
        model.addAttribute("book",new Book());
        return "createbooks";
    }

    @PostMapping("/save")
    public String createBooks(@ModelAttribute("book") Book book,Model model) {

        try {
            bookService.createBook(book);
            List<Book> books = bookService.getBooks();
            model.addAttribute("book",books);
        } catch (NoBooksAvailableException noBooksAvailableException) {
            model.addAttribute(ERROR_MESSAGE,noBooksAvailableException.getMessage());
        }
        return "books";
    }
}
