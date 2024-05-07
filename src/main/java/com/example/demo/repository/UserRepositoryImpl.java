package com.example.demo.repository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.demo.entity.Book;
import com.example.demo.entity.BookCheckout;
import com.example.demo.entity.Genre;
import com.example.demo.entity.Review;
import com.example.demo.entity.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private EntityManager entityManager;

    public UserRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void save(User user) {
        if (entityManager.find(User.class, user.getUsername()) != null) {
            entityManager.merge(user);
        } else {
            entityManager.persist(user);
        }
    }

    @Override
    public User findByUsername(String username) {
        return entityManager.find(User.class, username);
    }

    @Override
    public List<User> findLikeUsername(String username) {
        String queryString = "SELECT * FROM users U WHERE U.username LIKE :usernamePattern";
        Query query = entityManager
            .createNativeQuery(queryString, User.class)
            .setParameter("usernamePattern", "%" + username + "%");
        return (List<User>) query.getResultList();
    }

    @Override
    public List<User> findAll() {
        String queryString = "SELECT * FROM users";
        Query query = entityManager.createNativeQuery(queryString, User.class);
        return (List<User>) query.getResultList();
    }

    @Override
    @Transactional
    public void delete(User user) {
        entityManager.remove(user);
    }

    @Override
    public List<User> findLikeName(String name) {
        String queryString = "SELECT DISTINCT * FROM users U WHERE U.first_name = :name OR U.last_name = :name";
        Query query = entityManager
            .createNativeQuery(queryString, User.class)
            .setParameter("name", name);
        return (List<User>) query.getResultList();
    }

    @Override
    public List<Genre> getUserLikedGenres(User user) {
        String queryString = "SELECT G.* FROM user_likes_genres UG INNER JOIN genres G ON UG.genre_name = G.genre_name WHERE UG.username = :username";
        Query query = entityManager
            .createNativeQuery(queryString, Genre.class)
            .setParameter("username", user.getUsername());
        return (List<Genre>) query.getResultList();
    }

    @Override
    public List<Review> getUserReviews(User user) {
        String queryString = "SELECT * FROM reviews R WHERE R.username = :username";
        Query query = entityManager
            .createNativeQuery(queryString, Review.class)
            .setParameter("username", user.getUsername());
        return (List<Review>) query.getResultList();
    }

    @Override
    public List<Book> getUserCheckedBooks(User user) {
        String queryString = "SELECT B.* FROM book_checkouts BC INNER JOIN books B ON BC.isbn = B.isbn WHERE BC.username = :username";
        Query query = entityManager
            .createNativeQuery(queryString, Book.class)
            .setParameter("username", user.getUsername());
        return (List<Book>) query.getResultList();
    }

    @Override
    public double getUserLateFees(User user) {
        String bookCheckoutQueryString = "SELECT * FROM book_checkouts BC WHERE BC.username = :username";
        Query bookCheckoutQuery = entityManager
            .createNativeQuery(bookCheckoutQueryString, BookCheckout.class)
            .setParameter("username", user.getUsername());
        List<BookCheckout> bookCheckouts = (List<BookCheckout>) bookCheckoutQuery.getResultList();
        return bookCheckouts.stream()
            .map(checkout -> {
                long weekDifference = ChronoUnit.WEEKS.between(checkout.getDueDate(), LocalDate.now());
                double overdueFees = 0.0;
                double principal = 15.0;
                final double overdueInterest = 0.05;

                for (int i = 0; i < weekDifference; i++) {
                    overdueFees += principal;
                    principal = principal + (principal * overdueInterest);
                } 

                return overdueFees;
            })
            .mapToDouble(Double::doubleValue)
            .sum();
    }
}