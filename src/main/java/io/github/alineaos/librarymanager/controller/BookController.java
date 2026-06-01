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

@RequiredArgsConstructor
@RestController
@RequestMapping("v1/books")
public class BookController {
    private final BookService service;

    @GetMapping
    @IsUser
    public ResponseEntity<List<BookInfoResponse>> findAll(BookFilter filter){
       List<BookInfoResponse> responses =  service.findAll(filter);

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    @IsUser
    public ResponseEntity<BookInfoResponse> findById(@PathVariable Long id){
        BookInfoResponse response =  service.findById(id);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @IsAdmin
    public ResponseEntity<BookCreateResponse> save(@RequestBody @Valid BookCreateRequest request){
        BookCreateResponse response =  service.save(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}")
    @IsAdmin
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody @Valid BookUpdateRequest request){
        service.update(id, request);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @IsAdmin
    public ResponseEntity<Void> delete(@PathVariable Long id){
        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}
