package com.assignment.booksmanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "Books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
//@EqualsAndHashCode(of = "isbn")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    @Column(name = "ID")
    private Long id;
    @Column(name = "ISBN", unique = true, nullable = false)
    private Long isbn;
    @Column(name = "TITLE",nullable = false)
    private String title;
    @Column(name = "AUTHOR", nullable = false)
    private String author;
    @OneToMany(mappedBy = "book",
                    cascade = CascadeType.ALL,
                    orphanRemoval = true
    )
    private Set<BookTag> tags;
    @CreationTimestamp
    @Column(name = "CreatedAt", nullable = false)
    private Date createdAt;
    @Column(name = "LastUpdatedAt")
    private Date updatedAt;

    public void addBookTag(BookTag bookTag) {
        if (tags == null) {
            tags = new HashSet<>();
        }
        tags.add(bookTag);
        bookTag.setBook(this);
    }

    public void addBookTags(Set<BookTag> bookTags) {
        if (!(null == bookTags) && (!bookTags.isEmpty())) {
            if (tags == null) {
                tags = new HashSet<>();
            }
            for (BookTag bookTag : bookTags) {
                tags.add(bookTag);
                bookTag.setBook(this);
            }
        }
    }

    public void removeBookTag(BookTag bookTag) {
        tags.remove(bookTag);
        bookTag.setBook(null);
    }

    public void removeBooksTags(Set<BookTag> bookTags) {
        for (BookTag bookTag : bookTags) {
            tags.remove(bookTag);
            bookTag.setBook(null);
        }
    }

    public Book(long isbn, String title, String author) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
    }

    public Book(Map<String, String> row) {
        this(Long.parseLong(row.get("ISBN")), row.get("TITLE"), row.get("AUTHOR"));
        String[] rowTags = row.get("TAGS").split("\\|", -1);
        Set<BookTag> bookTags = new HashSet<>();
        for (String tag : rowTags) {
            BookTag bookTag = new BookTag();
            bookTag.setTag(tag.trim());
            bookTags.add(bookTag);
        }

        this.tags = bookTags;
        this.addBookTags(bookTags);
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Book book1 = (Book) o;
        return isbn == book1.isbn;
    }

    @Override public int hashCode() {
        return Objects.hash(isbn);
    }
}
