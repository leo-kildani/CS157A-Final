package com.example.demo.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "book_checkouts")
@NoArgsConstructor
public @Data class BookCheckout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "checkout_id")
    private int checkoutId;

    @Column(name = "username")
    private String username;

    @Column(name = "isbn")
    private String isbn;

    @Column(name = "checkout_date")
    private LocalDate checkoutDate;

    @Transient
    @Setter(AccessLevel.NONE)
    private LocalDate dueDate;

    public BookCheckout(String username, String isbn) {
        this.username = username;
        this.isbn = isbn;
        this.checkoutDate = LocalDate.now();
    }

    public LocalDate getDueDate() {
        if (dueDate == null) {
            dueDate = checkoutDate.plusDays(30);
        }
        return dueDate;
    }
}
