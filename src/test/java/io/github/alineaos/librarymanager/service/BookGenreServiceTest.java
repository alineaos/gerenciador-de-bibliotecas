package io.github.alineaos.librarymanager.service;

import io.github.alineaos.librarymanager.config.UnitTestConfig;
import io.github.alineaos.librarymanager.domain.entity.Book;
import io.github.alineaos.librarymanager.domain.entity.BookGenre;
import io.github.alineaos.librarymanager.domain.entity.Genre;
import io.github.alineaos.librarymanager.dto.genres.GenreBasicResponse;
import io.github.alineaos.librarymanager.repository.BookGenreRepository;
import io.github.alineaos.librarymanager.util.BookFactory;
import io.github.alineaos.librarymanager.util.BookGenreFactory;
import io.github.alineaos.librarymanager.util.GenreFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookGenreServiceTest extends UnitTestConfig {
    @InjectMocks
    private BookGenreService service;
    @Mock
    private BookGenreRepository repository;
    @Mock
    private GenreService genreService;

    private final GenreFactory genreFactory = new GenreFactory();
    private final BookFactory bookFactory = new BookFactory(genreFactory);
    private final BookGenreFactory bookGenreFactory = new BookGenreFactory(bookFactory, genreFactory);

    private List<BookGenre> bookGenreList;

    @BeforeEach
    void init() {
        bookGenreList = bookGenreFactory.newBookGenreList();
    }

    @Test
    @DisplayName("findGenresGroupedByBookIds returns a map with bookIds and GenreBasicResponseList when booksIds is not null")
    @Order(1)
    void findGenresGroupedByBookIds_ReturnsMapWithBookIdsAngGenreBasicResponseList_WhenBooksIdsIsNotNull() {
        List<BookGenre> booksNacionalGenre = bookGenreList.stream()
                .filter(bg -> bg.getGenre().getName().equalsIgnoreCase("Nacional"))
                .toList();

        List<Long> bookIds = booksNacionalGenre.stream()
                .map(bookGenre -> bookGenre.getBook().getId())
                .toList();

        Map<Long, List<GenreBasicResponse>> expectedMap = booksNacionalGenre.stream()
                .collect(Collectors.groupingBy(
                        bg -> bg.getBook().getId(),
                        Collectors.mapping(
                                bg -> {
                                    Genre genre = bg.getGenre();
                                    return new GenreBasicResponse(genre.getId(), genre.getName());
                                }, Collectors.toList()
                        )
                ));

        when(repository.findByBookIds(bookIds)).thenReturn(booksNacionalGenre);

        Map<Long, List<GenreBasicResponse>> genresGroupedByBookIds = service.findGenresGroupedByBookIds(bookIds);

        Assertions.assertThat(genresGroupedByBookIds.keySet()).isNotNull().containsExactlyElementsOf(bookIds);
        Assertions.assertThat(genresGroupedByBookIds).containsExactlyInAnyOrderEntriesOf(expectedMap);
    }

    @ParameterizedTest(name = "[{index}] bookIds = {0}")
    @NullAndEmptySource
    @DisplayName("findGenresGroupedByBookIds returns an empty map when booksIds is empty or null")
    @Order(2)
    void findGenresGroupedByBookIds_ReturnsEmptyMap_WhenBooksIdsIsEmptyOrNull(List<Long> bookIds) {
        Map<Long, List<GenreBasicResponse>> genresGroupedByBookIds = service.findGenresGroupedByBookIds(bookIds);

        Assertions.assertThat(genresGroupedByBookIds).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("findGenresByBookId returns a list with GenreBasicResponse when successful")
    @Order(3)
    void findGenresByBookId_ReturnsGenreBasicResponseList_WhenSuccessful() {
        Long bookId = 1L;

        List<BookGenre> expectedBookGenres = bookGenreList.stream()
                .filter(bg -> bg.getBook().getId().equals(bookId))
                .toList();

        List<GenreBasicResponse> expectedGenreResponses = expectedBookGenres.stream()
                .map(bg -> {
                    Genre genre = bg.getGenre();
                    return new GenreBasicResponse(genre.getId(), genre.getName());
                })
                .toList();

        when(repository.findByBookId(bookId)).thenReturn(expectedBookGenres);

        List<GenreBasicResponse> genresByBookId = service.findGenresByBookId(bookId);

        Assertions.assertThat(genresByBookId).isNotNull().containsExactlyElementsOf(expectedGenreResponses);
    }

    @Test
    @DisplayName("addGenresToBook returns a list with GenreBasicResponse and creates BookGenre association when successful")
    @Order(4)
    void addGenresToBook_ReturnsGenreBasicResponseListAndCreatesBookGenreAssociation_WhenSuccessful() {
        Book book = bookFactory.newBookSaved();
        Genre genre = genreFactory.newGenreNacional();
        List<Long> genreIds = List.of(genre.getId());

        List<BookGenre> expectedBookGenres = List.of(bookGenreFactory.newBookGenreSaved(book, genre));

        List<GenreBasicResponse> expectedGenreResponses = expectedBookGenres.stream()
                .map(bg -> {
                    Genre bgToGenre = bg.getGenre();
                    return new GenreBasicResponse(bgToGenre.getId(), bgToGenre.getName());
                })
                .toList();

        when(genreService.getReferenceById(any())).thenReturn(genre);
        when(repository.saveAll(any())).thenReturn(expectedBookGenres);

        List<GenreBasicResponse> genresByBookId = service.addGenresToBook(book, genreIds);

        Assertions.assertThat(genresByBookId).isNotNull().containsExactlyElementsOf(expectedGenreResponses);
    }

    @Test
    @DisplayName("updateGenresByBook deletes old genres and save new ones when genreIds lists are different")
    @Order(5)
    void updateGenresByBook_DeleteOldGenresAndSaveNewOnes_WhenGenreIdsListsAreDifferent() {
        Book book = bookFactory.newBookSaved();
        Genre genreToDelete = genreFactory.newGenreSaved();
        Genre genreToKeep = genreFactory.newGenreNacional();
        Genre genreToUpdate = genreFactory.newGenreList().get(1);

        List<Long> newGenreIds = List.of(
                genreToKeep.getId(),
                genreToUpdate.getId()
        );

        List<BookGenre> currentBookGenres = List.of(
                bookGenreFactory.newBookGenreSaved(book, genreToDelete),
                bookGenreFactory.newBookGenreSaved(book, genreToKeep)
        );

        when(repository.findByBookId(book.getId())).thenReturn(currentBookGenres);
        when(genreService.getReferenceById(any())).thenReturn(genreToUpdate);

        service.updateGenresByBook(book, newGenreIds);

        List<BookGenre> bookGenreToSave = List.of(bookGenreFactory.newBookGenre(book, genreToUpdate));

        verify(repository, times(1)).deleteByBookIdAndGenreIdIn(book.getId(), Set.of(genreToDelete.getId()));
        verify(repository, times(1)).saveAll(bookGenreToSave);
    }

    @Test
    @DisplayName("updateGenresByBook should do nothing when new genreIds are equals to saved genres")
    @Order(6)
    void updateGenresByBook_ShouldDoNothing_WhenNewGenreIdsAreEqualsToSavedGenres() {
        Book book = bookFactory.newBookSaved();
        Genre genreToKeep = genreFactory.newGenreNacional();

        List<Long> genreIds = List.of(genreToKeep.getId());

        List<BookGenre> currentBookGenres = List.of(bookGenreFactory.newBookGenreSaved(book, genreToKeep));

        when(repository.findByBookId(book.getId())).thenReturn(currentBookGenres);

        service.updateGenresByBook(book, genreIds);

        verify(genreService, never()).getReferenceById(any());
        verify(repository, never()).deleteByBookIdAndGenreIdIn(any(), any());
        verify(repository, never()).saveAll(any());
    }

    @Test
    @DisplayName("deleteBookGenreByBook deletes associations when successful")
    @Order(7)
    void deleteBookGenreByBook_DeletesAssociations_WhenSuccessful() {
        Book book = bookFactory.newBookSaved();

        service.deleteBookGenreByBook(book);

        verify(repository, times(1)).deleteByBook(book);
    }

    @Test
    @DisplayName("deleteBookGenreByGenre deletes associations when successful")
    @Order(8)
    void deleteBookGenreByGenre_DeletesAssociations_WhenSuccessful() {
        Genre genre = genreFactory.newGenreSaved();

        service.deleteBookGenreByGenre(genre);

        verify(repository, times(1)).deleteByGenre(genre);
    }
}