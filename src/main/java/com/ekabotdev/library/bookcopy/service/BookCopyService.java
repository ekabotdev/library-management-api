package com.ekabotdev.library.bookcopy.service;

import com.ekabotdev.library.book.entity.Book;
import com.ekabotdev.library.book.exception.BookNotFoundException;
import com.ekabotdev.library.book.repository.BookRepository;
import com.ekabotdev.library.bookcopy.dto.BookCopyResponse;
import com.ekabotdev.library.bookcopy.dto.CreateBookCopyRequest;
import com.ekabotdev.library.bookcopy.dto.UpdateBookCopyConditionRequest;
import com.ekabotdev.library.bookcopy.entity.BookCopy;
import com.ekabotdev.library.bookcopy.exception.BookCopyBorrowedException;
import com.ekabotdev.library.bookcopy.exception.BookCopyNotFoundException;
import com.ekabotdev.library.bookcopy.exception.BookCopyNotInGoodCondition;
import com.ekabotdev.library.bookcopy.repository.BookCopyRepository;
import com.ekabotdev.library.enums.CopyCondition;
import com.ekabotdev.library.enums.CopyStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookCopyService {

    private final BookCopyRepository bookCopyRepository;
    private final BookRepository bookRepository;

    @Transactional
    public BookCopyResponse createBookCopy(CreateBookCopyRequest request) {

        Book book  = bookRepository.findById(request.bookId()).orElseThrow(()
                -> new BookNotFoundException("Book with id " + request.bookId() + " not found"));
        BookCopy bookCopy = new BookCopy();
        bookCopy.setBook(book);
        bookCopy.setStatus(CopyStatus.AVAILABLE);
        bookCopy.setConditionStatus(CopyCondition.GOOD);

        BookCopy savedBookCopy = bookCopyRepository.save(bookCopy);

        return toResponse(savedBookCopy);

    }
    private BookCopyResponse toResponse(BookCopy bookCopy) {
         return  new BookCopyResponse(
                 bookCopy.getId(),
                 bookCopy.getBook().getId(),
                 bookCopy.getStatus(),
                 bookCopy.getConditionStatus()
         );
    }

    @Transactional(readOnly = true)
    public BookCopyResponse getBookCopyById(Long id) {
        BookCopy copy = bookCopyRepository.findById(id).orElseThrow(()
                -> new BookCopyNotFoundException("BookCopy  with id " + id + " not found"));

        return toResponse(copy);
    }
    @Transactional(readOnly = true)
    public Page<BookCopyResponse> getAllBookCopies(Pageable pageable) {
        return bookCopyRepository.findAll(pageable)
                .map(this::toResponse);
    }

    @Transactional
    public BookCopyResponse updateBookCopy(Long id, UpdateBookCopyConditionRequest request) {
        BookCopy copy = bookCopyRepository.findById(id).orElseThrow(()
                -> new BookCopyNotFoundException("BookCopy with id " + id + " not found"));

        copy.setConditionStatus(request.conditionStatus());

        return toResponse(copy);
    }

    @Transactional
    public void deleteBookCopy(Long id) {
        BookCopy copy = bookCopyRepository.findById(id).orElseThrow(()
                -> new BookCopyNotFoundException("Book copy with id " + id + " not found"));

        if (copy.getStatus() == CopyStatus.BORROWED) {
            throw new BookCopyBorrowedException("Book copy is already borrowed");
        }

        if (copy.getConditionStatus() != CopyCondition.GOOD) {
            throw new BookCopyNotInGoodCondition("Book Copy not in good condition");
        }
        bookCopyRepository.deleteById(id);
    }

}
