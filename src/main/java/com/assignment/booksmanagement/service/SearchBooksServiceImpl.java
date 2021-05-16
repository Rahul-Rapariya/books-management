package com.assignment.booksmanagement.service;

import com.assignment.booksmanagement.controller.BookResource;
import com.assignment.booksmanagement.controller.helper.PagingHeaders;
import com.assignment.booksmanagement.controller.helper.PagingResponse;
import com.assignment.booksmanagement.model.Book;
import com.assignment.booksmanagement.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class SearchBooksServiceImpl implements SearchBooksService {
    private static final Logger logger = LoggerFactory.getLogger(SearchBooksServiceImpl.class);

    @Autowired BookRepository bookRepository;

    @Override
    public PagingResponse searchBook(Specification<Book> spec, HttpHeaders headers, Sort sort) {
        if (isRequestPaged(headers)) {
            return search(spec, buildPageRequest(headers, sort));
        } else {
            List<Book> entities = search(spec, sort);
            return new PagingResponse((long) entities.size(), 0L, 0L, 0L, 0L, entities);
        }
    }

    public PagingResponse search(Specification<Book> spec, Pageable pageable) {
        Page<Book> page = bookRepository.findAll(spec, pageable);
        List<Book> content = page.getContent();
        return new PagingResponse(page.getTotalElements(), (long) page.getNumber(), (long) page.getNumberOfElements(),
                        pageable.getOffset(), (long) page.getTotalPages(), content);
    }

    public List<Book> search(Specification<Book> spec, Sort sort) {
        return bookRepository.findAll(spec, sort);
    }

    private Pageable buildPageRequest(HttpHeaders headers, Sort sort) {
        int page = Integer.parseInt(
                        Objects.requireNonNull(headers.get(PagingHeaders.PAGE_NUMBER.getName())).get(0));
        int size = Integer.parseInt(Objects.requireNonNull(headers.get(PagingHeaders.PAGE_SIZE.getName())).get(0));
        return PageRequest.of(page, size, sort);
    }

    private boolean isRequestPaged(HttpHeaders headers) {
        return headers.containsKey(PagingHeaders.PAGE_NUMBER.getName()) && headers
                        .containsKey(PagingHeaders.PAGE_SIZE.getName());
    }

}
