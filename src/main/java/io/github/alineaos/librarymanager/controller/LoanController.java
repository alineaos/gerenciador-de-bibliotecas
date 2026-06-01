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

@RestController
@RequiredArgsConstructor
@IsAdmin
@RequestMapping("v1/loans")
public class LoanController {
    private final LoanService service;

    @GetMapping
    public ResponseEntity<List<LoanInfoResponse>> findAll(LoanFilter filter) {
        List<LoanInfoResponse> responses = service.findAll(filter);

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoanInfoResponse> findById(@PathVariable Long id) {
        LoanInfoResponse response = service.findById(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-history")
    @IsAuthenticatedUser
    public ResponseEntity<List<LoanHistoryResponse>> findMyHistory(@AuthenticationPrincipal Jwt jwt) {
        Long loggedUserId = jwt.getClaim("userId");

        List<LoanHistoryResponse> responses = service.findMyHistory(loggedUserId);

        return ResponseEntity.ok(responses);
    }

    @PostMapping
    public ResponseEntity<LoanCreateResponse> save(@RequestBody @Valid LoanCreateRequest request) {
        LoanCreateResponse response = service.save(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}/renew")
    public ResponseEntity<Void> renew(@PathVariable Long id){
        service.renew(id);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/return")
    public ResponseEntity<Void> finalize(@PathVariable Long id, @RequestBody @Valid LoanReturnRequest request){
        service.finalize(id, request);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/lost")
    public ResponseEntity<Void> lost(@PathVariable Long id){
        service.lost(id);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long id){
        service.cancel(id);

        return ResponseEntity.noContent().build();
    }

}
