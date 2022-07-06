package com.tw.vapsi.biblioteca.controller;

import com.tw.vapsi.biblioteca.exception.NoBooksAvailableException;
import com.tw.vapsi.biblioteca.exception.bookcheckout.BookCheckOutException;
import com.tw.vapsi.biblioteca.exception.bookcheckout.MaximumBooksCheckedOutException;
import com.tw.vapsi.biblioteca.model.Book;
import com.tw.vapsi.biblioteca.service.BookService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/books")
public class BooksController {

    private static final String ERROR_MESSAGE = "errorMessage";
    private static final String SUCCESS_MESSAGE = "successCheckoutMessage";
    @Autowired
    private BookService bookService;

    @GetMapping("/list")
    public String books (Model model) {
        List<Book> book;
        try {
            book = bookService.getBooks ();
            model.addAttribute ("book", book);
        } catch (NoBooksAvailableException noBooksAvailableException) {
            model.addAttribute (ERROR_MESSAGE, noBooksAvailableException.getMessage ());
        }
        return "books";
    }

    @GetMapping("/checkout/{id}")

    public String checkOut(@PathVariable("id") long bookId, Model model) throws MaximumBooksCheckedOutException {

        Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();

        if (loggedInUser instanceof AnonymousAuthenticationToken) {
            model.addAttribute (ERROR_MESSAGE, "Log in to Continue...");
            return ("login");
        }


        try {
            Book book = bookService.checkOutBook(bookId, loggedInUser.getName());
            model.addAttribute(SUCCESS_MESSAGE, "Book: \"" + book.getName() + "\" Checkedout Successfully");
        }
        catch(BookCheckOutException bookCheckOutException){
            model.addAttribute(ERROR_MESSAGE,bookCheckOutException.getMessage());
            return books(model);
        }
        return books(model);

    }

    @PreAuthorize("hasRole('LIBRARIAN')")
    @GetMapping("/create")
    public String goToCreatePage (Model model) {
        model.addAttribute ("book", new Book ());
        return "createbooks";
    }

    @PostMapping("/save")
    public String createBooks (@ModelAttribute("book") Book book, Model model) {
        if (!checkCreateBookAttributes (book, model)) {
            return "createbooks";
        }
        bookService.createBook (book);
        List<Book> books = bookService.getBooks ();
        model.addAttribute ("book", books);

        return "books";
    }

    private boolean checkCreateBookAttributes (Book book, Model model) {
        boolean isValidAttribute = true;

        if (Strings.isBlank(book.getName())) {
            model.addAttribute("nameErrorMessage", "Invalid Book Name");

            isValidAttribute = false;
        }
        if (book.getAuthor () == null || book.getAuthor ().trim ().isEmpty ()) {
            model.addAttribute ("authorErrorMessage", "Invalid Author Name");
            isValidAttribute = false;
        }
        if (book.getGenre () == null || book.getGenre ().trim ().isEmpty ()) {
            model.addAttribute ("genreErrorMessage", "Invalid Genre Name");
            isValidAttribute = false;
        }
        if (book.getYearOfPublish () == 0 || book.getYearOfPublish () < 0) {
            model.addAttribute ("yearOfPublishErrorMessage", "Invalid Year Of Publish");
            isValidAttribute = false;
        }
        return isValidAttribute;
    }

    @GetMapping("/mybooks")
    public String getMyBooks (Model model) {
        try {

            Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
            Set<Book> books = bookService.getMyBooks(loggedInUser.getName());
            model.addAttribute("myBook", books);

        } catch (NoBooksAvailableException exception) {
            model.addAttribute ("errorMessage", exception.getMessage ());
        }
        return "myBooks";
    }
}
