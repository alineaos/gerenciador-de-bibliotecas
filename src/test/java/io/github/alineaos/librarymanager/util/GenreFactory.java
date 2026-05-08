package io.github.alineaos.librarymanager.util;

import io.github.alineaos.librarymanager.domain.entity.Genre;
import io.github.alineaos.librarymanager.dto.request.GenrePostRequest;
import io.github.alineaos.librarymanager.dto.request.GenrePutRequest;
import io.github.alineaos.librarymanager.dto.response.GenreGetResponse;
import io.github.alineaos.librarymanager.dto.response.GenrePostResponse;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GenreFactory {
    public List<Genre> newGenreList() {
        Genre fantasia = Genre.builder()
                .id(1L)
                .name("Fantasia")
                .createdAt(LocalDateTime.parse("2026-04-24T18:00:33"))
                .updatedAt(LocalDateTime.parse("2026-04-24T18:00:33"))
                .build();

        Genre romance = Genre.builder()
                .id(2L)
                .name("Romance")
                .createdAt(LocalDateTime.parse("2026-04-24T18:01:33"))
                .updatedAt(LocalDateTime.parse("2026-04-24T18:01:33"))
                .build();

        Genre sciFi = Genre.builder()
                .id(3L)
                .name("Ficção Científica")
                .createdAt(LocalDateTime.parse("2026-04-24T18:02:33"))
                .updatedAt(LocalDateTime.parse("2026-04-24T18:02:33"))
                .build();

        return new ArrayList<>(List.of(fantasia, romance, sciFi));
    }

    public Genre newGenreSaved() {

        return Genre.builder()
                .id(99L)
                .name("Aventura")
                .createdAt(LocalDateTime.parse("2026-04-24T18:44:33"))
                .updatedAt(LocalDateTime.parse("2026-04-24T18:44:33"))
                .build();
    }

    public GenreGetResponse newGenreGetResponse() {
        Genre genre = newGenreList().getFirst();

        return new GenreGetResponse(
                genre.getId(),
                genre.getName(),
                genre.getCreatedAt(),
                genre.getUpdatedAt());
    }

    public GenreGetResponse newGenreGetResponseById(Long id) {
        Genre genre = newGenreList().stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Invalid Test: Id Not Found in GenreFactory: " + id));

        return new GenreGetResponse(
                genre.getId(),
                genre.getName(),
                genre.getCreatedAt(),
                genre.getUpdatedAt());
    }

    public GenrePostRequest newGenrePostRequest() {
        Genre genre = newGenreSaved();

        return new GenrePostRequest(
                genre.getName()
        );
    }

    public GenrePostResponse newGenrePostResponse() {
        Genre genre = newGenreSaved();

        return new GenrePostResponse(
                genre.getId(),
                genre.getName(),
                genre.getCreatedAt());
    }

    public GenrePutRequest newGenrePutRequest() {
        return new GenrePutRequest(
                "Fantasy");
    }
}
