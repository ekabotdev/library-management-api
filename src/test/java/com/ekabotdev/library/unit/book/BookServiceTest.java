package com.ekabotdev.library.unit.book;


import com.ekabotdev.library.author.entity.Author;
import com.ekabotdev.library.author.exception.AuthorNotFoundException;
import com.ekabotdev.library.author.repository.AuthorRepository;
import com.ekabotdev.library.author.service.AuthorService;
import com.ekabotdev.library.book.dto.BookResponse;
import com.ekabotdev.library.book.dto.CreateBookRequest;
import com.ekabotdev.library.book.dto.UpdateBookRequest;
import com.ekabotdev.library.book.entity.Book;
import com.ekabotdev.library.book.exception.BookHasCopiesException;
import com.ekabotdev.library.book.exception.BookNotFoundException;
import com.ekabotdev.library.book.repository.BookRepository;
import com.ekabotdev.library.book.service.BookService;
import com.ekabotdev.library.bookcopy.repository.BookCopyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
 class BookServiceTest {
    @Mock
    private BookRepository bookRepository;
    @Mock
    private AuthorRepository authorRepository;
    @Mock
    private BookCopyRepository bookCopyRepository;
    @InjectMocks
    private BookService bookService;
    @InjectMocks
    private AuthorService authorService;


    @Test
    void createBook_shouldSaveAndReturnBookResponse() {
        Author author = new Author();
        author.setId(10L);
        author.setName("Robert C Martin");

        CreateBookRequest request = new CreateBookRequest(
                "Clean Code",
                "1202200330",
                "A handbook",
                2008,
                10L
        );

        when(authorRepository.findById(10L)).thenReturn(Optional.of(author));
        when(bookRepository.save(any(Book.class)))
                .thenAnswer(invocation -> { Book book = invocation.getArgument(0);
                book.setId(1L);
                return book;
                });

        BookResponse response = bookService.createBook(request);

        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(new BookResponse(
                        1L,
                        "Clean Code",
                        "1202200330",
                        "A handbook",
                        2008,
                        10L

                ));

        verify(authorRepository).findById(10L);
        verify(bookRepository).save(any(Book.class));
        verifyNoInteractions(bookCopyRepository);

    }

    @Test
    void createBook_shouldThrowAnExceptionWhenAuthorNotFound() {

                CreateBookRequest request = new CreateBookRequest(
                "Clean Code",
                "1202200330",
                "A handbook",
                2008,
                1L
        );
        when(authorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.createBook(request))
                .isInstanceOf(AuthorNotFoundException.class);

        verify(authorRepository).findById(1L);
        verify(bookRepository, never()).save(any(Book.class));
        verifyNoInteractions(bookCopyRepository);
    }

    @Test
    void getBookById_shouldReturnBookResponse() {
        Author author = new Author();
        author.setId(10L);
        author.setName("Robert C Martin");

        Book book = new Book();
        book.setId(1L);
        book.setTitle("Clean Code");
        book.setAuthor(author);
        book.setIsbn("1202200330");
        book.setPublicationYear(2008);
        book.setDescription("A handbook");

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        BookResponse response = bookService.getBookById(1L);
        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(new BookResponse(
                        1L,
                        "Clean Code",
                        "1202200330",
                        "A handbook",
                        2008,
                        10L
                ));

        verify(bookRepository).findById(1L);
        verifyNoInteractions(authorRepository, bookCopyRepository);
    }

    @Test
    void getBookById_shouldThrowAnExceptionWhenBookNotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.getBookById(1L))
                .isInstanceOf(BookNotFoundException.class);

        verify(bookRepository).findById(1L);
        verifyNoInteractions(bookCopyRepository);
    }
    @Test
    void getBooks_shouldRetrieveAllBooks() {

        Pageable pageable = PageRequest.of(0, 10);
        Author author = new Author();
        author.setId(10L);
        author.setName("Robert C Martin");

        Book book = new Book();
        book.setId(1L);
        book.setTitle("Clean Code");
        book.setAuthor(author);
        book.setIsbn("1202200330");
        book.setPublicationYear(2008);
        book.setDescription("A handbook");

        Page<Book> books = new PageImpl<>(List.of(book));

        when(bookRepository.findAll(pageable)).thenReturn(books);

        Page<BookResponse> response = bookService.getBooks("",pageable);

        assertThat(response.getContent())
                .usingRecursiveComparison()
                .isEqualTo(List.of(new BookResponse(
                        1L,
                        "Clean Code",
                        "1202200330",
                        "A handbook",
                        2008,
                        10L
                )));

        verify(bookRepository).findAll(pageable);
        verifyNoInteractions(authorRepository, bookCopyRepository);


    }

    @Test
    void getBooks_shouldSearchBooksWhenKeywordIsProvided() {
        Pageable pageable = PageRequest.of(0, 10);
        Author author = new Author();
        author.setId(10L);
        author.setName("Robert C Martin");

        Book book = new Book();
        book.setId(1L);
        book.setTitle("Clean Code");
        book.setAuthor(author);
        book.setIsbn("1202200330");
        book.setPublicationYear(2008);
        book.setDescription("A handbook");

        Page<Book> books = new PageImpl<>(List.of(book));

        when(bookRepository.findAll(ArgumentMatchers.<Specification<Book>>any(),eq(pageable))).thenReturn(books);

        Page<BookResponse> response = bookService.getBooks("Clean Code",pageable);
        assertThat(response.getContent())
                .usingRecursiveComparison()
                .isEqualTo(List.of(new BookResponse(
                        1L,
                        "Clean Code",
                        "1202200330",
                        "A handbook",
                        2008,
                        10L
                )));

        verify(bookRepository).findAll(ArgumentMatchers.<Specification<Book>>any(),eq(pageable));
        verify(bookRepository,never()).findAll(pageable);
        verifyNoInteractions(authorRepository, bookCopyRepository);

    }

    @Test
    void updateBook_shouldReturnUpdatedBookResponse() {

        UpdateBookRequest request = new UpdateBookRequest(
                "Predator Badlands",
                "Prey to none"
        );
        Author author = new Author();
        author.setId(10L);
        author.setName("Robert C Martin");


        Book book = new Book();
        book.setId(1L);
        book.setTitle("Clean Code");
        book.setAuthor(author);
        book.setIsbn("1202200330");
        book.setPublicationYear(2008);
        book.setDescription("A handbook");

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        BookResponse response = bookService.updateBook(1L,request);

        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(new BookResponse(
                        1L,
                        "Predator Badlands",
                        "1202200330",
                        "Prey to none",
                        2008,
                        10L
                ));

        verify(bookRepository).findById(1L);
        assertThat(book.getTitle()).isEqualTo("Predator Badlands");
        assertThat(book.getDescription()).isEqualTo("Prey to none");
        verifyNoInteractions(authorRepository, bookCopyRepository);

    }

    @Test
    void updateBook_shouldThrowAnExceptionWhenBookNotFound() {

        UpdateBookRequest request = new UpdateBookRequest(
                "Predator Badlands",
                "Prey to none"
        );
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());


        assertThatThrownBy(()->bookService.updateBook(1L,request))
                .isInstanceOf(BookNotFoundException.class);

        verify(bookRepository).findById(1L);
        verifyNoInteractions(authorRepository, bookCopyRepository);
    }

    @Test
    void deleteBook_shouldThrowAnExceptionWhenBookNotFound() {

        when(bookRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> bookService.deleteBook(1L))
                .isInstanceOf(BookNotFoundException.class);

        verify(bookRepository).existsById(1L);
        verify(bookRepository,never()).deleteById(1L);
        verifyNoInteractions(authorRepository, bookCopyRepository);

    }

    @Test
    void deleteBook_shouldThrowAnExceptionWhenBookHasCopies() {

        when(bookRepository.existsById(1L)).thenReturn(true);
        when(bookCopyRepository.existsByBookId(1L)).thenReturn(true);

        assertThatThrownBy(() -> bookService.deleteBook(1L))
                .isInstanceOf(BookHasCopiesException.class);

        verify(bookRepository).existsById(1L);
        verify(bookCopyRepository).existsByBookId(1L);
        verifyNoInteractions(authorRepository);
    }
    @Test
    void deleteBook_shouldDeleteBookWhenBookHasNoCopies() {
        when(bookRepository.existsById(1L)).thenReturn(true);
        when(bookCopyRepository.existsByBookId(1L)).thenReturn(false);

        bookService.deleteBook(1L);

        verify(bookRepository).existsById(1L);
        verify(bookCopyRepository).existsByBookId(1L);
        verify(bookRepository).deleteById(1L);
        verifyNoInteractions(authorRepository);
    }
}
