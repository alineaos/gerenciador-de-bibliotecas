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
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("v1/genres")
@IsAdmin
public class GenreController {
    private final GenreService service;

    @IsUser
    @GetMapping
    public ResponseEntity<List<GenreInfoResponse>> findAll(GenreFilter filter){
        log.info("Request to search all genres matching with filters {}", filter);

        List<GenreInfoResponse> responses = service.findAll(filter);

        log.debug("Found {} genres matching the filter", responses.size());

        return ResponseEntity.ok(responses);
    }

    @IsUser
    @GetMapping("/{id}")
    public ResponseEntity<GenreInfoResponse> findById(@PathVariable Long id){
        log.info("Request to search for genre by id {}", id);

        GenreInfoResponse response = service.findById(id);

        log.debug("Found genre with id {}. Name: '{}'", id, response.name());

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<GenreCreateResponse> save(@RequestBody @Valid GenreCreateRequest request){
        log.info("Request to save genre '{}'", request.name());

        GenreCreateResponse response = service.save(request);

        log.debug("Genre successfully saved with id: {}", response.id());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody @Valid GenreUpdateRequest request){
        log.info("Request to update genre with id {}", id);

        service.update(id, request);

        log.debug("Genre with id {} successfully updated.", id);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        log.info("Request to delete genre with id {}", id);

        service.delete(id);

        log.debug("Genre with id {} successfully deleted.", id);

        return ResponseEntity.noContent().build();
    }
}
