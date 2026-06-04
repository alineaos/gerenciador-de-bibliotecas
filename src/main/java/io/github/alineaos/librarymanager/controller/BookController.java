package io.github.alineaos.librarymanager.controller;

import io.github.alineaos.librarymanager.dto.books.BookCreateRequest;
import io.github.alineaos.librarymanager.dto.books.BookCreateResponse;
import io.github.alineaos.librarymanager.dto.books.BookFilter;
import io.github.alineaos.librarymanager.dto.books.BookInfoResponse;
import io.github.alineaos.librarymanager.dto.books.BookUpdateRequest;
import io.github.alineaos.librarymanager.dto.errors.DefaultMessageError;
import io.github.alineaos.librarymanager.dto.errors.ValidationMessageError;
import io.github.alineaos.librarymanager.security.annotation.IsAdmin;
import io.github.alineaos.librarymanager.security.annotation.IsUser;
import io.github.alineaos.librarymanager.service.BookService;
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
@RequiredArgsConstructor
@RestController
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Book Management", description = "Book related endpoints")
@RequestMapping("v1/books")
public class BookController {
    private final BookService service;

    @GetMapping
    @IsUser
    @Operation(summary = "Get all books", description = "Retrieves a list of all books available in the system based on the provided filters.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Books retrieved successfully. Returns a list of books matching the criteria.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = BookInfoResponse.class)))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized. User must be authenticated.",
                    content = @Content
            )
    })
    public ResponseEntity<List<BookInfoResponse>> findAll(BookFilter filter) {
        log.info("Request to search all books matching with filters {}", filter);
        List<BookInfoResponse> responses = service.findAll(filter);

        log.debug("Found {} books matching the filter", responses.size());

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    @IsUser
    @Operation(summary = "Get book by id", description = "Retrieves a book details by its unique id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book retrieved successfully.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BookInfoResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized. User must be authenticated.",
                    content = @Content
            ),
            @ApiResponse(responseCode = "404", description = "Book not found. No book found with the given id.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DefaultMessageError.class))
            )
    })
    public ResponseEntity<BookInfoResponse> findById(@PathVariable Long id) {
        log.info("Request to search for book by id {}", id);
        BookInfoResponse response = service.findById(id);

        log.debug("Found book with id {}. Title: '{}'", id, response.title());

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @IsAdmin
    @Operation(summary = "Create book", description = "Creates a new book in the system and saves its data in the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Book created successfully.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BookCreateResponse.class))
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
    public ResponseEntity<BookCreateResponse> save(@RequestBody @Valid BookCreateRequest request) {
        log.info("Request to save book '{}'", request.title());

        BookCreateResponse response = service.save(request);

        log.debug("Book successfully saved with id: {}", response.id());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}")
    @IsAdmin
    @Operation(summary = "Update book", description = "Update a book's data in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Book updated successfully."
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
            @ApiResponse(responseCode = "404", description = "Book not found. No book found with the given id.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DefaultMessageError.class))
            )
    })
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody @Valid BookUpdateRequest request) {
        log.info("Request to update book with id {}", id);

        service.update(id, request);

        log.debug("Book with id {} successfully updated.", id);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @IsAdmin
    @Operation(summary = "Delete book", description = "Removes a book from the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Book deleted successfully."
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
            @ApiResponse(responseCode = "404", description = "Book not found. No book found with the given id.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DefaultMessageError.class))
            )
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Request to delete book with id {}", id);

        service.delete(id);

        log.debug("Book with id {} successfully deleted.", id);

        return ResponseEntity.noContent().build();
    }
}
