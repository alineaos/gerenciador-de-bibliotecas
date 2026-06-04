package io.github.alineaos.librarymanager.controller;

import io.github.alineaos.librarymanager.dto.errors.DefaultMessageError;
import io.github.alineaos.librarymanager.dto.errors.ValidationMessageError;
import io.github.alineaos.librarymanager.dto.loans.LoanCreateRequest;
import io.github.alineaos.librarymanager.dto.loans.LoanCreateResponse;
import io.github.alineaos.librarymanager.dto.loans.LoanFilter;
import io.github.alineaos.librarymanager.dto.loans.LoanHistoryResponse;
import io.github.alineaos.librarymanager.dto.loans.LoanInfoResponse;
import io.github.alineaos.librarymanager.dto.loans.LoanReturnRequest;
import io.github.alineaos.librarymanager.security.annotation.IsAdmin;
import io.github.alineaos.librarymanager.security.annotation.IsAuthenticatedUser;
import io.github.alineaos.librarymanager.service.LoanService;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
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
@IsAdmin
@RequestMapping("v1/loans")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Loan Management", description = "Loan related endpoints")
public class LoanController {
    private final LoanService service;

    @GetMapping
    @Operation(summary = "Get all loans", description = "Retrieves a list of all loans available in the system based on the provided filters.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Loans retrieved successfully. Returns a list of loans matching the criteria.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = LoanInfoResponse.class)))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized. User must be authenticated.",
                    content = @Content
            ),
            @ApiResponse(responseCode = "403", description = "Forbidden. Admin privileges required.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DefaultMessageError.class))
            )
    })
    public ResponseEntity<List<LoanInfoResponse>> findAll(LoanFilter filter) {
        log.info("Request to search all loans matching with filters {}", filter);

        List<LoanInfoResponse> responses = service.findAll(filter);

        log.debug("Found {} loans matching the filter", responses.size());

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get loan by id", description = "Retrieves a loan details by its unique id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Loan retrieved successfully.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = LoanInfoResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized. User must be authenticated.",
                    content = @Content
            ),
            @ApiResponse(responseCode = "403", description = "Forbidden. Admin privileges required.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DefaultMessageError.class))
            ),
            @ApiResponse(responseCode = "404", description = "Loan not found. No loan found with the given id.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DefaultMessageError.class))
            )
    })
    public ResponseEntity<LoanInfoResponse> findById(@PathVariable Long id) {
        log.info("Request to search for loan by id {}", id);

        LoanInfoResponse response = service.findById(id);

        log.debug("Found loan with id {}. User id: {}, Book id {}", id, response.user().id(), response.book().id());


        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-history")
    @IsAuthenticatedUser
    @Operation(summary = "Get logged-in user's loan history", description = "Retrieves a complete list of all loans associated with the currently authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Loans history retrieved successfully. Returns a list of the user's loans.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = LoanHistoryResponse.class)))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized. User must be authenticated.",
                    content = @Content
            )
    })
    public ResponseEntity<List<LoanHistoryResponse>> findMyHistory(@AuthenticationPrincipal Jwt jwt) {
        Long loggedUserId = jwt.getClaim("userId");

        log.info("Request to search for loans history by user id {}", loggedUserId);

        List<LoanHistoryResponse> responses = service.findMyHistory(loggedUserId);

        log.debug("Found {} loans for user id {}", responses.size(), loggedUserId);

        return ResponseEntity.ok(responses);
    }

    @PostMapping
    @Operation(summary = "Create loan", description = "Creates a new loan in the system and saves its data in the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Loan created successfully.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = LoanCreateResponse.class))
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
    public ResponseEntity<LoanCreateResponse> save(@RequestBody @Valid LoanCreateRequest request) {
        log.info("Request to save loan with user id {} and book id {}", request.userId(), request.bookId());

        LoanCreateResponse response = service.save(request);

        log.debug("Loan successfully saved with id: {}", response.id());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}/renew")
    @Operation(summary = "Renew loan", description = "Updates a loan's status to RENEWED in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Loan renewed successfully."
            ),
            @ApiResponse(responseCode = "400", description = "Bad request. Business rule violation.",
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
            @ApiResponse(responseCode = "404", description = "Loan not found. No loan found with the given id.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DefaultMessageError.class))
            )
    })
    public ResponseEntity<Void> renew(@PathVariable Long id) {
        log.info("Request to renew loan with id {}", id);

        service.renew(id);

        log.debug("Loan with id {} successfully renewed", id);

        return ResponseEntity.noContent().build();
    }


    @PatchMapping("/{id}/return")
    @Operation(summary = "Return loan", description = "Updates a loan's status to RETURNED in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Loan returned successfully."
            ),
            @ApiResponse(responseCode = "400", description = "Bad request. Invalid fields or Business rule violation.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(oneOf = {DefaultMessageError.class, ValidationMessageError.class}))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized. User must be authenticated.",
                    content = @Content
            ),
            @ApiResponse(responseCode = "403", description = "Forbidden. Admin privileges required.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DefaultMessageError.class))
            ),
            @ApiResponse(responseCode = "404", description = "Loan not found. No loan found with the given id.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DefaultMessageError.class))
            )
    })
    public ResponseEntity<Void> finalize(@PathVariable Long id, @RequestBody @Valid LoanReturnRequest request) {
        log.info("Request to return loan with id {}", id);

        service.finalize(id, request);

        log.debug("Loan with id {} successfully returned", id);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/lost")
    @Operation(summary = "Mark loan as lost", description = "Updates a loan's status to LOST in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Loan marked as lost successfully."
            ),
            @ApiResponse(responseCode = "400", description = "Bad request. Business rule violation.",
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
            @ApiResponse(responseCode = "404", description = "Loan not found. No loan found with the given id.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DefaultMessageError.class))
            )
    })
    public ResponseEntity<Void> lost(@PathVariable Long id) {
        log.info("Request to mark loan with id {} as LOST", id);

        service.lost(id);

        log.debug("Loan with id {} successfully marked as LOST", id);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancel loan", description = "Updates a loan's status to CANCELLED in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Loan cancelled successfully."
            ),
            @ApiResponse(responseCode = "400", description = "Bad request. Business rule violation.",
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
            @ApiResponse(responseCode = "404", description = "Loan not found. No loan found with the given id.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DefaultMessageError.class))
            )
    })
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        log.info("Request to cancel loan with id {}", id);

        service.cancel(id);

        log.debug("Loan with id {} successfully cancelled", id);

        return ResponseEntity.noContent().build();
    }

}
