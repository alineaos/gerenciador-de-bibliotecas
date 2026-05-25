package io.github.alineaos.librarymanager.service;

import io.github.alineaos.librarymanager.domain.entity.Book;
import io.github.alineaos.librarymanager.dto.BookFilter;
import io.github.alineaos.librarymanager.dto.request.BookPatchRequest;
import io.github.alineaos.librarymanager.dto.request.BookPostRequest;
import io.github.alineaos.librarymanager.dto.response.BookGetResponse;
import io.github.alineaos.librarymanager.dto.response.BookPostResponse;
import io.github.alineaos.librarymanager.dto.response.GenreBasicResponse;
import io.github.alineaos.librarymanager.exception.BusinessException;
import io.github.alineaos.librarymanager.exception.NotFoundException;
import io.github.alineaos.librarymanager.mapper.BookMapper;
import io.github.alineaos.librarymanager.repository.BookRepository;
import io.github.alineaos.librarymanager.repository.specification.BookSpecification;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Validated
@Service
public class BookService {
    private final BookRepository repository;
    private final BookGenreService bookGenreService;
    private final GenreService genreService;
    private final BookMapper mapper;

    public List<BookGetResponse> findAll(BookFilter bookFilter) {
        List<Book> books = repository.findAll(
                BookSpecification.hasTitle(bookFilter.title())
                        .and(BookSpecification.hasAuthor(bookFilter.author()))
                        .and(BookSpecification.hasPublisher(bookFilter.publisher()))
                        .and(BookSpecification.hasYear(bookFilter.year()))
                        .and(BookSpecification.hasEdition(bookFilter.edition()))
                        .and(BookSpecification.hasIsbn(bookFilter.isbn()))
                        .and(BookSpecification.hasGenreWithName(bookFilter.genreName()))
        );

        List<Long> bookIds = books.stream().map(Book::getId).toList();

        Map<Long, List<GenreBasicResponse>> genresByBookId = bookGenreService.findGenresGroupedByBookIds(bookIds);

        return mapper.toBookGetResponseList(books, genresByBookId);
    }

    public BookGetResponse findById(Long id) {
        Book book = findByIdOrThrowNotFound(id);
        List<GenreBasicResponse> genresByBookId = bookGenreService.findGenresByBookId(id);
        return mapper.toBookGetResponse(book, genresByBookId);
    }

    public BookPostResponse save(@Valid BookPostRequest postRequest) {
        List<Long> genresById = postRequest.genreIds();
        assertIsbnDoesNotExists(postRequest.isbn());
        genreService.assertGenreByIdExists(genresById);

        Book bookToSave = mapper.toBook(postRequest);

        Book bookSaved = repository.save(bookToSave);

        List<GenreBasicResponse> genreResponses = bookGenreService.addGenresToBook(bookSaved, genresById);

        return mapper.toBookPostResponse(bookSaved, genreResponses);
    }

    @Transactional
    public void update(Long id, @Valid BookPatchRequest patchRequest) {
        Book bookToUpdate = findByIdOrThrowNotFound(id);

        if (patchRequest.isbn() != null) {
            assertIsbnDoesNotExists(patchRequest.isbn(), id);
        }

        if (patchRequest.genreIds() != null && !patchRequest.genreIds().isEmpty()){
            bookGenreService.updateGenresByBook(bookToUpdate, patchRequest.genreIds());
        }

        mapper.mergeRequestToBook(patchRequest, bookToUpdate);

        repository.save(bookToUpdate);
    }

    @Transactional
    public void delete(Long id) {
        Book book = findByIdOrThrowNotFound(id);

        bookGenreService.deleteBookGenreByBook(book);
        repository.delete(book);
    }

    private void assertIsbnDoesNotExists(String isbn) {
        repository.findByIsbn(isbn).ifPresent(this::throwIsbnAlreadyExists);
    }

    private void assertIsbnDoesNotExists(String isbn, Long id) {
        repository.findByIsbnAndIdNot(isbn, id).ifPresent(this::throwIsbnAlreadyExists);
    }

    private void throwIsbnAlreadyExists(Book book) {
        throw new BusinessException("Isbn '%s' already exists".formatted(book.getIsbn()));
    }

    private Book findByIdOrThrowNotFound(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new NotFoundException("Book not found.")
        );
    }
}
