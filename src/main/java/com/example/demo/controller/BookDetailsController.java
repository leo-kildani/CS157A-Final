package com.example.demo.controller;

import com.example.demo.entity.Author;
import com.example.demo.entity.Book;
import com.example.demo.entity.Genre;
import com.example.demo.entity.User;
import com.example.demo.service.LibraryManagementSystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class BookDetailsController {

    @Autowired
    private LibraryManagementSystemService libService;

    @Autowired
    private CurrentUser currentUser;

    private static final Logger logger = LoggerFactory.getLogger(CatalogController.class);

    @PostMapping("/rate")
    public String rateBook(@RequestParam("isbn") String isbn,
            @RequestParam("rating") int rating) {
        User user = currentUser.getCurrentUser();
        Book book = libService.getBook(isbn);
        if (libService.getUserBookRating(user, book) >= 0) {
            libService.removeUserRating(user, book);
        }
        libService.userRatesBook(user, book, rating);
        return "redirect:/book?isbn=" + isbn;
    }

    @PostMapping("/submitReview")
    public String reviewBook(@RequestParam("isbn") String isbn,
            @RequestParam("reviewContent") String content) {
        User user = currentUser.getCurrentUser();
        Book book = libService.getBook(isbn);
        libService.addReview(content, user, book);
        return "redirect:/book?isbn=" + isbn;
    }

    @PostMapping("/likeGenre")
    public String likeGenre(@RequestParam("genreName") String genreName,
            @RequestParam(value = "liked", defaultValue = "false") boolean liked,
            @RequestParam("isbn") String isbn) {
        User user = currentUser.getCurrentUser();
        Optional<Genre> optionalGenre = libService.getGenreByName(genreName);
        if (!optionalGenre.isPresent()) {
            return "redirect:/book?isbn=" + isbn;
        }
        Genre genre = optionalGenre.get();
        if (liked) {
            libService.userLikesGenre(user, genre);
            logger.info("liked");
        } else {
            libService.userUnlikesGenre(user, genre);
            logger.info("unliked");
        }

        return "redirect:/book?isbn=" + isbn;
    }

    @PostMapping("/favoriteAuthor")
    public String favAuthor(@RequestParam("authorId") int authorId,
            @RequestParam(value = "favorited", defaultValue = "false") boolean liked,
            @RequestParam("isbn") String isbn) {
        User user = currentUser.getCurrentUser();
        Optional<Author> optionalAuthor = libService.getAuthorById(authorId);
        if (!optionalAuthor.isPresent()) {
            return "redirect:/book?isbn=" + isbn;
        }
        Author author = optionalAuthor.get();
        if (liked) {
            libService.userFavoritesAuthor(user, author);
            logger.info("faved");
        } else {
            libService.userUnfavoritesAuthor(user, author);
            logger.info("unfaved");
        }

        return "redirect:/book?isbn=" + isbn;
    }
}