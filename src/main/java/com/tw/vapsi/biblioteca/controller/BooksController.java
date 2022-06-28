package com.tw.vapsi.biblioteca.controller;

import com.tw.vapsi.biblioteca.model.Book;
import com.tw.vapsi.biblioteca.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/api/v1")
public class BooksController {

    @Autowired
    private BookService bookService;

    @GetMapping("/books")
    public String books(Model model) {
        List<Book> book = bookService.getBooks();
        if (book.isEmpty()) {
            model.addAttribute("errorMessage","No Books Available in Library");
            model.addAttribute("hideTable","true");
        } else {
            model.addAttribute("book",book);
            model.addAttribute("hideTable","false");
        }
        return "books";
    }

    @GetMapping("/checkout/{id}")
    public String checkOut(Model model) {
        model.addAttribute("errorMessage","Log in to Continue");
        return "checkout";
    }
}
