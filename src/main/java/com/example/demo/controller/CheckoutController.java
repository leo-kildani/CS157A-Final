package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entity.Book;
import com.example.demo.service.LibraryManagementSystemService;

@Controller
public class CheckoutController {

    @Autowired
    private LibraryManagementSystemService service;

    @Autowired
    private CurrentUser currentUser;

    @GetMapping("/checkoutBook")
    public String checkoutBook(@ModelAttribute("book") Book book) {
        if (!currentUser.checkUser()) {
            return "redirect:/loginPage";
        }
        service.addBookCheckout(currentUser.getCurrentUser(), book);
        return "redirect:/userprofile";
    }

    @GetMapping("/returnBook")
    public String returnBook(@RequestParam String isbn) {
        if (!currentUser.checkUser()) {
            return "redirect:/loginPage";
        }
        service.deleteBookCheckout(currentUser.getCurrentUser(), service.getBook(isbn));
        return "redirect:/userprofile";
    }
}