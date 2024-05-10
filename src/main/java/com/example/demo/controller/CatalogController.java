package com.example.demo.controller;

import com.example.demo.entity.Book;
import com.example.demo.entity.Genre;
import com.example.demo.entity.Author;
import com.example.demo.entity.Review;
import com.example.demo.entity.User;
import com.example.demo.service.LibraryManagementSystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class CatalogController {

    @Autowired
    private LibraryManagementSystemService libService;

    @Autowired
    private CurrentUser currentUser;

    private static final Logger logger = LoggerFactory.getLogger(CatalogController.class);

    @GetMapping("/catalog")
    public String loadPage(Model model) {
        if (currentUser.checkUser()) {
            model.addAttribute("user", currentUser.getCurrentUser());
            // model.addAttribute("userRole",
            // libService.findByUsername(user.getUsername()));
        } else {
            return "redirect:/loginPage";
        }

        List<Genre> genres = libService.getAllGenres();
        model.addAttribute("genres", genres);

        return "catalog";
    }

    @GetMapping("/catalog/search")
    public String searchBooks(@RequestParam(required = false) Optional<String> title,
            @RequestParam(required = false) Optional<String> author,
            @RequestParam(required = false) Optional<String> genre,
            Model model) {
        List<Book> books = libService.getBookByAppliedFilters(
                title.orElse(null),
                author.orElse(null),
                genre.orElse(null));

        model.addAttribute("books", books);
        model.addAttribute("service", libService);

        List<Genre> genres = libService.getAllGenres();
        model.addAttribute("genres", genres);

        List<Genre> resultGenres = new ArrayList<Genre>();
        List<Author> resultAuthors = new ArrayList<Author>();
        List<Integer> remainingCopies = new ArrayList<Integer>();
        books.forEach(book -> {
            resultGenres.add(libService.getGenreByBook(book).get(0));
            resultAuthors.add(libService.getAuthorByBook(book));
            remainingCopies.add(book.getCopies() - libService.findCheckoutByIsbn(book.getIsbn()).size());
        });
        model.addAttribute("resultGenres", resultGenres);
        model.addAttribute("resultAuthors", resultAuthors);
        model.addAttribute("remainingCopies", remainingCopies);

        return "catalog";
    }

    @GetMapping("/author")
    public String authorDetails(@RequestParam("id") int authorId, Model model) {
        Optional<Author> authorOptional = libService.getAuthorById(authorId);
        if (!authorOptional.isPresent()) {
            return "redirect:/catalog";
        }
        Author author = authorOptional.get();
        model.addAttribute("author", author);

        List<Book> booksByAuthor = libService.getBookByAuthorId(authorId);
        model.addAttribute("books", booksByAuthor);

        List<Genre> bookGenres = new ArrayList<Genre>();
        booksByAuthor.forEach(book -> {
            bookGenres.add(libService.getGenreByBook(book).get(0));
        });
        model.addAttribute("bookGenres", bookGenres);

        return "authorDetails"; // Name of the Thymeleaf template for author details
    }

    @GetMapping("/book")
    public String bookDetails(@RequestParam("isbn") String isbn, Model model) {
        User user = currentUser.getCurrentUser();
        Book book = libService.getBook(isbn);

        Author author = libService.getAuthorByBook(book);
        Boolean userFavedAuthorBool = libService.getUserFavoriteAuthor(user).contains(author);

        List<Genre> genres = libService.getGenreByBook(book);
        List<Genre> userLikedGenres = libService.getUserLikedGenres(user);
        List<Boolean> userLikedGenreBool = new ArrayList<Boolean>();
        genres.forEach(genre -> {
            if (userLikedGenres.contains(genre)) {
                userLikedGenreBool.add(true);
            } else {
                userLikedGenreBool.add(false);
            }
        });
        logger.info("userlikedgenres: " + userLikedGenres);
        logger.info("genrebool: " + userLikedGenreBool);

        int userRating = libService.getUserBookRating(user, book);
        double overallRating = Math.round(libService.getBookStarRating(book) * 10) / 10.0;
        List<Review> reviews = libService.getBookReviews(book);
        int remainingCopies = book.getCopies() - libService.findCheckoutByIsbn(book.getIsbn()).size();
        logger.info("bookDetails genres:" + genres);

        model.addAttribute("book", book);
        model.addAttribute("author", author);
        model.addAttribute("authorFavedBool", userFavedAuthorBool);
        model.addAttribute("genres", genres);
        model.addAttribute("genresLikedBool", userLikedGenreBool);
        model.addAttribute("userRating", userRating);
        model.addAttribute("overallRating", overallRating);
        model.addAttribute("reviews", reviews);
        model.addAttribute("remainingCopies", remainingCopies);

        return "bookDetails"; // Name of the Thymeleaf template for book details
    }

    @GetMapping("/catalog/logout")
    public String logout(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("logoutMessage", "You have been logged out successfully");
        return "redirect:/loginPage";
    }

    @GetMapping("/catalog/checkout")
    public String checkoutBook(@RequestParam("isbn") String isbn, RedirectAttributes redirectAttributes) {
        Book book = libService.getBook(isbn);
        if (!libService.checkBookAvailable(book)
                || libService.getUserCheckedBooks(currentUser.getCurrentUser()).contains(book)
                || Double.parseDouble(libService.getUserLateFees(currentUser.getCurrentUser())) > 0) {
            redirectAttributes.addFlashAttribute("checkErr", "Book is not available for checkout.");
            return "redirect:/catalog/search";
        }
        redirectAttributes.addFlashAttribute("book", book);
        return "redirect:/checkoutBook";
    }

}