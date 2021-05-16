package com.assignment.booksmanagement.controller;

import com.assignment.booksmanagement.controller.BookResource;
import com.assignment.booksmanagement.model.Book;
import com.assignment.booksmanagement.repository.BookRepository;
import com.assignment.booksmanagement.service.BookService;
import com.assignment.booksmanagement.service.UploadBooksService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(value = BookResource.class)
public class BookResourceTest {

    @MockBean
    BookService bookService;
    @Mock
    BookRepository bookRepository;

    @Autowired MockMvc mockMvc;


    @Test
    public void testAddBooks() throws Exception {

        when(bookRepository.save(new Book()))
                        .thenReturn(Book.builder().isbn(9999L).author("rahul").title("Who will cry").build());
        String json = "{\n"
                        + "    \"isbn\" : 9999,\n"
                        + "    \"author\" : \"rahul\",\n"
                        + "    \"title\" : \"Who will cry\",\n"
                        + "    \"tags\" : [{\"tag\":\"inspiration\"}, {\"tag\":\"motivation\"}] \n"
                        + "}";
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("books-management/v1/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json);

        MvcResult mockMvc = this.mockMvc.perform(requestBuilder).andReturn();
        verify(bookService).addBook(any());
        verify(bookRepository).save(any());
        assertEquals(HttpStatus.OK.value(), mockMvc.getResponse().getStatus());
        System.out.println(mockMvc.getResponse().getErrorMessage());
    }

    //Book without Tag

    //Book with author

}