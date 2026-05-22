package io.github.alineaos.librarymanager.repository.specification;

import io.github.alineaos.librarymanager.domain.entity.Book;
import io.github.alineaos.librarymanager.domain.entity.BookGenre;
import io.github.alineaos.librarymanager.domain.entity.Genre;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.time.Year;

public class BookSpecification {

    public static Specification<Book> hasTitle(String title) {
        return (root, query, cb) ->
                title == null ? null : cb.like(root.get("title"), "%" + title.toLowerCase() + "%");
    }

    public static Specification<Book> hasAuthor(String author) {
        return (root, query, cb) ->
                author == null ? null : cb.like(root.get("author"), "%" + author.toLowerCase() + "%");
    }

    public static Specification<Book> hasPublisher(String publisher) {
        return (root, query, cb) ->
                publisher == null ? null : cb.like(root.get("publisher"), "%" + publisher.toLowerCase() + "%");
    }

    public static Specification<Book> hasYear(Year year) {
        return (root, query, cb) ->
                year == null ? null : cb.equal(root.get("year"), year);
    }

    public static Specification<Book> hasEdition(Integer edition) {
        return (root, query, cb) ->
                edition == null ? null : cb.equal(root.get("edition"), edition);
    }

    public static Specification<Book> hasIsbn(String isbn) {
        return (root, query, cb) ->
                isbn == null ? null : cb.like(root.get("isbn"), "%" + isbn + "%");
    }

    public static Specification<Book> hasGenreWithName(String genreName) {
        return (root, query, cb) -> {
            if (genreName == null || query == null) return null;

            Subquery<Long> bookIdSubQuery = query.subquery(Long.class);
            Root<BookGenre> bookGenreRoot = bookIdSubQuery.from(BookGenre.class);
            Join<BookGenre, Genre> genreJoin = bookGenreRoot.join("genre");

            bookIdSubQuery.select(bookGenreRoot.get("book").get("id"));

            bookIdSubQuery.where(cb.like(genreJoin.get("name"), "%" + genreName + "%"));

            return cb.in(root.get("id")).value(bookIdSubQuery);
        };
    }
}
