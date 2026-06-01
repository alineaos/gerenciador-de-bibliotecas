package io.github.alineaos.librarymanager.util.factories;

import io.github.alineaos.librarymanager.domain.entity.Genre;
import io.github.alineaos.librarymanager.dto.genres.GenreCreateRequest;
import io.github.alineaos.librarymanager.dto.genres.GenreUpdateRequest;
import io.github.alineaos.librarymanager.dto.genres.GenreInfoResponse;
import io.github.alineaos.librarymanager.dto.genres.GenreCreateResponse;

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

    public GenreInfoResponse newGenreInfoResponse() {
        Genre genre = newGenreList().getFirst();

        return new GenreInfoResponse(
                genre.getId(),
                genre.getName(),
                genre.getCreatedAt(),
                genre.getUpdatedAt());
    }

    public GenreInfoResponse newGenreInfoResponseById(Long id) {
        Genre genre = newGenreList().stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Invalid Test: Id Not Found in GenreFactory: " + id));

        return new GenreInfoResponse(
                genre.getId(),
                genre.getName(),
                genre.getCreatedAt(),
                genre.getUpdatedAt());
    }

    public GenreCreateRequest newGenreCreateRequest() {
        Genre genre = newGenreSaved();

        return new GenreCreateRequest(
                genre.getName()
        );
    }

    public GenreCreateResponse newGenreCreateResponse() {
        Genre genre = newGenreSaved();

        return new GenreCreateResponse(
                genre.getId(),
                genre.getName(),
                genre.getCreatedAt());
    }

    public GenreUpdateRequest newGenreUpdateRequest() {
        return new GenreUpdateRequest(
                "Fantasy");
    }

    public Genre newGenreNacional() {
       return  Genre.builder()
                .id(10L)
                .name("Nacional")
                .createdAt(LocalDateTime.parse("2026-04-24T18:45:33"))
                .updatedAt(LocalDateTime.parse("2026-04-24T18:45:33"))
                .build();
    }

    public Genre newGenreFiccao() {
        return  Genre.builder()
                .id(11L)
                .name("Ficção")
                .createdAt(LocalDateTime.parse("2026-04-24T18:46:33"))
                .updatedAt(LocalDateTime.parse("2026-04-24T18:46:33"))
                .build();
    }
}
