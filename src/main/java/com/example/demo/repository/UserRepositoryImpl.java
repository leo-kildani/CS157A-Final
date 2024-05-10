package com.example.demo.repository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Author;
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

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private JdbcTemplate jdbcTemplate;

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
    public User findByEmail(String email) {
        String queryString = "SELECT * FROM users U WHERE U.email = :email";
        Query query = entityManager
                .createNativeQuery(queryString, User.class)
                .setParameter("email", email);
        List<User> result = (List<User>) query.getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<User> findLikeUsername(String username) {
        String queryString = "SELECT * FROM users U WHERE U.username LIKE :usernamePattern";
        Query query = entityManager
                .createNativeQuery(queryString, User.class)
                .setParameter("usernamePattern", "%" + username + "%");
        return (List<User>) query.getResultList();
    }

    @SuppressWarnings("unchecked")
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

    @SuppressWarnings("unchecked")
    @Override
    public List<User> findLikeName(String name) {
        String queryString = "SELECT DISTINCT * FROM users U WHERE U.first_name = :name OR U.last_name = :name";
        Query query = entityManager
                .createNativeQuery(queryString, User.class)
                .setParameter("name", name);
        return (List<User>) query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Genre> getUserLikedGenres(User user) {
        String queryString = "SELECT G.* FROM user_likes_genres UG INNER JOIN genres G ON UG.genre_name = G.genre_name WHERE UG.username = :username";
        Query query = entityManager
                .createNativeQuery(queryString, Genre.class)
                .setParameter("username", user.getUsername());
        return (List<Genre>) query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Review> getUserReviews(User user) {
        String queryString = "SELECT * FROM reviews R WHERE R.username = :username";
        Query query = entityManager
                .createNativeQuery(queryString, Review.class)
                .setParameter("username", user.getUsername());
        return (List<Review>) query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Book> getUserCheckedBooks(User user) {
        String queryString = "SELECT B.* FROM book_checkouts BC INNER JOIN books B ON BC.isbn = B.isbn WHERE BC.username = :username";
        Query query = entityManager
                .createNativeQuery(queryString, Book.class)
                .setParameter("username", user.getUsername());
        return (List<Book>) query.getResultList();
    }

    @SuppressWarnings("unchecked")
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

    @Override
    public void userLikesGenre(User user, Genre genre) {
        String insertString = "INSERT INTO user_likes_genres (username, genre_name) VALUES (?, ?)";
        jdbcTemplate.update(insertString, user.getUsername(), genre.getName());
    }

    @Override
    public void userUnlikesGenre(User user, Genre genre) {
        String deleteString = "DELETE FROM user_likes_genres WHERE username = ? AND genre_name = ?";
        jdbcTemplate.update(deleteString, user.getUsername(), genre.getName());
    }

    @Override
    public void userFavoritesAuthor(User user, Author author) {
        String insertString = "INSERT INTO user_favorites_author (username, author_id) VALUES (?, ?)";
        jdbcTemplate.update(insertString, user.getUsername(), author.getAuthorId());
    }

    @Override
    public void userUnfavoritesAuthor(User user, Author author) {
        String deleteString = "DELETE FROM user_favorites_author WHERE username = ? AND author_id = ?";
        jdbcTemplate.update(deleteString, user.getUsername(), author.getAuthorId());
    }

    @Override
    public void userRatesBook(User user, Book book, int stars) {
        String insertString = "INSERT INTO user_rates_book (username, isbn, stars) VALUES (?, ?, ?)";
        jdbcTemplate.update(insertString, user.getUsername(), book.getIsbn(), stars);
    }

    @Override
    public void removeUserRating(User user, Book book) {
        String deleteString = "DELETE FROM user_rates_book WHERE username = ? AND isbn = ?";
        jdbcTemplate.update(deleteString, user.getUsername(), book.getIsbn());
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Author> getUserFavoriteAuthors(User user) {
        String queryString = "SELECT A.* FROM user_favorites_author UA INNER JOIN authors A ON UA.author_id = A.author_id WHERE UA.username = :username";
        Query query = entityManager
                .createNativeQuery(queryString, Author.class)
                .setParameter("username", user.getUsername());
        return (List<Author>) query.getResultList();
    }

    @Override
    public int getUserBookRating(User user, Book book) {
        String queryString = "SELECT stars FROM user_rates_book WHERE username = :username AND isbn = :isbn";
        Query query = entityManager.createNativeQuery(queryString)
                .setParameter("username", user.getUsername())
                .setParameter("isbn", book.getIsbn());

        List<?> results = query.getResultList();
        if (!results.isEmpty()) {
            Number rating = (Number) results.get(0);
            return rating.intValue();
        }
        return -1; // no rating found
    }

}