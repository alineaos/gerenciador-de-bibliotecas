package io.github.alineaos.librarymanager.controller;

import io.github.alineaos.librarymanager.dto.genres.GenreFilter;
import io.github.alineaos.librarymanager.dto.genres.GenreCreateRequest;
import io.github.alineaos.librarymanager.dto.genres.GenreUpdateRequest;
import io.github.alineaos.librarymanager.dto.genres.GenreInfoResponse;
import io.github.alineaos.librarymanager.dto.genres.GenreCreateResponse;
import io.github.alineaos.librarymanager.security.annotation.IsAdmin;
import io.github.alineaos.librarymanager.security.annotation.IsUser;
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

    @IsUser
    @GetMapping
    public ResponseEntity<List<GenreInfoResponse>> findAll(GenreFilter filter){
        List<GenreInfoResponse> responses = service.findAll(filter);

        return ResponseEntity.ok(responses);
    }

    @IsUser
    @GetMapping("/{id}")
    public ResponseEntity<GenreInfoResponse> findById(@PathVariable Long id){
        GenreInfoResponse response = service.findById(id);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<GenreCreateResponse> save(@RequestBody @Valid GenreCreateRequest request){
        GenreCreateResponse response = service.save(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody @Valid GenreUpdateRequest request){
        service.update(id, request);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}
