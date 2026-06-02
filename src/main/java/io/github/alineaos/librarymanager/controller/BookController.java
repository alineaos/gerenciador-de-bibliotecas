package io.github.alineaos.librarymanager.controller;

import io.github.alineaos.librarymanager.dto.books.BookFilter;
import io.github.alineaos.librarymanager.dto.books.BookUpdateRequest;
import io.github.alineaos.librarymanager.dto.books.BookCreateRequest;
import io.github.alineaos.librarymanager.dto.books.BookInfoResponse;
import io.github.alineaos.librarymanager.dto.books.BookCreateResponse;
import io.github.alineaos.librarymanager.security.annotation.IsAdmin;
import io.github.alineaos.librarymanager.security.annotation.IsUser;
import io.github.alineaos.librarymanager.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("v1/books")
public class BookController {
    private final BookService service;

    @GetMapping
    @IsUser
    public ResponseEntity<List<BookInfoResponse>> findAll(BookFilter filter){
       log.info("Request to search all books matching with filters {}", filter);
        List<BookInfoResponse> responses =  service.findAll(filter);

        log.debug("Found {} books matching the filter", responses.size());

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    @IsUser
    public ResponseEntity<BookInfoResponse> findById(@PathVariable Long id){
        log.info("Request to search for book by id {}", id);
        BookInfoResponse response =  service.findById(id);

        log.debug("Found book with id {}. Title: '{}'", id, response.title());

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @IsAdmin
    public ResponseEntity<BookCreateResponse> save(@RequestBody @Valid BookCreateRequest request){
        log.info("Request to save book '{}'", request.title());

        BookCreateResponse response =  service.save(request);

        log.debug("Book successfully saved with id: {}", response.id());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}")
    @IsAdmin
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody @Valid BookUpdateRequest request){
        log.info("Request to update book with id {}", id);

        service.update(id, request);

        log.debug("Book with id {} successfully updated.", id);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @IsAdmin
    public ResponseEntity<Void> delete(@PathVariable Long id){
        log.info("Request to delete book with id {}", id);

        service.delete(id);

        log.debug("Book with id {} successfully deleted.", id);

        return ResponseEntity.noContent().build();
    }
}
