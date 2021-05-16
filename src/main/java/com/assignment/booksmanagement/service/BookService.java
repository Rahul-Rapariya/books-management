package com.assignment.booksmanagement.service;

import com.assignment.booksmanagement.model.Book;

public interface BookService {
    Book addBook(Book book);

    Book getBook(long isbn);

    Book updateBook(long isbn, Book book);

    void deleteBook(long id);
}
