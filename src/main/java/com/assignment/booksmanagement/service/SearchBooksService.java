package com.assignment.booksmanagement.service;

import com.assignment.booksmanagement.controller.helper.PagingResponse;
import com.assignment.booksmanagement.model.Book;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;

public interface SearchBooksService {

    PagingResponse searchBook(Specification<Book> spec, HttpHeaders headers, Sort sort);
}
