package io.github.alineaos.librarymanager.controller;

import io.github.alineaos.librarymanager.dto.BookFilter;
import io.github.alineaos.librarymanager.dto.request.BookPostRequest;
import io.github.alineaos.librarymanager.dto.response.BookGetResponse;
import io.github.alineaos.librarymanager.dto.response.BookPostResponse;
import io.github.alineaos.librarymanager.security.annotation.IsAdmin;
import io.github.alineaos.librarymanager.security.annotation.IsUser;
import io.github.alineaos.librarymanager.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
    public ResponseEntity<List<BookGetResponse>> findAll(BookFilter bookFilter){
       List<BookGetResponse> getResponseList =  service.findAll(bookFilter);

        return ResponseEntity.ok(getResponseList);
    }

    @GetMapping("/{id}")
    @IsUser
    public ResponseEntity<BookGetResponse> findById(@PathVariable Long id){
        BookGetResponse getResponse =  service.findById(id);

        return ResponseEntity.ok(getResponse);
    }

    @PostMapping
    @IsAdmin
    public ResponseEntity<BookPostResponse> save(@RequestBody @Valid BookPostRequest postRequest){
        BookPostResponse postResponse =  service.save(postRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(postResponse);
    }
}
