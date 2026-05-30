package io.github.alineaos.librarymanager.controller;

import io.github.alineaos.librarymanager.dto.LoanFilter;
import io.github.alineaos.librarymanager.dto.request.LoanPostRequest;
import io.github.alineaos.librarymanager.dto.request.LoanReturnRequest;
import io.github.alineaos.librarymanager.dto.response.LoanGetResponse;
import io.github.alineaos.librarymanager.dto.response.LoanHistoryResponse;
import io.github.alineaos.librarymanager.dto.response.LoanPostResponse;
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
    public ResponseEntity<List<LoanGetResponse>> findAll(LoanFilter loanFilter) {
        List<LoanGetResponse> getResponseList = service.findAll(loanFilter);

        return ResponseEntity.ok(getResponseList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoanGetResponse> findById(@PathVariable Long id) {
        LoanGetResponse getResponse = service.findById(id);

        return ResponseEntity.ok(getResponse);
    }

    @GetMapping("/my-history")
    @IsAuthenticatedUser
    public ResponseEntity<List<LoanHistoryResponse>> findMyHistory(@AuthenticationPrincipal Jwt jwt) {
        Long loggedUserId = jwt.getClaim("userId");

        List<LoanHistoryResponse> historyResponseList = service.findMyHistory(loggedUserId);

        return ResponseEntity.ok(historyResponseList);
    }

    @PostMapping
    public ResponseEntity<LoanPostResponse> save(@RequestBody @Valid LoanPostRequest postRequest) {
        LoanPostResponse postResponse = service.save(postRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(postResponse);
    }

    @PatchMapping("/{id}/renew")
    public ResponseEntity<Void> renew(@PathVariable Long id){
        service.renew(id);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/return")
    public ResponseEntity<Void> finalize(@PathVariable Long id, @RequestBody @Valid LoanReturnRequest returnRequest){
        service.finalize(id, returnRequest);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/lost")
    public ResponseEntity<Void> lost(@PathVariable Long id){
        service.lost(id);

        return ResponseEntity.noContent().build();
    }
}
