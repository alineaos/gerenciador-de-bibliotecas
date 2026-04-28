package io.github.alineaos.librarymanager.controller;

import io.github.alineaos.librarymanager.dto.UserFilter;
import io.github.alineaos.librarymanager.dto.request.UserPatchRequest;
import io.github.alineaos.librarymanager.dto.request.UserPostRequest;
import io.github.alineaos.librarymanager.dto.response.UserGetResponse;
import io.github.alineaos.librarymanager.dto.response.UserPostResponse;
import io.github.alineaos.librarymanager.security.annotation.IsAdmin;
import io.github.alineaos.librarymanager.security.annotation.IsAdminOrOwner;
import io.github.alineaos.librarymanager.security.domain.UserAuthenticated;
import io.github.alineaos.librarymanager.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
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
    public ResponseEntity<List<UserGetResponse>> findAll(UserFilter filter) {
        List<UserGetResponse> getResponseList = service.findAll(filter);
        return ResponseEntity.ok(getResponseList);
    }

    @GetMapping("/{id}")
    @IsAdminOrOwner
    public ResponseEntity<UserGetResponse> findById(@PathVariable Long id, Authentication authentication) {
        UserGetResponse getResponse = service.findById(id);
        if (authentication.getPrincipal() instanceof Jwt jwt){
            System.out.println(jwt.getClaims().get("userId"));
            System.out.println(id);
        }
        return ResponseEntity.ok(getResponse);
    }

    @PostMapping
    @IsAdmin
    public ResponseEntity<UserPostResponse> save(@RequestBody @Valid UserPostRequest postRequest) {
        UserPostResponse postResponse = service.save(postRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(postResponse);
    }

    @PatchMapping("/{id}")
    @IsAdminOrOwner
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody @Valid UserPatchRequest patchRequest) {
        service.update(id, patchRequest);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @IsAdmin
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}
