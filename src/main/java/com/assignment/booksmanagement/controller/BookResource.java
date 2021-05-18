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
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
    @ApiOperation(value = "Return book for given ISBN",
                    notes = "Book with all the attributes will be return for give ISBN",
                    response = Book.class)
    public ResponseEntity getBook(
                    @ApiParam(value = "Unique ISBN value of the book", required = true)
                    @PathVariable long isbn) {
        logger.info("Request received to get book having ISBN : {}", isbn);
        return ResponseEntity.ok().body(bookService.getBook(isbn));
    }

    @PostMapping("/book")
    @ApiOperation(value = "Add new given book to the database",
                    notes = "Please only provide ISBN, Author, Title and List of tag of book in the request body. All 4 fields are mandatory",
                    response = Book.class)
    public ResponseEntity addBook(@RequestBody Book book) {
        validateBook(book);
        logger.info("Request received to add book with ISBN : {}", book.getIsbn());
        logger.info("Request received to add book with ISBN : {}", book.getTags().toArray());
        return ResponseEntity.ok().body(bookService.addBook(book));
    }

    @PutMapping("/book/{isbn}")
    @ApiOperation(value = "Update the book in database with new given book attributes for given ISBN",
                    notes = "Please only provide ISBN, Author, Title and List of tag of book in the request body. All 4 fields are mandatory",
                    response = Book.class)
    public ResponseEntity updateBook(
                    @ApiParam(value = "Unique ISBN value of the book", required = true)
                    @PathVariable long isbn, @RequestBody Book book) {
        validateBook(book);
        logger.info("Request received to update book with ISBN : {}", isbn);
        Book updatedBook = bookService.updateBook(isbn, book);
        return ResponseEntity.ok().body(updatedBook);
    }

    @DeleteMapping("/book/{isbn}")
    @ApiOperation(value = "Delete the book in database for given ISBN")
    public ResponseEntity deleteBook(
                    @ApiParam(value = "Unique ISBN value of the book", required = true)
                    @PathVariable long isbn) {
        logger.info("Request received to delete book with ISBN : {} ", isbn);
        bookService.deleteBook(isbn);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("deleted successfully"));

    }

    @PostMapping("/upload-books")
    @ApiOperation(value = "Add the books of given CSV file",
                    notes = "Please upload the comma separated CSV file. Use '|' for adding multiple tags. "
                                    + "Sample records with Header:\n "
                                    + "ISBN,TITLE,AUTHOR,TAGS\n"
                                    + "121,Book1,Author1,inspire1|inspire11\n"
                                    + "122,Book2,Author2,inspire2")
    public ResponseEntity uploadBooks(@RequestParam("file") MultipartFile file) {
        logger.info("Request received to upload books of file {}", file.getName());
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
    @ApiOperation(value = "Search the book with one of given attributes among ISBN, tag, author and title",
                    notes = "You can search the books using one of above mentioned parameter. Sample request: "
                                    + "http://localhost:8080/books-management/v1/search-books?tag={tag_value}")
    public ResponseEntity<List<Book>> searchBooks(
                    @ApiParam(value = "Unique search value of the book", required = false)
                                    BookSpec bookSpec, Sort sort, @RequestHeader HttpHeaders headers) {
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



