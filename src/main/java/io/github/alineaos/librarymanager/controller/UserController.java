package io.github.alineaos.librarymanager.controller;

import io.github.alineaos.librarymanager.dto.UserFilter;
import io.github.alineaos.librarymanager.dto.request.UserPatchRequest;
import io.github.alineaos.librarymanager.dto.request.UserPostRequest;
import io.github.alineaos.librarymanager.dto.response.UserGetResponse;
import io.github.alineaos.librarymanager.dto.response.UserPostResponse;
import io.github.alineaos.librarymanager.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("v1/users")

public class UserController {
    private final UserService service;

    @GetMapping
    public ResponseEntity<List<UserGetResponse>> findAll(UserFilter filter) {
        List<UserGetResponse> getResponseList = service.findAll(filter);
        return ResponseEntity.ok(getResponseList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserGetResponse> findById(@PathVariable Long id) {
        UserGetResponse getResponse = service.findById(id);
        return ResponseEntity.ok(getResponse);
    }

    @PostMapping
    public ResponseEntity<UserPostResponse> save(@RequestBody @Valid UserPostRequest postRequest) {
        UserPostResponse postResponse = service.save(postRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(postResponse);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody UserPatchRequest patchRequest){
        service.update(id, patchRequest);

        return ResponseEntity.noContent().build();
    }
}
