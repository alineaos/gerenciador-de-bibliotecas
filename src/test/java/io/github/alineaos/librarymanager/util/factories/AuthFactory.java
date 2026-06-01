package io.github.alineaos.librarymanager.util.factories;

import io.github.alineaos.librarymanager.domain.entity.Book;
import io.github.alineaos.librarymanager.domain.entity.Genre;
import io.github.alineaos.librarymanager.domain.entity.User;
import io.github.alineaos.librarymanager.dto.books.BookCreateRequest;
import io.github.alineaos.librarymanager.dto.books.BookCreateResponse;
import io.github.alineaos.librarymanager.dto.books.BookInfoResponse;
import io.github.alineaos.librarymanager.dto.books.BookUpdateRequest;
import io.github.alineaos.librarymanager.dto.genres.GenreBasicResponse;
import io.github.alineaos.librarymanager.dto.users.UserLoginRequest;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class AuthFactory {
    private final UserFactory userFactory;

    public AuthFactory(UserFactory userFactory) {
        this.userFactory = userFactory;
    }

    public UserLoginRequest newUserLoginRequest() {
        User userSaved = userFactory.newUserList().getFirst();

        return new UserLoginRequest(userSaved.getEmail(),
                userSaved.getPassword());
    }

    public static class BookFactory {
        private final GenreFactory genreFactory;

        public BookFactory(GenreFactory genreFactory) {
            this.genreFactory = genreFactory;
        }

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

            Book jogosVorazes = Book.builder()
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

            return new ArrayList<>(List.of(capitaesDaAreia, jogosVorazes, horaDaEstrela));
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

        public BookInfoResponse newBookInfoResponse() {
            Book book = newBookList().getFirst();

            return new BookInfoResponse(
                    book.getId(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getPublisher(),
                    book.getYear(),
                    book.getEdition(),
                    book.getIsbn(),
                    genreNacionalResponseList(),
                    book.getCreatedAt(),
                    book.getUpdatedAt());
        }

        public BookInfoResponse newBookInfoResponseById(Long id) {
            Book book = newBookList().stream()
                    .filter(b -> b.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Invalid Test: Id Not Found in BookFactory: " + id));

            return new BookInfoResponse(
                    book.getId(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getPublisher(),
                    book.getYear(),
                    book.getEdition(),
                    book.getIsbn(),
                    getGenresForBook(book.getId()),
                    book.getCreatedAt(),
                    book.getUpdatedAt());
        }

        public BookCreateRequest newBookCreateRequest() {
            Book book = newBookSaved();

            return new BookCreateRequest(
                    book.getTitle(),
                    book.getAuthor(),
                    book.getPublisher(),
                    book.getYear(),
                    book.getEdition(),
                    book.getIsbn(),
                    newGenreIdsList());
        }

        public BookCreateResponse newBookCreateResponse() {
            Book book = newBookSaved();

            return new BookCreateResponse(
                    book.getId(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getIsbn(),
                    genreNacionalResponseList(),
                    book.getCreatedAt());
        }

        public BookUpdateRequest newBookUpdateRequest() {
            Book book = newBookList().getFirst();

            return new BookUpdateRequest(
                    "Mar morto",
                    book.getAuthor(),
                    book.getPublisher(),
                    book.getYear(),
                    book.getEdition(),
                    "9788535911824",
                    newGenreIdsList());
        }

        public List<GenreBasicResponse> genreNacionalResponseList() {
            Genre nacional = genreFactory.newGenreNacional();

            return Stream.of(nacional)
                    .map(g -> new GenreBasicResponse(g.getId(), g.getName()))
                    .toList();
        }

        public List<GenreBasicResponse> genreFiccaoResponseList() {
            Genre ficcao = genreFactory.newGenreFiccao();

            return Stream.of(ficcao)
                    .map(g -> new GenreBasicResponse(g.getId(), g.getName()))
                    .toList();
        }

        public List<GenreBasicResponse> getGenresForBook(Long bookId) {
            List<Long> nacionalBookIds = List.of(1L, 3L, 99L);
            List<Long> ficcaoBookIds = List.of(2L);

            if (nacionalBookIds.contains(bookId)) {
                return genreNacionalResponseList();
            }

            if (ficcaoBookIds.contains(bookId)) {
                return genreFiccaoResponseList();
            }

            throw new IllegalArgumentException("Invalid Book Id %d. Please, update the genreBasicResponseList() method".formatted(bookId));
        }

        private List<Long> newGenreIdsList(){
            return genreNacionalResponseList()
                    .stream()
                    .map(GenreBasicResponse::id)
                    .toList();
        }
    }
}
