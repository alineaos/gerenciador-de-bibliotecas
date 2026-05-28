package io.github.alineaos.librarymanager.controller;

import io.github.alineaos.librarymanager.dto.request.LoanPostRequest;
import io.github.alineaos.librarymanager.dto.response.LoanPostResponse;
import io.github.alineaos.librarymanager.security.annotation.IsAdmin;
import io.github.alineaos.librarymanager.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@IsAdmin
@RequestMapping("v1/loans")
public class LoanController {
    private final LoanService service;

    @PostMapping
    public ResponseEntity<LoanPostResponse> save(@RequestBody @Valid LoanPostRequest postRequest){
        LoanPostResponse postResponse = service.save(postRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(postResponse);
    }
}
