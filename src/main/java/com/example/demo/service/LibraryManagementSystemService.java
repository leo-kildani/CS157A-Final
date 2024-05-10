package com.example.demo.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.example.demo.entity.*;

public interface LibraryManagementSystemService {

        void addUser(String username, String password, String email, String firstName, String lastName, LocalDate dob,
                        String role);

        void updateUser(User user);

        User getUser(String username);

        User getUserByEmail(String email);

        List<User> getUsers();

        List<User> getUsersBySimilarUsername(String username);

        List<User> getUsersBySimilarName(String name);

        String getUserLateFees(User user);

        void deleteUser(User user);

        void addAuthor(String firstName, String lastName, String biography);

        List<Author> getUserFavoriteAuthor(User user);

        void userFavoritesAuthor(User user, Author author);

        void userUnfavoritesAuthor(User user, Author author);

        void updateAuthor(Author author);

        void deleteAuthor(Author author);

        void addBook(String isbn, String title, Author author, LocalDate publishDate, String description,
                        List<Genre> genres, int copies);

        void updateBook(Book book);

        Book getBook(String isbn);

        List<Book> getBooksBySimilarTitle(String title);

        List<Book> getBooksByAuthorLastName(String authorLastName);

        List<Book> getBooksByGenre(Genre genre);

        List<Book> getBooksByPublishDate(LocalDate date);

        List<Book> getBookByAppliedFilters(String title, String authorLastName, String genre/*
                                                                                             * , LocalDate publishDate
                                                                                             */);

        double getBookStarRating(Book book);

        boolean checkBookAvailable(Book book);

        void deleteBook(Book book);

        void addBookCheckout(User user, Book book);

        void deleteBookCheckout(User user, Book book);

        void addReview(String content, User user, Book book);

        void updateReview(Review review);

        List<Review> getUserReviews(User user);

        List<Review> getBookReviews(Book book);

        void deleteReview(Review review);

        void addGenre(String genreName, String overview);

        List<Genre> getUserLikedGenres(User user);

        void userLikesGenre(User user, Genre genre);

        void userUnlikesGenre(User user, Genre genre);

        void deleteGenre(Genre genre);

        List<Book> getUserRecommendations(User user);

        List<Book> getUserCheckedBooks(User user);

        List<Genre> getAllGenres();

        List<Genre> getGenreByBook(Book book);

        Author getAuthorByBook(Book book);

        Optional<Author> getAuthorById(int id);

        List<Book> getBookByAuthorId(int id);

        void userRatesBook(User user, Book book, int stars);

        void removeUserRating(User user, Book book);

        int getUserBookRating(User user, Book book);

        List<BookCheckout> findCheckoutByIsbn(String isbn);

        String formatLocalDate(LocalDate date, String pattern);

        Optional<Genre> getGenreByName(String name);
}
