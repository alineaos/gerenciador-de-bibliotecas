package io.github.alineaos.librarymanager.service;

import io.github.alineaos.librarymanager.domain.entity.Book;
import io.github.alineaos.librarymanager.dto.books.BookCreateRequest;
import io.github.alineaos.librarymanager.dto.books.BookFilter;
import io.github.alineaos.librarymanager.dto.books.BookUpdateRequest;
import io.github.alineaos.librarymanager.dto.books.BookInfoResponse;
import io.github.alineaos.librarymanager.dto.books.BookCreateResponse;
import io.github.alineaos.librarymanager.dto.genres.GenreBasicResponse;
import io.github.alineaos.librarymanager.exception.BusinessException;
import io.github.alineaos.librarymanager.exception.NotFoundException;
import io.github.alineaos.librarymanager.mapper.BookMapper;
import io.github.alineaos.librarymanager.repository.BookRepository;
import io.github.alineaos.librarymanager.repository.specification.BookSpecification;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Validated
@Service
public class BookService {
    private final BookRepository repository;
    private final BookGenreService bookGenreService;
    private final GenreService genreService;
    private final BookMapper mapper;

    public List<BookInfoResponse> findAll(BookFilter bookFilter) {
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

        return mapper.toBookInfoResponseList(books, genresByBookId);
    }

    public BookInfoResponse findById(Long id) {
        Book book = findByIdOrThrowNotFound(id);
        List<GenreBasicResponse> genresByBookId = bookGenreService.findGenresByBookId(id);
        return mapper.toBookInfoResponse(book, genresByBookId);
    }

    public BookCreateResponse save(@Valid BookCreateRequest request) {
        List<Long> genresById = request.genreIds();
        assertIsbnDoesNotExist(request.isbn());
        genreService.assertGenreByIdExists(genresById);

        Book bookToSave = mapper.toBook(request);

        Book bookSaved = repository.save(bookToSave);

        List<GenreBasicResponse> genreResponses = bookGenreService.addGenresToBook(bookSaved, genresById);

        return mapper.toBookCreateResponse(bookSaved, genreResponses);
    }

    @Transactional
    public void update(Long id, @Valid BookUpdateRequest request) {
        Book bookToUpdate = findByIdOrThrowNotFound(id);

        if (request.isbn() != null) {
            assertIsbnDoesNotExist(request.isbn(), id);
        }

        if (request.genreIds() != null && !request.genreIds().isEmpty()){
            bookGenreService.updateGenresByBook(bookToUpdate, request.genreIds());
        }

        mapper.mergeRequestToBook(request, bookToUpdate);

        repository.save(bookToUpdate);
    }

    @Transactional
    public void delete(Long id) {
        Book book = findByIdOrThrowNotFound(id);

        bookGenreService.deleteBookGenreByBook(book);
        repository.delete(book);
    }

    public Book getBookByIdOrThrowNotFound(Long id){
        return findByIdOrThrowNotFound(id);
    }

    private void assertIsbnDoesNotExist(String isbn) {
        repository.findByIsbn(isbn).ifPresent(this::throwIsbnAlreadyExists);
    }

    private void assertIsbnDoesNotExist(String isbn, Long id) {
        repository.findByIsbnAndIdNot(isbn, id).ifPresent(this::throwIsbnAlreadyExists);
    }

    private void throwIsbnAlreadyExists(Book book) {
        log.warn("Validation failed: ISBN '{}' already exists in the database for the book id {}", book.getIsbn(), book.getId());
        throw new BusinessException("Isbn '%s' already exists".formatted(book.getIsbn()));
    }

    private Book findByIdOrThrowNotFound(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new NotFoundException("Book not found.")
        );
    }
}
