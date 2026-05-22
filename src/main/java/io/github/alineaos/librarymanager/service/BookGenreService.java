package io.github.alineaos.librarymanager.service;

import io.github.alineaos.librarymanager.domain.entity.Book;
import io.github.alineaos.librarymanager.domain.entity.BookGenre;
import io.github.alineaos.librarymanager.domain.entity.Genre;
import io.github.alineaos.librarymanager.dto.response.GenreBasicResponse;
import io.github.alineaos.librarymanager.repository.BookGenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
                .map(this::newGenreBasicResponse)
                .toList();
    }

    public Map<Long, List<GenreBasicResponse>> findGenresGroupedByBookIds(List<Long> bookIds) {
        if (bookIds == null || bookIds.isEmpty()) return Collections.emptyMap();

        List<BookGenre> bookGenres = repository.findByBookIds(bookIds);
        return bookGenres.stream()
                .collect(Collectors.groupingBy(
                        bg -> bg.getBook().getId(),
                        Collectors.mapping(
                                bg -> newGenreBasicResponse(bg.getGenre()),
                                Collectors.toList()
                        )
                ));

    }

    private GenreBasicResponse newGenreBasicResponse(Genre genre){
        return new GenreBasicResponse(
                genre.getId(),
                genre.getName()
        );
    }
}
