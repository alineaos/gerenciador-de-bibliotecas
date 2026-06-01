package io.github.alineaos.librarymanager.service;

import io.github.alineaos.librarymanager.domain.entity.Book;
import io.github.alineaos.librarymanager.domain.entity.BookGenre;
import io.github.alineaos.librarymanager.domain.entity.Genre;
import io.github.alineaos.librarymanager.dto.genres.GenreBasicResponse;
import io.github.alineaos.librarymanager.mapper.GenreMapper;
import io.github.alineaos.librarymanager.repository.BookGenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookGenreService {
    private final BookGenreRepository repository;
    private final GenreService genreService;
    private final GenreMapper genreMapper;

    public Map<Long, List<GenreBasicResponse>> findGenresGroupedByBookIds(List<Long> bookIds) {
        if (bookIds == null || bookIds.isEmpty()) return Collections.emptyMap();

        List<BookGenre> bookGenres = repository.findByBookIds(bookIds);
        return bookGenres.stream()
                .collect(Collectors.groupingBy(
                        bg -> bg.getBook().getId(),
                        Collectors.mapping(
                                bg -> genreMapper.toGenreBasicResponse(bg.getGenre()),
                                Collectors.toList()
                        )
                ));

    }

    public List<GenreBasicResponse> findGenresByBookId(Long bookId) {
        List<BookGenre> bookGenres = repository.findByBookId(bookId);

        return bookGenres.stream()
                .map(BookGenre::getGenre)
                .map(genreMapper::toGenreBasicResponse)
                .toList();
    }

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
                .map(genreMapper::toGenreBasicResponse)
                .toList();
    }

    public void updateGenresByBook(Book book, List<Long> newGenresId) {
        Long bookId = book.getId();

        List<Long> savedGenresIds = repository.findByBookId(bookId)
                .stream()
                .map(bg -> bg.getGenre().getId())
                .toList();

        Set<Long> genresToUpdate = new HashSet<>(newGenresId);
        savedGenresIds.forEach(genresToUpdate::remove);

        Set<Long> genresToDelete = new HashSet<>(savedGenresIds);
        newGenresId.forEach(genresToDelete::remove);

        if (!genresToDelete.isEmpty()) {
            repository.deleteByBookIdAndGenreIdIn(bookId, genresToDelete);
        }

        if (!genresToUpdate.isEmpty()) {
            List<BookGenre> bookGenres = genresToUpdate.stream()
                    .map(genreId -> {
                        Genre genre = genreService.getReferenceById(genreId);

                        return BookGenre.builder()
                                .book(book)
                                .genre(genre)
                                .build();
                    })
                    .toList();

            repository.saveAll(bookGenres);
        }
    }

    public void deleteBookGenreByBook(Book book){
        repository.deleteByBook(book);
    }

    public void deleteBookGenreByGenre(Genre genre){
        repository.deleteByGenre(genre);
    }
}
