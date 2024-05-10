package com.example.demo.repository;

import java.util.List;

import org.springframework.data.repository.Repository;

import com.example.demo.entity.Author;
import com.example.demo.entity.Book;
import com.example.demo.entity.Genre;
import com.example.demo.entity.Review;
import com.example.demo.entity.User;

public interface UserRepository extends Repository<User, String> {

    // save or update a user to db
    void save(User user);

    // find a string match to provided username
    User findByUsername(String username);

    User findByEmail(String email);

    // find similar/substring match to provided username
    List<User> findLikeUsername(String username);

    // find all users
    List<User> findAll();

    // delete user
    void delete(User user);

    // find a similar/substring match to provided name
    List<User> findLikeName(String name);

    List<Author> getUserFavoriteAuthors(User user);

    List<Genre> getUserLikedGenres(User user);

    List<Review> getUserReviews(User user);

    List<Book> getUserCheckedBooks(User user);

    double getUserLateFees(User user);

    void userLikesGenre(User user, Genre genre);

    void userUnlikesGenre(User user, Genre genre);

    void userFavoritesAuthor(User user, Author author);

    void userUnfavoritesAuthor(User user, Author author);

    void userRatesBook(User user, Book book, int stars);

    void removeUserRating(User user, Book book);

    int getUserBookRating(User user, Book book);
}
