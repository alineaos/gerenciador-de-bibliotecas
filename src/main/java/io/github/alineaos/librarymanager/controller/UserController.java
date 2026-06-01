package io.github.alineaos.librarymanager.controller;

import io.github.alineaos.librarymanager.dto.users.UserFilter;
import io.github.alineaos.librarymanager.dto.users.UserUpdateRequest;
import io.github.alineaos.librarymanager.dto.users.UserCreateRequest;
import io.github.alineaos.librarymanager.dto.users.UserInfoResponse;
import io.github.alineaos.librarymanager.dto.users.UserCreateResponse;
import io.github.alineaos.librarymanager.security.annotation.IsAdmin;
import io.github.alineaos.librarymanager.security.annotation.IsAdminOrOwner;
import io.github.alineaos.librarymanager.service.UserService;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("v1/users")
public class UserController {
    private final UserService service;

    @GetMapping
    @IsAdmin
    public ResponseEntity<List<UserInfoResponse>> findAll(UserFilter filter) {
        List<UserInfoResponse> responses = service.findAll(filter);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    @IsAdminOrOwner
    public ResponseEntity<UserInfoResponse> findById(@PathVariable Long id) {
        UserInfoResponse response = service.findById(id);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @IsAdmin
    public ResponseEntity<UserCreateResponse> save(@RequestBody @Valid UserCreateRequest request) {
        UserCreateResponse response = service.save(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}")
    @IsAdminOrOwner
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody @Valid UserUpdateRequest request) {
        service.update(id, request);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @IsAdmin
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}
