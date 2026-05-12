package io.github.alineaos.librarymanager.service;

import io.github.alineaos.librarymanager.domain.entity.Book;
import io.github.alineaos.librarymanager.dto.BookFilter;
import io.github.alineaos.librarymanager.dto.request.BookPostRequest;
import io.github.alineaos.librarymanager.dto.response.BookGetResponse;
import io.github.alineaos.librarymanager.dto.response.BookPostResponse;
import io.github.alineaos.librarymanager.exception.BusinessException;
import io.github.alineaos.librarymanager.mapper.BookMapper;
import io.github.alineaos.librarymanager.repository.BookRepository;
import io.github.alineaos.librarymanager.repository.specification.BookSpecification;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@RequiredArgsConstructor
@Validated
@Service
public class BookService {
    private final BookRepository repository;
    private final BookMapper mapper;

    public List<BookGetResponse> findAll(BookFilter bookFilter){
        List<Book> books = repository.findAll(
                BookSpecification.hasTitle(bookFilter.title())
                        .and(BookSpecification.hasAuthor(bookFilter.author()))
                        .and(BookSpecification.hasPublisher(bookFilter.publisher()))
                        .and(BookSpecification.hasYear(bookFilter.year()))
                        .and(BookSpecification.hasEdition(bookFilter.edition()))
                        .and(BookSpecification.hasIsbn(bookFilter.isbn()))
        );

        return mapper.toBookGetResponseList(books);
    }

    public BookPostResponse save(@Valid BookPostRequest postRequest){
        assertIsbnNotExists(postRequest.isbn());

        Book bookToSave = mapper.toBook(postRequest);

        Book bookSaved = repository.save(bookToSave);

        return mapper.toBookPostResponse(bookSaved);
    }

    private void assertIsbnNotExists(String isbn){
        repository.findByIsbn(isbn).ifPresent(this::throwIsbnAlreadyExists);
    }

    private void throwIsbnAlreadyExists(Book book){
        throw new BusinessException("Isbn '%s' already exists".formatted(book.getIsbn()));
    }
}
