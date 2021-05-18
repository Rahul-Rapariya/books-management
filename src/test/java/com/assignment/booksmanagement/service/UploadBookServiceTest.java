package com.assignment.booksmanagement.service;

import com.assignment.booksmanagement.Utils.TestData;
import com.assignment.booksmanagement.model.Book;
import com.assignment.booksmanagement.repository.BookRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UploadBookServiceTest {
    @InjectMocks
    UploadBooksServiceImpl uploadBooksService;

    @Mock BookRepository bookRepository;

    @Test
    public void testUploadBooks() throws IOException {
        List<Book> bookList = TestData.listOfValidBooks();
        when(bookRepository.saveAll(anyList())).thenReturn(bookList);
        ClassPathResource classPathResource = new org.springframework.core.io.ClassPathResource("/static/Book.csv");
        MockMultipartFile file = new MockMultipartFile("Books", "Books", "multipart/form-data",
                        classPathResource.getInputStream());

        int numberOfRecordsUpdated = uploadBooksService.uploadBooks(file);
        assertEquals(4, numberOfRecordsUpdated);
        verify(bookRepository, times(1)).saveAll(anyList());
    }

}
