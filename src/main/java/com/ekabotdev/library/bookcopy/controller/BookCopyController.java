package com.ekabotdev.library.bookcopy.controller;

import com.ekabotdev.library.bookcopy.dto.BookCopyResponse;
import com.ekabotdev.library.bookcopy.dto.CreateBookCopyRequest;
import com.ekabotdev.library.bookcopy.dto.UpdateBookCopyConditionRequest;
import com.ekabotdev.library.bookcopy.service.BookCopyService;
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
@RequestMapping("/api/book_copies")
@RequiredArgsConstructor
public class BookCopyController {

    private  final BookCopyService bookCopyService;

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookCopyResponse> createBookCopy
            (@Valid @RequestBody CreateBookCopyRequest request){
        BookCopyResponse response = bookCopyService.createBookCopy(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BookCopyResponse> getBookCopy(@PathVariable Long id){
        return ResponseEntity.ok(bookCopyService.getBookCopyById(id));
    }

@GetMapping
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Page<BookCopyResponse>> getAllBookCopies(Pageable pageable){
        return ResponseEntity.ok(bookCopyService.getAllBookCopies(pageable));
}

  @PatchMapping("/{id}/condition")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookCopyResponse>  updateBookCopy
          (@PathVariable Long id, @Valid @RequestBody UpdateBookCopyConditionRequest request){
        return ResponseEntity.ok(bookCopyService.updateBookCopy(id, request));
  }


  @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBookCopy(@PathVariable Long id){
        bookCopyService.deleteBookCopy(id);
        return ResponseEntity
                .noContent()
                .build();
  }
}
