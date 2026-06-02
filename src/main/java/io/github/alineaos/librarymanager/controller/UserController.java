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
@RestController
@RequiredArgsConstructor
@RequestMapping("v1/users")
public class UserController {
    private final UserService service;

    @GetMapping
    @IsAdmin
    public ResponseEntity<List<UserInfoResponse>> findAll(UserFilter filter) {
        log.info("Request to search all users matching with filters {}", filter);

        List<UserInfoResponse> responses = service.findAll(filter);

        log.debug("Found {} users matching the filter", responses.size());

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    @IsAdminOrOwner
    public ResponseEntity<UserInfoResponse> findById(@PathVariable Long id) {
        log.info("Request to search for user by id {}", id);

        UserInfoResponse response = service.findById(id);

        log.debug("Found user with id {}. Full Name: '{}'", id, response.fullName());

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @IsAdmin
    public ResponseEntity<UserCreateResponse> save(@RequestBody @Valid UserCreateRequest request) {
        log.info("Request to save user '{}'", request.fullName());

        UserCreateResponse response = service.save(request);

        log.debug("User successfully saved with id: {}", response.id());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}")
    @IsAdminOrOwner
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody @Valid UserUpdateRequest request) {
        log.info("Request to update user with id {}", id);

        service.update(id, request);

        log.debug("User with id {} successfully updated.", id);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @IsAdmin
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Request to delete user with id {}", id);

        service.delete(id);

        log.debug("User with id {} successfully deleted.", id);

        return ResponseEntity.noContent().build();
    }
}
