package com.ekabotdev.library.book.service;

import com.ekabotdev.library.author.entity.Author;
import com.ekabotdev.library.author.exception.AuthorNotFoundException;
import com.ekabotdev.library.author.repository.AuthorRepository;
import com.ekabotdev.library.book.dto.BookResponse;
import com.ekabotdev.library.book.dto.CreateBookRequest;
import com.ekabotdev.library.book.dto.UpdateBookRequest;
import com.ekabotdev.library.book.entity.Book;
import com.ekabotdev.library.book.exception.BookHasCopiesException;
import com.ekabotdev.library.book.exception.BookNotFoundException;
import com.ekabotdev.library.book.repository.BookRepository;
import com.ekabotdev.library.book.specification.BookSpecification;
import com.ekabotdev.library.bookcopy.repository.BookCopyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final BookCopyRepository bookCopyRepository;

    @Transactional
    public BookResponse createBook(CreateBookRequest request) {

        Author author = authorRepository.findById(request.authorId()).orElseThrow(()
                -> new AuthorNotFoundException("Author with id " + request.authorId() + " not found"));

        Book book = new Book();
        book.setTitle(request.title());
        book.setIsbn(request.isbn());
        book.setDescription(request.description());
        book.setPublicationYear(request.publicationYear());
        book.setAuthor(author);

        Book savedBook = bookRepository.save(book);

        return toBookResponse(savedBook);


    }
    private BookResponse toBookResponse(Book book) {
       return new BookResponse(
               book.getId(),
               book.getTitle(),
               book.getIsbn(),
               book.getDescription(),
               book.getPublicationYear(),
               book.getAuthor().getId()
       );
    }

    @Transactional(readOnly = true)
    public BookResponse getBookById(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(()
                -> new BookNotFoundException("Book with id " + id + " not found"));
        return toBookResponse(book);
    }

    @Transactional(readOnly = true)
    public Page<BookResponse> getBooks(String keyword,Pageable pageable) {
        if (keyword != null && !keyword.isBlank()) {
            Specification<Book> specification = BookSpecification.search(keyword);
            return bookRepository.findAll(specification,pageable)
                    .map(this::toBookResponse);

        }
        return bookRepository.findAll(pageable)
                .map(this::toBookResponse);
    }

    @Transactional
    public BookResponse updateBook(Long id ,UpdateBookRequest request) {
        Book book = bookRepository.findById(id).orElseThrow(()
                -> new BookNotFoundException("Book with id " + id + " not found"));

        book.setTitle(request.title());
        book.setDescription(request.description());


        return toBookResponse(book);
    }
    @Transactional
    public void deleteBook(Long id) {
        if(!bookRepository.existsById(id)) {
            throw new BookNotFoundException("Book with id " + id + " not found");
        }
        if (bookCopyRepository.existsByBookId(id)) {
            throw new BookHasCopiesException
                    ("Book with id " + id + " cannot be deleted because they still have some copies");
        }
            bookRepository.deleteById(id);
    }
}
