package com.assignment.booksmanagement.service;

import com.assignment.booksmanagement.Utils.TestData;
import com.assignment.booksmanagement.exception.ResourceAlreadyExistException;
import com.assignment.booksmanagement.exception.ResourceNotFoundException;
import com.assignment.booksmanagement.model.Book;
import com.assignment.booksmanagement.repository.BookRepository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;

import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import java.util.Optional;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BookServiceTest {
    @InjectMocks
    BookServiceImpl bookService;

    @Mock BookRepository bookRepository;

    @Test
    public void testAddBookForNewBook() {
        Book book = TestData.validBook();
        when(bookRepository.save(book)).thenReturn
                        (book);
        Book addedBook = bookService.addBook(book);
        assertEquals(book.getIsbn(), addedBook.getIsbn());
        assertEquals(book.getAuthor(), addedBook.getAuthor());
        assertEquals(book.getTitle(), addedBook.getTitle());
        assertEquals(book.getTags(), addedBook.getTags());
        verify(bookRepository, times(1)).findByisbn(anyLong());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test(expected = ResourceAlreadyExistException.class)
    public void testAddBookForAlreadyExistingBook() {
        when(bookRepository.findByisbn(9999L)).thenReturn
                        (Optional.of(TestData.validBook()));

        bookService.addBook(TestData.validBook());
        verify(bookRepository, times(1)).findByisbn(anyLong());
        verifyNoInteractions(bookRepository);
    }

    @Test
    public void testGetBookForExistingBook() {
        Book book = TestData.validBook();
        when(bookRepository.findByisbn(9999L)).thenReturn
                        (Optional.of(book));

        Book result = bookService.getBook(9999l);
        assertEquals(book.getIsbn(), result.getIsbn());
        assertEquals(book.getAuthor(), result.getAuthor());
        assertEquals(book.getTitle(), result.getTitle());
        assertEquals(book.getTags(), result.getTags());
        verify(bookRepository, times(1)).findByisbn(anyLong());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testGetBookForNonExistingBook() {
        when(bookRepository.findByisbn(9999L)).thenReturn
                        (Optional.empty());

        bookService.getBook(9999L);
        verify(bookRepository, times(1)).findByisbn(anyLong());
        verifyNoInteractions(bookRepository);
    }

    @Test
    public void testUpdateBookForExistingBook() {
        Book book = TestData.bookToUpdate();
        when(bookRepository.findByisbn(999l)).thenReturn
                        (Optional.of(book));
        when(bookRepository.save(book)).thenReturn
                        (book);
        Book updatedBook = bookService.updateBook(999l, book);
        assertEquals(book.getIsbn(), updatedBook.getIsbn());
        assertEquals(book.getAuthor(), updatedBook.getAuthor());
        assertEquals(book.getTitle(), updatedBook.getTitle());
        assertEquals(book.getTags(), updatedBook.getTags());

        verify(bookRepository, times(1)).findByisbn(anyLong());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testUpdateBookForNonExistingBook() {
        Book book = TestData.bookToUpdate();
        when(bookRepository.findByisbn(999L)).thenReturn
                        (Optional.empty());
        bookService.updateBook(999l, book);

        verify(bookRepository, times(1)).findByisbn(anyLong());
        verifyNoInteractions(bookRepository);
    }

    @Test
    public void testDeleteBookWithExistingBook() {
        Book book = TestData.validBook();
        when(bookRepository.findByisbn(9999)).thenReturn
                        (Optional.of(book));

        bookService.deleteBook(9999l);
        verify(bookRepository, times(1)).findByisbn(anyLong());
        verify(bookRepository, times(1)).delete(any(Book.class));
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testDeleteBookWithNotExistingBook() {
        Book book = TestData.validBook();
        when(bookRepository.findByisbn(9999)).thenReturn
                        (Optional.empty());

        bookService.deleteBook(9999l);
        verify(bookRepository, times(1)).findByisbn(anyLong());
        verifyNoMoreInteractions(bookRepository);
    }
}
