package io.github.alineaos.librarymanager.controller;

import io.github.alineaos.librarymanager.dto.loans.LoanFilter;
import io.github.alineaos.librarymanager.dto.loans.LoanCreateRequest;
import io.github.alineaos.librarymanager.dto.loans.LoanReturnRequest;
import io.github.alineaos.librarymanager.dto.loans.LoanInfoResponse;
import io.github.alineaos.librarymanager.dto.loans.LoanHistoryResponse;
import io.github.alineaos.librarymanager.dto.loans.LoanCreateResponse;
import io.github.alineaos.librarymanager.security.annotation.IsAdmin;
import io.github.alineaos.librarymanager.security.annotation.IsAuthenticatedUser;
import io.github.alineaos.librarymanager.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
public class LoanController {
    private final LoanService service;

    @GetMapping
    public ResponseEntity<List<LoanInfoResponse>> findAll(LoanFilter filter) {
        log.info("Request to search all loans matching with filters {}", filter);

        List<LoanInfoResponse> responses = service.findAll(filter);

        log.debug("Found {} loans matching the filter", responses.size());

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoanInfoResponse> findById(@PathVariable Long id) {
        log.info("Request to search for loan by id {}", id);

        LoanInfoResponse response = service.findById(id);

        log.debug("Found loan with id {}. User id: {}, Book id {}", id, response.user().id(), response.book().id());


        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-history")
    @IsAuthenticatedUser
    public ResponseEntity<List<LoanHistoryResponse>> findMyHistory(@AuthenticationPrincipal Jwt jwt) {
        Long loggedUserId = jwt.getClaim("userId");

        log.info("Request to search for loans history by user id {}", loggedUserId);

        List<LoanHistoryResponse> responses = service.findMyHistory(loggedUserId);

        log.debug("Found {} loans for user id {}", responses.size(), loggedUserId);

        return ResponseEntity.ok(responses);
    }

    @PostMapping
    public ResponseEntity<LoanCreateResponse> save(@RequestBody @Valid LoanCreateRequest request) {
        log.info("Request to save loan with user id {} and book id {}", request.userId(), request.bookId());

        LoanCreateResponse response = service.save(request);

        log.debug("Loan successfully saved with id: {}", response.id());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}/renew")
    public ResponseEntity<Void> renew(@PathVariable Long id){
        log.info("Request to renew loan with id {}", id);

        service.renew(id);

        log.debug("Loan with id {} successfully renewed", id);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/return")
    public ResponseEntity<Void> finalize(@PathVariable Long id, @RequestBody @Valid LoanReturnRequest request){
        log.info("Request to return loan with id {}", id);

        service.finalize(id, request);

        log.debug("Loan with id {} successfully returned", id);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/lost")
    public ResponseEntity<Void> lost(@PathVariable Long id){
        log.info("Request to mark loan with id {} as LOST", id);

        service.lost(id);

        log.debug("Loan with id {} successfully marked as LOST", id);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long id){
        log.info("Request to cancel loan with id {}", id);

        service.cancel(id);

        log.debug("Loan with id {} successfully cancelled", id);

        return ResponseEntity.noContent().build();
    }

}
