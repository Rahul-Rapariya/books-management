package com.assignment.booksmanagement.Utils;

import com.assignment.booksmanagement.model.Book;
import com.assignment.booksmanagement.model.BookTag;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TestData {

    public static Book validBook() {
        Set<BookTag> bookTags = new HashSet<>();
        for (int i = 0; i < 3; i++) {
            BookTag bookTag = new BookTag();
            bookTag.setTag("tag" + i);
            bookTags.add(bookTag);
        }
        Book book = Book.builder().isbn(9999L).author("RAHUL").title("Blunders").tags(bookTags).build();

        return book;
    }

    public static List<Book> listOfValidBooks() {
        List<Book> bookList = new ArrayList<>();
        Set<BookTag> bookTags = new HashSet<>();
        for (int i = 0; i < 3; i++) {
            BookTag bookTag = new BookTag();
            bookTag.setTag("tag" + i);
            bookTags.add(bookTag);
        }
        Book book = Book.builder().isbn(9984L).author("RAHUL").title("Blunders").tags(bookTags).build();
        Book book1 = Book.builder().isbn(9980L).author("RAHUL1").title("Blunders1").tags(bookTags).build();
        Book book2 = Book.builder().isbn(9981L).author("RAHUL2").title("Blunders2").tags(bookTags).build();
        Book book3 = Book.builder().isbn(9982L).author("RAHUL3").title("Blunders3").tags(bookTags).build();

        bookList.add(book);
        bookList.add(book1);
        bookList.add(book2);
        bookList.add(book3);
        return bookList;
    }

    public static Book alreadyExistingBook() {
        Set<BookTag> bookTags = new HashSet<>();
        for (int i = 0; i < 3; i++) {
            BookTag bookTag = new BookTag();
            bookTag.setTag("tag" + i);
            bookTags.add(bookTag);
        }
        Book book = Book.builder().isbn(777L).author("RAHUL777").title("Blunders777").tags(bookTags).build();

        return book;
    }

    public static Book bookWithMissingParam() {
        Set<BookTag> bookTags = new HashSet<>();
        for (int i = 0; i < 3; i++) {
            BookTag bookTag = new BookTag();
            bookTag.setTag("tag" + i);
            bookTags.add(bookTag);
        }
        Book book = Book.builder().isbn(9999L).title("EK THA RAJA").tags(bookTags).build();

        return book;
    }

    public static Book bookToUpdate() {
        Set<BookTag> bookTags = new HashSet<>();
        for (int i = 0; i < 2; i++) {
            BookTag bookTag = new BookTag();
            bookTag.setTag("tagUpdated" + i);
            bookTags.add(bookTag);
        }
        Book book = Book.builder().isbn(999L).author("RahulUpdated").title("BlundersUpdated").tags(bookTags).build();

        return book;
    }

}
