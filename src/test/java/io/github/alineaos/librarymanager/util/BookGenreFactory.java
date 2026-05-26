package io.github.alineaos.librarymanager.util;

import io.github.alineaos.librarymanager.domain.entity.Book;
import io.github.alineaos.librarymanager.domain.entity.BookGenre;
import io.github.alineaos.librarymanager.domain.entity.Genre;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BookGenreFactory {
    private final BookFactory bookFactory;
    private final GenreFactory genreFactory;

    public BookGenreFactory(BookFactory bookFactory, GenreFactory genreFactory) {
        this.bookFactory = bookFactory;
        this.genreFactory = genreFactory;
    }


    public List<BookGenre> newBookGenreList() {
        List<Book> bookList = bookFactory.newBookList();
        Book capitaesDaAreia = bookList.get(0);
        Book jogosVorazes = bookList.get(1);
        Book horaDaEstrela = bookList.get(2);

        Genre nacional = genreFactory.newGenreNacional();
        Genre ficcao = genreFactory.newGenreFiccao();

        BookGenre capitaesDaAreiaNacional = BookGenre.builder()
                .id(1L)
                .book(capitaesDaAreia)
                .genre(nacional)
                .createdAt(LocalDateTime.parse("2026-04-24T18:00:33"))
                .updatedAt(LocalDateTime.parse("2026-04-24T18:00:33"))
                .build();

        BookGenre jogosVorazesFiccao = BookGenre.builder()
                .id(2L)
                .book(jogosVorazes)
                .genre(ficcao)
                .createdAt(LocalDateTime.parse("2026-04-24T18:01:33"))
                .updatedAt(LocalDateTime.parse("2026-04-24T18:01:33"))
                .build();

        BookGenre horaDaEstrelaNacional = BookGenre.builder()
                .id(3L)
                .book(horaDaEstrela)
                .genre(nacional)
                .createdAt(LocalDateTime.parse("2026-04-24T18:02:33"))
                .updatedAt(LocalDateTime.parse("2026-04-24T18:02:33"))
                .build();

        return new ArrayList<>(List.of(capitaesDaAreiaNacional, jogosVorazesFiccao, horaDaEstrelaNacional));
    }

    public BookGenre newBookGenre(Book book, Genre genre) {

        return BookGenre.builder()
                .book(book)
                .genre(genre)
                .createdAt(LocalDateTime.parse("2026-04-24T18:00:33"))
                .updatedAt(LocalDateTime.parse("2026-04-24T18:00:33"))
                .build();
    }

    public BookGenre newBookGenreSaved(Book book, Genre genre) {

        return BookGenre.builder()
                .id(99L)
                .book(book)
                .genre(genre)
                .createdAt(LocalDateTime.parse("2026-04-24T18:00:33"))
                .updatedAt(LocalDateTime.parse("2026-04-24T18:00:33"))
                .build();
    }
}