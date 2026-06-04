package io.github.alineaos.librarymanager.controller;

import io.github.alineaos.librarymanager.dto.errors.DefaultMessageError;
import io.github.alineaos.librarymanager.dto.errors.ValidationMessageError;
import io.github.alineaos.librarymanager.dto.genres.GenreCreateRequest;
import io.github.alineaos.librarymanager.dto.genres.GenreCreateResponse;
import io.github.alineaos.librarymanager.dto.genres.GenreFilter;
import io.github.alineaos.librarymanager.dto.genres.GenreInfoResponse;
import io.github.alineaos.librarymanager.dto.genres.GenreUpdateRequest;
import io.github.alineaos.librarymanager.security.annotation.IsAdmin;
import io.github.alineaos.librarymanager.security.annotation.IsUser;
import io.github.alineaos.librarymanager.service.GenreService;
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
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Genre Management", description = "Genre related endpoints")
@IsAdmin
public class GenreController {
    private final GenreService service;

    @IsUser
    @GetMapping
    @Operation(summary = "Get all genres", description = "Retrieves a list of all genres available in the system based on the provided filters.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Genres retrieved successfully. Returns a list of genres matching the criteria.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = GenreInfoResponse.class)))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized. User must be authenticated.",
                    content = @Content
            )
    })
    public ResponseEntity<List<GenreInfoResponse>> findAll(GenreFilter filter){
        log.info("Request to search all genres matching with filters {}", filter);

        List<GenreInfoResponse> responses = service.findAll(filter);

        log.debug("Found {} genres matching the filter", responses.size());

        return ResponseEntity.ok(responses);
    }

    @IsUser
    @GetMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Genre retrieved successfully.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = GenreInfoResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized. User must be authenticated.",
                    content = @Content
            ),
            @ApiResponse(responseCode = "404", description = "Genre not found. No genre found with the given id.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DefaultMessageError.class))
            )
    })
    public ResponseEntity<GenreInfoResponse> findById(@PathVariable Long id){
        log.info("Request to search for genre by id {}", id);

        GenreInfoResponse response = service.findById(id);

        log.debug("Found genre with id {}. Name: '{}'", id, response.name());

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Create genre", description = "Creates a new genre in the system and saves its data in the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Genre created successfully.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = GenreCreateResponse.class))
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
    public ResponseEntity<GenreCreateResponse> save(@RequestBody @Valid GenreCreateRequest request){
        log.info("Request to save genre '{}'", request.name());

        GenreCreateResponse response = service.save(request);

        log.debug("Genre successfully saved with id: {}", response.id());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update genre", description = "Update a genre's data in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Genre updated successfully."
            ),
            @ApiResponse(responseCode = "400", description = "Bad request. Invalid fields.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(oneOf = {DefaultMessageError.class, ValidationMessageError.class}))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized. User must be authenticated.",
                    content = @Content
            ),
            @ApiResponse(responseCode = "403", description = "Forbidden. Admin privileges.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DefaultMessageError.class))
            ),
            @ApiResponse(responseCode = "404", description = "Genre not found. No genre found with the given id.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DefaultMessageError.class))
            )
    })
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody @Valid GenreUpdateRequest request){
        log.info("Request to update genre with id {}", id);

        service.update(id, request);

        log.debug("Genre with id {} successfully updated.", id);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete genre", description = "Removes a genre from the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Genre deleted successfully."
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
            @ApiResponse(responseCode = "404", description = "Genre not found. No genre found with the given id.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DefaultMessageError.class))
            )
    })
    public ResponseEntity<Void> delete(@PathVariable Long id){
        log.info("Request to delete genre with id {}", id);

        service.delete(id);

        log.debug("Genre with id {} successfully deleted.", id);

        return ResponseEntity.noContent().build();
    }
}
