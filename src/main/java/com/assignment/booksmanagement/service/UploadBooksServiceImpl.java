package com.assignment.booksmanagement.service;

import com.assignment.booksmanagement.exception.DefaultRuntimeException;
import com.assignment.booksmanagement.utility.CsvParserUtility;
import com.assignment.booksmanagement.model.Book;
import com.assignment.booksmanagement.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class UploadBooksServiceImpl implements UploadBooksService {
    private static final Logger logger = LoggerFactory.getLogger(UploadBooksServiceImpl.class);
    @Autowired BookRepository bookRepository;

    @Override public int uploadBooks(MultipartFile file) {
        try {
            List<Book> books = CsvParserUtility.readFile(file.getInputStream());
            bookRepository.saveAll(books);
            logger.debug("{} books uploaded successfully", books.size());
            return books.size();
        } catch (IOException ioException) {
            logger.error("IOException:Exception while uploading the file ", ioException);
            throw new DefaultRuntimeException("Something went wrong!");
        }
    }

}


