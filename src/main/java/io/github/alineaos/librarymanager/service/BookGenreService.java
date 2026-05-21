package io.github.alineaos.librarymanager.service;

import io.github.alineaos.librarymanager.domain.entity.Book;
import io.github.alineaos.librarymanager.domain.entity.BookGenre;
import io.github.alineaos.librarymanager.domain.entity.Genre;
import io.github.alineaos.librarymanager.dto.response.GenreBasicResponse;
import io.github.alineaos.librarymanager.repository.BookGenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookGenreService {
    private final BookGenreRepository repository;
    private final GenreService genreService;

    public List<GenreBasicResponse> addGenresToBook(Book book, List<Long> genreIds) {
        List<BookGenre> bookGenres = genreIds.stream()
                .map(genreId -> {
                            Genre genre = genreService.getReferenceById(genreId);

                            return BookGenre.builder()
                                    .book(book)
                                    .genre(genre)
                                    .build();
                        }
                )
                .toList();

        List<BookGenre> savedBookGenres = repository.saveAll(bookGenres);

        return savedBookGenres.stream()
                .map(BookGenre::getGenre)
                .map(g -> new GenreBasicResponse(g.getId(), g.getName()))
                .toList();
    }
}
