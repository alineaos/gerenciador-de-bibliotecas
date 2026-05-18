package io.github.alineaos.librarymanager.util;

import io.github.alineaos.librarymanager.domain.entity.Book;
import io.github.alineaos.librarymanager.dto.request.BookPatchRequest;
import io.github.alineaos.librarymanager.dto.request.BookPostRequest;
import io.github.alineaos.librarymanager.dto.response.BookGetResponse;
import io.github.alineaos.librarymanager.dto.response.BookPostResponse;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

public class BookFactory {
    public List<Book> newBookList() {
        Book capitaesDaAreia = Book.builder()
                .id(1L)
                .title("Capitães da Areia")
                .author("Jorge Amado")
                .publisher("Companhia das Letras")
                .year(Year.of(2008))
                .edition(1)
                .isbn("9788535911695")
                .createdAt(LocalDateTime.parse("2026-04-24T18:00:33"))
                .updatedAt(LocalDateTime.parse("2026-04-24T18:00:33"))
                .build();

        Book gabriel = Book.builder()
                .id(2L)
                .title("Jogos Vorazes")
                .author("Suzanne Collins")
                .publisher("Rocco")
                .year(Year.of(2022))
                .edition(1)
                .isbn("9786555321449")
                .createdAt(LocalDateTime.parse("2026-04-24T18:01:33"))
                .updatedAt(LocalDateTime.parse("2026-04-24T18:01:33"))
                .build();

        Book horaDaEstrela = Book.builder()
                .id(3L)
                .title("A hora da estrela")
                .author("Clarice Lispector")
                .publisher("Rocco")
                .year(Year.of(2020))
                .edition(1)
                .isbn("9786555320350")
                .createdAt(LocalDateTime.parse("2026-04-24T18:02:33"))
                .updatedAt(LocalDateTime.parse("2026-04-24T18:02:33"))
                .build();

        return new ArrayList<>(List.of(capitaesDaAreia, gabriel, horaDaEstrela));
    }

    public Book newBookSaved() {

        return Book.builder()
                .id(99L)
                .title("Dom Casmurro")
                .author("Machado de Assis")
                .publisher("Principis")
                .year(Year.of(2019))
                .edition(3)
                .isbn("9788594318602")
                .createdAt(LocalDateTime.parse("2026-04-24T18:00:33"))
                .updatedAt(LocalDateTime.parse("2026-04-24T18:00:33"))
                .build();
    }

    public BookGetResponse newBookGetResponse() {
        Book book = newBookList().getFirst();

        return new BookGetResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getPublisher(),
                book.getYear(),
                book.getEdition(),
                book.getIsbn(),
                book.getCreatedAt(),
                book.getUpdatedAt());
    }

    public BookGetResponse newBookGetResponseById(Long id) {
        Book book = newBookList().stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Invalid Test: Id Not Found in BookFactory: " + id));

        return new BookGetResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getPublisher(),
                book.getYear(),
                book.getEdition(),
                book.getIsbn(),
                book.getCreatedAt(),
                book.getUpdatedAt());
    }

    public BookPostRequest newBookPostRequest() {
        Book book = newBookSaved();

        return new BookPostRequest(
                book.getTitle(),
                book.getAuthor(),
                book.getPublisher(),
                book.getYear(),
                book.getEdition(),
                book.getIsbn());
    }

    public BookPostResponse newBookPostResponse() {
        Book book = newBookSaved();

        return new BookPostResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getCreatedAt());
    }

    public BookPatchRequest newBookPatchRequest() {
        Book book = newBookList().getFirst();

        return new BookPatchRequest(
               "Mar morto",
                book.getAuthor(),
                book.getPublisher(),
                book.getYear(),
                book.getEdition(),
                "9788535911824");
    }
}
