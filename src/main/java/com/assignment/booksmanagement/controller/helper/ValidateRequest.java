package com.assignment.booksmanagement.controller.helper;

import com.assignment.booksmanagement.exception.RequestNotValidException;
import com.assignment.booksmanagement.model.Book;

public class ValidateRequest {
    public static void validateBook(Book book) {
        if (book.getIsbn() == null || book.getAuthor() == null || book.getTags() == null || book.getTitle() == null) {
            throw new RequestNotValidException(
                            "One of the attributes is missing! Please check the request and submit again");
        }

    }
}
