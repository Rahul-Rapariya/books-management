package com.assignment.booksmanagement;

import com.assignment.booksmanagement.Utils.TestData;
import com.assignment.booksmanagement.model.Book;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import static org.junit.Assert.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class BooksManagementApplicationTests {


    @LocalServerPort int port;
    @Autowired TestRestTemplate restTemplate;

    @Test
    public void testAddBook() throws JSONException {
        String url = "http://localhost:" + port + "/books-management/v1/book";
        HttpHeaders headers = new HttpHeaders();
        Book book = TestData.validBook();
        HttpEntity<Book> requestEntity = new HttpEntity<>(book, headers);
        ResponseEntity<String> responseEntity =
                        restTemplate.postForEntity(url, requestEntity, String.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        String expectedResult =
                        "{\"isbn\":9999,\"title\":\"Blunders\",\"author\":\"RAHUL\",\"tags\":[{\"tag\":\"tag2\"},{\"tag\":\"tag1\"},{\"tag\":\"tag0\"}]}";
        JSONAssert.assertEquals(expectedResult, (responseEntity.getBody()), false);
    }

    @Test
    @Sql(statements = ("INSERT INTO BOOKS (ISBN, Author, Title) VALUES (777, 'Rahul777', 'Blunder777');"),
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = ("INSERT INTO BOOKS_TAGS (ISBN, TAG) VALUES (777, 'TagBlunder777')"),
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void testAddAlreadyExistingBook() {
        String url = "http://localhost:" + port + "/books-management/v1/book";
        HttpHeaders headers = new HttpHeaders();
        Book book = TestData.alreadyExistingBook();
        HttpEntity<Book> requestEntity = new HttpEntity<>(book, headers);
        ResponseEntity<String> responseEntity =
                        restTemplate.postForEntity(url, requestEntity, String.class);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals(
                        "{\"message\":\"Book already exist with ISBN : 777\"}",
                        responseEntity.getBody());
    }

    @Test
    public void testBookWithMissingParam() {
        String url = "http://localhost:" + port + "/books-management/v1/book";
        HttpHeaders headers = new HttpHeaders();
        Book book = TestData.bookWithMissingParam();
        HttpEntity<Book> requestEntity = new HttpEntity<>(book, headers);
        ResponseEntity<String> responseEntity =
                        restTemplate.postForEntity(url, requestEntity, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(
                        "{\"message\":\"One of the attributes is missing! Please check the request and submit again\"}",
                        responseEntity.getBody());
    }

    @Test
    @Sql(statements = ("INSERT INTO BOOKS (ISBN, Author, Title) VALUES (888, 'Rahul888', 'Blunder888');"),
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = ("INSERT INTO BOOKS_TAGS (ISBN, TAG) VALUES (888, 'TagBlunder888')"),
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void testGetBookWithISBN() throws JSONException {
        String url = "http://localhost:" + port + "/books-management/v1/book/888";
        ResponseEntity<String> responseEntity =
                        restTemplate.getForEntity(url, String.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        String expectedResult =
                        "{\"isbn\":888,\"title\":\"Blunder888\",\"author\":\"Rahul888\",\"tags\":[{\"tag\":\"TagBlunder888\"}],\"createdAt\":null,\"updatedAt\":null}";
        JSONAssert.assertEquals(expectedResult, responseEntity.getBody(), true);
    }

    @Test
    public void testGetBookNotFoundWithISBN() {
        String url = "http://localhost:" + port + "/books-management/v1/book/111";
        ResponseEntity<String> responseEntity =
                        restTemplate.getForEntity(url, String.class);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("{\"message\":\"Book not found with ISBN : 111\"}", responseEntity.getBody());
    }

    @Test
    @Sql(statements = ("INSERT INTO BOOKS (ISBN, Author, Title) VALUES (999, 'Rahul999', 'Blunder999');"),
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = ("INSERT INTO BOOKS_TAGS (ISBN, TAG) VALUES (999, 'TagBlunder999')"),
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void testUpdateBookWithISBN() throws JSONException {

        String url = "http://localhost:" + port + "/books-management/v1/book/999";
        HttpHeaders headers = new HttpHeaders();
        Book book = TestData.bookToUpdate();
        HttpEntity<Book> requestEntity = new HttpEntity<>(book, headers);
        ResponseEntity<String> responseEntity =
                        restTemplate.exchange(url, HttpMethod.PUT, requestEntity, String.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        String expectedResult =
                        "{\"isbn\":999,\"title\":\"BlundersUpdated\",\"author\":\"RahulUpdated\",\"tags\":[{\"tag\":\"tagUpdated1\"},{\"tag\":\"tagUpdated0\"}],\"createdAt\":null}";
        JSONAssert.assertEquals(expectedResult, responseEntity.getBody(), false);
    }

    @Test
    public void testUpdateBookNotFoundWithISBN() {
        String url = "http://localhost:" + port + "/books-management/v1/book/111";
        Book book = TestData.bookToUpdate();
        HttpEntity<Book> requestEntity = new HttpEntity<>(book, null);
        ResponseEntity<String> responseEntity =
                        restTemplate.exchange(url, HttpMethod.PUT, requestEntity, String.class);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("{\"message\":\"Book not found with ISBN : 111\"}", responseEntity.getBody());
    }

    @Test
    @Sql(statements = ("INSERT INTO BOOKS (ISBN, Author, Title) VALUES (555, 'Rahul555', 'Blunder555');"),
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = ("INSERT INTO BOOKS_TAGS (ISBN, TAG) VALUES (555, 'TagBlunder555')"),
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void testDeleteBookWithISBN() throws JSONException {
        String url = "http://localhost:" + port + "/books-management/v1/book/555";
        HttpEntity<Book> requestEntity = new HttpEntity<>(null, null);
        ResponseEntity responseEntity = restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, String.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("{\"message\":\"deleted successfully\"}", responseEntity.getBody());
    }

    @Test
    public void testUploadBook() {
        String url = "http://localhost:" + port + "/books-management/v1/upload-books";
        LinkedMultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();
        parameters.add("file", new org.springframework.core.io.ClassPathResource("/static/Book.csv"));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<LinkedMultiValueMap<String, Object>> entity = new HttpEntity<>(parameters, headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals("{\"message\":\"4 records uploaded successfully: Book.csv\"}", responseEntity.getBody());
    }

    @Test
    @Sql(statements = ("INSERT INTO BOOKS (ISBN, Author, Title) VALUES (1111, 'Rahul1111', 'Blunder1111');"),
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = ("INSERT INTO BOOKS_TAGS (ISBN, TAG) VALUES (1111, 'TagBlunder1111')"),
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = ("INSERT INTO BOOKS (ISBN, Author, Title) VALUES (2222, 'Rahul2222', 'Blunder2222');"),
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = ("INSERT INTO BOOKS_TAGS (ISBN, TAG) VALUES (2222, 'TagBlunder2222')"),
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void testSearchBookWithAuthor() throws JSONException {
     testSearchBooks("author", "Rahul1111");
    }

    @Test
    @Sql(statements = ("INSERT INTO BOOKS (ISBN, Author, Title) VALUES (1111, 'Rahul1111', 'Blunder1111');"),
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = ("INSERT INTO BOOKS_TAGS (ISBN, TAG) VALUES (1111, 'TagBlunder1111')"),
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = ("INSERT INTO BOOKS (ISBN, Author, Title) VALUES (2222, 'Rahul2222', 'Blunder2222');"),
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = ("INSERT INTO BOOKS_TAGS (ISBN, TAG) VALUES (2222, 'TagBlunder2222')"),
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void testSearchBookWithTitle() throws JSONException {
        testSearchBooks("title", "Blunder1111");

    }

    @Test
    @Sql(statements = ("INSERT INTO BOOKS (ISBN, Author, Title) VALUES (1111, 'Rahul1111', 'Blunder1111');"),
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = ("INSERT INTO BOOKS_TAGS (ISBN, TAG) VALUES (1111, 'TagBlunder1111')"),
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = ("INSERT INTO BOOKS (ISBN, Author, Title) VALUES (2222, 'Rahul2222', 'Blunder2222');"),
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = ("INSERT INTO BOOKS_TAGS (ISBN, TAG) VALUES (2222, 'TagBlunder2222')"),
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void testSearchBookWithISBN() throws JSONException {
        testSearchBooks("isbn","1111");
    }

    @Test
    @Sql(statements = ("INSERT INTO BOOKS (ISBN, Author, Title) VALUES (1111, 'Rahul1111', 'Blunder1111');"),
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = ("INSERT INTO BOOKS_TAGS (ISBN, TAG) VALUES (1111, 'TagBlunder1111')"),
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = ("INSERT INTO BOOKS (ISBN, Author, Title) VALUES (2222, 'Rahul2222', 'Blunder2222');"),
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = ("INSERT INTO BOOKS_TAGS (ISBN, TAG) VALUES (2222, 'TagBlunder2222')"),
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void testSearchBookWithTags() throws JSONException {
        testSearchBooks("tag","TagBlunder1111");
    }

    public void testSearchBooks(String param, String value) throws JSONException {
        String url = "http://localhost:" + port + "/books-management/v1/search-books";
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
       /* to do test with pagination
       headers.add(PagingHeaders.PAGE_NUMBER.getName(), "0");
        headers.add(PagingHeaders.PAGE_SIZE.getName(), "1");*/
        HttpEntity<Book> requestEntity = new HttpEntity<>(null, headers);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                        .queryParam(param, value);
        ResponseEntity<String> responseEntity =
                        restTemplate.exchange(builder.toUriString(), HttpMethod.GET, requestEntity, String.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        String expectedResult ="[{\"isbn\":1111,\"title\":\"Blunder1111\",\"author\":\"Rahul1111\",\"tags\":[{\"tag\":\"TagBlunder1111\"}],\"createdAt\":null,\"updatedAt\":null}]";
        JSONAssert.assertEquals(expectedResult,responseEntity.getBody(), true);
    }
}

