package com.ekabotdev.library.exception;

import com.ekabotdev.library.author.exception.AuthorAlreadyExistsException;
import com.ekabotdev.library.author.exception.AuthorHasBooksException;
import com.ekabotdev.library.author.exception.AuthorNotFoundException;
import com.ekabotdev.library.book.exception.BookHasCopiesException;
import com.ekabotdev.library.book.exception.BookNotFoundException;
import com.ekabotdev.library.bookcopy.exception.BookCopyBorrowedException;
import com.ekabotdev.library.bookcopy.exception.BookCopyHasLoanHistoryException;
import com.ekabotdev.library.bookcopy.exception.BookCopyNotFoundException;
import com.ekabotdev.library.bookcopy.exception.BookCopyNotInGoodCondition;
import com.ekabotdev.library.loan.exception.*;
import com.ekabotdev.library.member.exception.EmailAlreadyExistsException;
import com.ekabotdev.library.member.exception.MemberNotFoundException;
import com.ekabotdev.library.member.exception.MembershipStatusNotValidException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

@ExceptionHandler(AuthorNotFoundException.class)
    public ResponseEntity<ApiError> handleAuthorNotFoundException
        (AuthorNotFoundException exception){
    ApiError error = new ApiError(
            HttpStatus.NOT_FOUND.value(),
            exception.getMessage(),
            LocalDateTime.now()
    );
    return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(error);
        }

        @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<ApiError> handleBookNotFoundException
        (BookNotFoundException exception){
    ApiError error = new ApiError(
            HttpStatus.NOT_FOUND.value(),
            exception.getMessage(),
            LocalDateTime.now()
    );
    return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(error);
        }

        @ExceptionHandler(BookHasCopiesException.class)
    public ResponseEntity<ApiError> handleBookHasCopiesException
        (BookHasCopiesException exception){
    ApiError error = new ApiError(
            HttpStatus.CONFLICT.value(),
            exception.getMessage(),
            LocalDateTime.now()
    );
    return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(error);
        }

        @ExceptionHandler(AuthorHasBooksException.class)
    public ResponseEntity<ApiError> handleAuthorHasBooksException
        (AuthorHasBooksException exception){
    ApiError error = new ApiError(
            HttpStatus.CONFLICT.value(),
            exception.getMessage(),
            LocalDateTime.now()
    );
    return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(error);
        }

        @ExceptionHandler(BookCopyNotFoundException.class)
    public ResponseEntity<ApiError> handleBookCopyNotFoundException
        (BookCopyNotFoundException exception){
    ApiError error = new ApiError(
            HttpStatus.NOT_FOUND.value(),
            exception.getMessage(),
            LocalDateTime.now()
    );
    return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(error);
        }

        @ExceptionHandler(BookCopyBorrowedException.class)
    public ResponseEntity<ApiError> handleBookCopyBorrowedException
        (BookCopyBorrowedException exception){
    ApiError error = new ApiError(
            HttpStatus.CONFLICT.value(),
            exception.getMessage(),
            LocalDateTime.now()
    );
    return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(error);
        }

        @ExceptionHandler(BookCopyNotInGoodCondition.class)
    public ResponseEntity<ApiError> handleBookCopyNotInGoodCondition
        (BookCopyNotInGoodCondition exception){
    ApiError error = new ApiError(
            HttpStatus.CONFLICT.value(),
            exception.getMessage(),
            LocalDateTime.now()
    );
    return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(error);
        }

        @ExceptionHandler(BookCopyHasLoanHistoryException.class)
    public ResponseEntity<ApiError> handleBookCopyHasLoanHistoryException
        (BookCopyHasLoanHistoryException exception){
    ApiError error = new ApiError(
            HttpStatus.CONFLICT.value(),
            exception.getMessage(),
            LocalDateTime.now()
    );
    return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(error);
        }

        @ExceptionHandler(LoanAlreadyReturnedException.class)
    public ResponseEntity<ApiError> handleLoanAlreadyReturnedException
        (LoanAlreadyReturnedException exception){

    ApiError error = new ApiError(
            HttpStatus.CONFLICT.value(),
            exception.getMessage(),
            LocalDateTime.now()
    );
    return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(error);
        }

        @ExceptionHandler(LoanNotFoundException.class)
    public ResponseEntity<ApiError> handleLoanNotFoundException
        (LoanNotFoundException exception){
    ApiError error = new ApiError(
            HttpStatus.NOT_FOUND.value(),
            exception.getMessage(),
            LocalDateTime.now()
    );
    return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(error);
        }

        @ExceptionHandler(LoanOwnershipException.class)
    public ResponseEntity<ApiError> handleLoanOwnershipException
        (LoanOwnershipException exception){
    ApiError error = new ApiError(
            HttpStatus.CONFLICT.value(),
            exception.getMessage(),
            LocalDateTime.now()
    );
    return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(error);
        }

        @ExceptionHandler(AuthorAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleAuthorAlreadyExistsException
        (AuthorAlreadyExistsException exception){
    ApiError error = new ApiError(
            HttpStatus.CONFLICT.value(),
            exception.getMessage(),
            LocalDateTime.now()
    );
    return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(error);
        }

        @ExceptionHandler(MemberNotFoundException.class)
        public ResponseEntity<ApiError> handleMemberNotFoundException
                (MemberNotFoundException exception){
    ApiError error = new ApiError(
            HttpStatus.NOT_FOUND.value(),
            exception.getMessage(),
            LocalDateTime.now()
    );
    return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(error);
        }

        @ExceptionHandler(MembershipStatusNotValidException.class)
    public  ResponseEntity<ApiError> handleMembershipStatusNotValidException
        (MembershipStatusNotValidException exception){
    ApiError error = new ApiError(
            HttpStatus.BAD_REQUEST.value(),
            exception.getMessage(),
            LocalDateTime.now()
    );
    return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(error);
        }

        @ExceptionHandler(MemberNotActiveException.class)
    public ResponseEntity<ApiError> handleMemberNotActiveException
        (MemberNotActiveException exception){

    ApiError error = new ApiError(
            HttpStatus.BAD_REQUEST.value(),
            exception.getMessage(),
            LocalDateTime.now()
    );

    return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(error);
        }

        @ExceptionHandler(InvalidBookCopyStateException.class)
    public ResponseEntity<ApiError> handleInvalidBookCopyStateException
        (InvalidBookCopyStateException exception){

    ApiError error = new ApiError(
            HttpStatus.BAD_REQUEST.value(),
            exception.getMessage(),
            LocalDateTime.now()
    );

    return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(error);
        }

        @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleEmailAlreadyExistsException
                (EmailAlreadyExistsException exception){

    ApiError error = new ApiError(
            HttpStatus.CONFLICT.value(),
            exception.getMessage(),
            LocalDateTime.now()
    );

    return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(error);
        }
}
