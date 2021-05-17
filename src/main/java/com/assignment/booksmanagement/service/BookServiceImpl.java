package com.assignment.booksmanagement.service;

import com.assignment.booksmanagement.exception.ResourceAlreadyExistException;
import com.assignment.booksmanagement.exception.ResourceNotFoundException;
import com.assignment.booksmanagement.model.Book;
import com.assignment.booksmanagement.model.BookTag;
import com.assignment.booksmanagement.repository.BookRepository;
import com.assignment.booksmanagement.repository.BookTagRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class BookServiceImpl implements BookService {
    private static final Logger logger = LoggerFactory.getLogger(BookServiceImpl.class);

    @Autowired BookRepository bookRepository;
    @Autowired BookTagRepository bookTagRepository;

    @Override
    public Book addBook(Book book) {
        Optional<Book> bookDb = bookRepository.findByisbn(book.getIsbn());
        if (bookDb.isPresent()) {
            logger.error("Book already exist with ISBN {}", book.getIsbn());
            throw new ResourceAlreadyExistException("Book already exist with ISBN : " + book.getIsbn());
        } else {
            Book bookToAdd = Book.builder().isbn(book.getIsbn())
                            .author(book.getAuthor())
                            .title(book.getTitle())
                            .build();
            bookToAdd.addBookTags(book.getTags());
            Book savedBook = bookRepository.save(bookToAdd);
            logger.info("Book with ISBN {} added successfully", +book.getIsbn());
            return savedBook;
        }
    }

    @Override public Book getBook(long isbn) {
        Optional<Book> bookDb = bookRepository.findByisbn(isbn);
        if (bookDb.isPresent()) {
            logger.info("Book with ISBN {} returned successfully", +isbn);
            return bookDb.get();
        } else {
            logger.error("Book not found with ISBN {}", isbn);
            throw new ResourceNotFoundException("Book not found with ISBN : " + isbn);
        }
    }

    @Override public Book updateBook(long isbn, Book book) {
        Optional<Book> bookDb = bookRepository.findByisbn(isbn);
        if (bookDb.isPresent()) {
            logger.info("Book found with ISBN {}", book.getIsbn());
            Book bookUpdate = bookDb.get();
            bookUpdate.setAuthor(book.getAuthor());
            bookUpdate.setTitle(book.getTitle());
            bookUpdate.setIsbn(book.getIsbn());
            bookUpdate.updateBookTags(book.getTags());
            bookUpdate.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            Book savedBook = bookRepository.save(bookUpdate);
            logger.info("Book with ISBN {} updated successfully", isbn);
            return savedBook;
        } else {
            logger.error("Book not found with ISBN {}", isbn);
            throw new ResourceNotFoundException("Book not found with ISBN : " + isbn);
        }
    }

    @Override public void deleteBook(long isbn) {
        Optional<Book> bookDb = this.bookRepository.findByisbn(isbn);
        if (bookDb.isPresent()) {
            logger.info("Book with ISBN {} found to delete", isbn);
            this.bookRepository.delete(bookDb.get());
        } else {
            throw new ResourceNotFoundException("book not found with Id : " + isbn);
        }
    }
}
