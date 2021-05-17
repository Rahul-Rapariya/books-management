package com.assignment.booksmanagement.controller;

import com.assignment.booksmanagement.exception.DefaultRuntimeException;
import com.assignment.booksmanagement.exception.RequestNotValidException;
import com.assignment.booksmanagement.controller.helper.ResponseMessage;
import com.assignment.booksmanagement.model.Book;
import com.assignment.booksmanagement.controller.helper.PagingHeaders;
import com.assignment.booksmanagement.controller.helper.PagingResponse;
import com.assignment.booksmanagement.service.BookService;
import com.assignment.booksmanagement.utility.CsvParserUtility;

import com.assignment.booksmanagement.service.SearchBooksService;
import com.assignment.booksmanagement.service.UploadBooksService;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Join;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.assignment.booksmanagement.controller.helper.ValidateRequest.validateBook;

@RestController
@RequestMapping("/books-management/v1")
public class BookResource {
    Logger logger = LoggerFactory.getLogger(BookResource.class);

    @Autowired
    BookService bookService;
    @Autowired
    UploadBooksService uploadBookService;
    @Autowired
    SearchBooksService searchBooksService;

    @GetMapping("/book/{isbn}")
    public ResponseEntity getBook(@PathVariable long isbn) {
        logger.info("Request received to get book having ISBN : {}", isbn);
        return ResponseEntity.ok().body(bookService.getBook(isbn));
    }

    @PostMapping("/book")
    public ResponseEntity addBook(@RequestBody Book book) {
        validateBook(book);
        logger.info("Request received to add book with ISBN : {}", book.getIsbn());
        return ResponseEntity.ok().body(bookService.addBook(book));
    }

    @PutMapping("/book/{isbn}")
    public ResponseEntity updateBook(@PathVariable long isbn, @RequestBody Book book) {
        validateBook(book);
        logger.info("Request received to update book with ISBN : {}", isbn);
        Book updatedBook = bookService.updateBook(isbn, book);
        return ResponseEntity.ok().body(updatedBook);
    }

    @DeleteMapping("/book/{isbn}")
    public ResponseEntity deleteBook(@PathVariable long isbn) {
        logger.info("Request received to delete book with ISBN : {} ", isbn);
        bookService.deleteBook(isbn);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("deleted successfully"));

    }

    @PostMapping("/upload-books")
    public ResponseEntity uploadBooks(@RequestParam("file") MultipartFile file) {
        String message = "";
        if (CsvParserUtility.hasCSVFormat(file)) {
            try {
                int countBooks = uploadBookService.uploadBooks(file);
                message = countBooks + " records uploaded successfully: " + file.getOriginalFilename();
                return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseMessage(message));
            } catch (Exception e) {
                throw new DefaultRuntimeException("Something went wrong!");
            }
        } else {
            throw new RequestNotValidException("Please upload a CSV file");
        }
    }

    @Join(path = "tags", alias = "b")
    @And({
                    @Spec(path = "b.tag", params = "tag", spec = Like.class),
                    @Spec(path = "author", params = "author", spec = Like.class),
                    @Spec(path = "title", params = "title", spec = Like.class),
                    @Spec(path = "isbn", params = "isbn", spec = Equal.class)
    })
    interface BookSpec extends Specification<Book> {

    }

    @GetMapping("/search-books")
    public ResponseEntity<List<Book>> searchBooks(BookSpec bookSpec, Sort sort, @RequestHeader HttpHeaders headers) {
        final PagingResponse response = searchBooksService.searchBook(bookSpec, headers, sort);
        return new ResponseEntity<>(response.getElements(), returnHttpHeaders(response), HttpStatus.OK);
    }

    public HttpHeaders returnHttpHeaders(PagingResponse response) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(PagingHeaders.COUNT.getName(), String.valueOf(response.getCount()));
        headers.set(PagingHeaders.PAGE_SIZE.getName(), String.valueOf(response.getPageSize()));
        headers.set(PagingHeaders.PAGE_OFFSET.getName(), String.valueOf(response.getPageOffset()));
        headers.set(PagingHeaders.PAGE_NUMBER.getName(), String.valueOf(response.getPageNumber()));
        headers.set(PagingHeaders.PAGE_TOTAL.getName(), String.valueOf(response.getPageTotal()));
        return headers;
    }

}



