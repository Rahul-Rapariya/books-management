package com.assignment.booksmanagement.service;

import org.springframework.web.multipart.MultipartFile;

public interface UploadBooksService {
    int uploadBooks(MultipartFile books);
}
