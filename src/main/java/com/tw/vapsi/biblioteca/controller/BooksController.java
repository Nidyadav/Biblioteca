package com.tw.vapsi.biblioteca.controller;

import com.tw.vapsi.biblioteca.exception.NoBooksAvailableException;
import com.tw.vapsi.biblioteca.model.Book;
import com.tw.vapsi.biblioteca.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/books")
public class BooksController {

    @Autowired
    private BookService bookService;

    @GetMapping("/list")
    public String books(Model model) {
        List<Book> book = new ArrayList<>();
        try {
            book = bookService.getBooks();
            model.addAttribute("book",book);
            model.addAttribute("hideTable","false");
        } catch (NoBooksAvailableException noBooksAvailableException) {
            model.addAttribute("errorMessage",noBooksAvailableException.getMessage());
            model.addAttribute("hideTable","true");
        }
        return "books";
    }

    @GetMapping("/checkout/{id}")
    public String checkOut(Model model) {
        model.addAttribute("errorMessage","Log in to Continue");
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
            model.addAttribute("hideTable","false");
        } catch (NoBooksAvailableException noBooksAvailableException) {
            model.addAttribute("errorMessage",noBooksAvailableException.getMessage());
            model.addAttribute("hideTable","true");
        }
        return "books";
    }
}
