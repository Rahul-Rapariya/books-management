package com.assignment.booksmanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "BooksTags")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BookTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    @JsonIgnore
    private Long id;
    @Column(name = "TAG", nullable = false)
    private String tag;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "BookTagID")
    Book book;

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        BookTag bookTag = (BookTag) o;
        return Objects.equals(tag, bookTag.tag) && Objects.equals(book, bookTag.book);
    }

    @Override public int hashCode() {
        return Objects.hash(tag, book);
    }
}
