package com.ekabotdev.library.loan.controller;

import com.ekabotdev.library.enums.LoanStatus;
import com.ekabotdev.library.loan.dto.CreateLoanRequest;
import com.ekabotdev.library.loan.dto.LoanResponse;
import com.ekabotdev.library.loan.service.LoanService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<LoanResponse> createLoan
            (@Valid @RequestBody CreateLoanRequest createLoanRequest) {
        LoanResponse loanResponse = loanService.createLoan(createLoanRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(loanResponse);
    }

    @PostMapping("/{id}/return")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<LoanResponse> returnLoan(@PathVariable Long id) {
        LoanResponse loanResponse = loanService.returnLoan(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(loanResponse);
    }

    @GetMapping("/my")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Page<LoanResponse>> myLoans(Pageable pageable) {
        return ResponseEntity.ok(loanService.myLoans(pageable));
    }

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<LoanResponse>> getAllLoans(
            @RequestParam(required = false) LoanStatus loanStatus,
            Pageable pageable) {
        return ResponseEntity.ok(loanService.getAllLoans(loanStatus,pageable));
    }
}
