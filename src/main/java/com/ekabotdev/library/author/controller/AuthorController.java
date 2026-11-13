package com.ekabotdev.library.author.controller;

import com.ekabotdev.library.author.dto.AuthorResponse;
import com.ekabotdev.library.author.dto.CreateAuthorRequest;
import com.ekabotdev.library.author.dto.UpdateAuthorRequest;
import com.ekabotdev.library.author.service.AuthorService;
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
@RequestMapping("/api/authors")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<AuthorResponse> createAuthor
            (@Valid @RequestBody CreateAuthorRequest request){
        AuthorResponse authorResponse = authorService.createAuthor(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authorResponse);
    }

    @GetMapping("{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<AuthorResponse> getAuthorById(@PathVariable Long id){
        AuthorResponse authorResponse = authorService.getAuthorById(id);
        return ResponseEntity.ok(authorResponse);
    }

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Page<AuthorResponse>> getAuthors(Pageable pageable){
        return ResponseEntity.ok(authorService.getAuthors(pageable));
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuthorResponse> updateAuthor
            (@PathVariable Long id,
             @Valid @RequestBody UpdateAuthorRequest request){
        AuthorResponse authorResponse = authorService.updateAuthor(id, request);
        return ResponseEntity.ok(authorResponse);
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        authorService.deleteAuthor(id);
        return ResponseEntity
                .noContent()
                .build();
    }

}
