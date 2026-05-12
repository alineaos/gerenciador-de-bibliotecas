package io.github.alineaos.librarymanager.repository.specification;

import io.github.alineaos.librarymanager.domain.entity.Book;
import org.springframework.data.jpa.domain.Specification;

import java.time.Year;

public class BookSpecification {

    public static Specification<Book> hasTitle(String title){
        return (root, query, cb) ->
                title == null ? null : cb.like(root.get("title"), "%" + title.toLowerCase() + "%");
    }

    public static Specification<Book> hasAuthor(String author){
        return (root, query, cb) ->
                author == null ? null : cb.like(root.get("author"), "%" + author.toLowerCase() + "%");
    }

    public static Specification<Book> hasPublisher(String publisher){
        return (root, query, cb) ->
                publisher == null ? null : cb.like(root.get("publisher"), "%" + publisher.toLowerCase() + "%");
    }

    public static Specification<Book> hasYear(Year year){
        return (root, query, cb) ->
                year == null ? null : cb.equal(root.get("year"), year);
    }

    public static Specification<Book> hasEdition(Integer edition){
        return (root, query, cb) ->
                edition == null ? null : cb.equal(root.get("edition"), edition);
    }

    public static Specification<Book> hasIsbn(String isbn){
        return (root, query, cb) ->
                isbn == null ? null : cb.like(root.get("isbn"), "%" + isbn + "%");
    }
}
