package io.github.alineaos.librarymanager.controller;

import io.github.alineaos.librarymanager.dto.GenreFilter;
import io.github.alineaos.librarymanager.dto.request.GenrePostRequest;
import io.github.alineaos.librarymanager.dto.request.GenrePutRequest;
import io.github.alineaos.librarymanager.dto.response.GenreGetResponse;
import io.github.alineaos.librarymanager.dto.response.GenrePostResponse;
import io.github.alineaos.librarymanager.security.annotation.IsAdmin;
import io.github.alineaos.librarymanager.service.GenreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("v1/genres")
@IsAdmin
public class GenreController {
    private final GenreService service;

    @GetMapping
    public ResponseEntity<List<GenreGetResponse>> findAll(GenreFilter filter){
        List<GenreGetResponse> getResponseList = service.findAll(filter);

        return ResponseEntity.ok(getResponseList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenreGetResponse> findById(@PathVariable Long id){
        GenreGetResponse getResponse = service.findById(id);

        return ResponseEntity.ok(getResponse);
    }

    @PostMapping
    public ResponseEntity<GenrePostResponse> save(@RequestBody @Valid GenrePostRequest postRequest){
        GenrePostResponse postResponse = service.save(postRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(postResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody @Valid GenrePutRequest putRequest){
        service.update(id, putRequest);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}
