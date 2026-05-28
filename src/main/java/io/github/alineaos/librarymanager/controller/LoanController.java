package io.github.alineaos.librarymanager.controller;

import io.github.alineaos.librarymanager.dto.LoanFilter;
import io.github.alineaos.librarymanager.dto.request.LoanPostRequest;
import io.github.alineaos.librarymanager.dto.response.LoanGetResponse;
import io.github.alineaos.librarymanager.dto.response.LoanPostResponse;
import io.github.alineaos.librarymanager.security.annotation.IsAdmin;
import io.github.alineaos.librarymanager.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

    @PostMapping
    public ResponseEntity<LoanPostResponse> save(@RequestBody @Valid LoanPostRequest postRequest) {
        LoanPostResponse postResponse = service.save(postRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(postResponse);
    }
}
