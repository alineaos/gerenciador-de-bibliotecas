package io.github.alineaos.librarymanager.controller;

import io.github.alineaos.librarymanager.dto.errors.DefaultMessageError;
import io.github.alineaos.librarymanager.dto.errors.ValidationMessageError;
import io.github.alineaos.librarymanager.dto.users.UserCreateRequest;
import io.github.alineaos.librarymanager.dto.users.UserCreateResponse;
import io.github.alineaos.librarymanager.dto.users.UserFilter;
import io.github.alineaos.librarymanager.dto.users.UserInfoResponse;
import io.github.alineaos.librarymanager.dto.users.UserUpdateRequest;
import io.github.alineaos.librarymanager.security.annotation.IsAdmin;
import io.github.alineaos.librarymanager.security.annotation.IsAdminOrOwner;
import io.github.alineaos.librarymanager.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "User Management", description = "User related endpoints")
public class UserController {
    private final UserService service;

    @GetMapping
    @IsAdmin
    @Operation(summary = "Get all users", description = "Retrieves a list of all users available in the system based on the provided filters.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully. Returns a list of users matching the criteria.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = UserInfoResponse.class)))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized. User must be authenticated.",
                    content = @Content
            ),
            @ApiResponse(responseCode = "403", description = "Forbidden. Admin privileges required.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DefaultMessageError.class))
            )
    })
    public ResponseEntity<List<UserInfoResponse>> findAll(UserFilter filter) {
        log.info("Request to search all users matching with filters {}", filter);

        List<UserInfoResponse> responses = service.findAll(filter);

        log.debug("Found {} users matching the filter", responses.size());

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    @IsAdminOrOwner
    @Operation(summary = "Get user by id", description = "Retrieves a user details by its unique id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserInfoResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized. User must be authenticated.",
                    content = @Content
            ),
            @ApiResponse(responseCode = "403", description = "Forbidden. Admin privileges or id owner required.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DefaultMessageError.class))
            ),
            @ApiResponse(responseCode = "404", description = "User not found. No user found with the given id.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DefaultMessageError.class))
            )
    })
    public ResponseEntity<UserInfoResponse> findById(@PathVariable Long id) {
        log.info("Request to search for user by id {}", id);

        UserInfoResponse response = service.findById(id);

        log.debug("Found user with id {}. Full Name: '{}'", id, response.fullName());

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @IsAdmin
    @Operation(summary = "Create user", description = "Creates a new user in the system and saves their data in the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserCreateResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Bad request. Invalid fields.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(oneOf = {DefaultMessageError.class, ValidationMessageError.class}))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized. User must be authenticated.",
                    content = @Content
            ),
            @ApiResponse(responseCode = "403", description = "Forbidden. Admin privileges required.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DefaultMessageError.class))
            )
    })
    public ResponseEntity<UserCreateResponse> save(@RequestBody @Valid UserCreateRequest request) {
        log.info("Request to save user '{}'", request.fullName());

        UserCreateResponse response = service.save(request);

        log.debug("User successfully saved with id: {}", response.id());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}")
    @IsAdminOrOwner
    @Operation(summary = "Update user", description = "Update a user's data in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User updated successfully."
            ),
            @ApiResponse(responseCode = "400", description = "Bad request. Invalid fields.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(oneOf = {DefaultMessageError.class, ValidationMessageError.class}))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized. User must be authenticated.",
                    content = @Content
            ),
            @ApiResponse(responseCode = "403", description = "Forbidden. Admin privileges or id owner required.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DefaultMessageError.class))
            ),
            @ApiResponse(responseCode = "404", description = "User not found. No user found with the given id.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DefaultMessageError.class))
            )
    })
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody @Valid UserUpdateRequest request) {
        log.info("Request to update user with id {}", id);

        service.update(id, request);

        log.debug("User with id {} successfully updated.", id);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @IsAdmin
    @Operation(summary = "Delete user", description = "Removes a user from the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully."
            ),
            @ApiResponse(responseCode = "400", description = "Bad request. Invalid fields.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DefaultMessageError.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized. User must be authenticated.",
                    content = @Content
            ),
            @ApiResponse(responseCode = "403", description = "Forbidden. Admin privileges required.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DefaultMessageError.class))
            ),
            @ApiResponse(responseCode = "404", description = "User not found. No user found with the given id.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DefaultMessageError.class))
            )
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Request to delete user with id {}", id);

        service.delete(id);

        log.debug("User with id {} successfully deleted.", id);

        return ResponseEntity.noContent().build();
    }
}
