package com.example.demo.controller;

import com.example.demo.entity.Book;
import com.example.demo.service.LibraryManagementSystemService;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import com.example.demo.entity.User;
import com.example.demo.entity.Author;
import com.example.demo.entity.Genre;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserProfileController {

    @Autowired
    private LibraryManagementSystemService service;

    @Autowired
    private CurrentUser currentUser;

    private static final Logger logger = LoggerFactory.getLogger(UserProfileController.class);

    @GetMapping("/userprofile")
    public String getUserProfile(Model model) {
        if (currentUser.checkUser()) {
            model.addAttribute("user", currentUser.getCurrentUser());
            model.addAttribute("userFees", service.getUserLateFees(currentUser.getCurrentUser()));
            // model.addAttribute("userRole",
            // libService.findByUsername(user.getUsername()));
        } else {
            return "redirect:/loginPage";
        }

        return "userprofile";
    }

    @GetMapping("/userprofile/favoriteAuthors")
    public String getFavAuthors(Model model) {
        User user = currentUser.getCurrentUser();
        logger.info("Current User: " + user.getUsername());
        List<Author> authors = service.getUserFavoriteAuthor(user);
        model.addAttribute("favAuthors", authors);
        logger.info("Returning authors: " + authors);
        return "fragments/favoriteAuthors";
    }

    @GetMapping("/userprofile/likedGenres")
    public String getLikedGenres(Model model) {
        User user = currentUser.getCurrentUser();
        logger.info("Current User: " + user.getUsername());
        List<Genre> genres = service.getUserLikedGenres(user);
        model.addAttribute("likedGenres", genres);
        logger.info("Returning genres: " + genres);
        return "fragments/likedGenres";
    }

    @GetMapping("/userprofile/borrowedBooks")
    public String getBorrowedBooks(Model model) {
        User user = currentUser.getCurrentUser();
        logger.info("Current User: " + user.getUsername());
        List<Book> books = service.getUserCheckedBooks(user);
        model.addAttribute("user", user);
        model.addAttribute("borrowedBooks", books);
        model.addAttribute("service", service);
        logger.info("Returning books: " + books);
        return "fragments/borrowedBooks";
    }

    @GetMapping("/userprofile/recommendations")
    public String getRecs(Model model) {
        User user = currentUser.getCurrentUser();
        logger.info("Current User: " + user.getUsername());
        List<Book> recs = service.getUserRecommendations(user);
        model.addAttribute("recs", recs);
        logger.info("Returning recs: " + recs);
        return "fragments/recommendations";
    }
}
