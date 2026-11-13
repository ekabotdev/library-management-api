package com.ekabotdev.library.unit.bookcopy;

import com.ekabotdev.library.author.entity.Author;
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
import com.ekabotdev.library.bookcopy.service.BookCopyService;
import com.ekabotdev.library.enums.CopyCondition;
import com.ekabotdev.library.enums.CopyStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookCopyServiceTest {

    @Mock
     private BookCopyRepository bookCopyRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookCopyService bookCopyService;



    @Test
    void createBookCopy_shouldSaveAndReturnBookCopyResponse() {
        CreateBookCopyRequest request = new CreateBookCopyRequest(
                1L
        );

        Author author = new Author();
        author.setId(10L);
        author.setName("Robert C Martin");

        Book book = new Book();
        book.setId(1L);
        book.setTitle("Clean Code");
        book.setDescription("The ultimate clean code guide");
        book.setIsbn("123456789");
        book.setPublicationYear(2008);
        book.setAuthor(author);


        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookCopyRepository.save(any(BookCopy.class)))
                .thenAnswer(invocation -> { BookCopy bookCopy = invocation.getArgument(0);
                        bookCopy.setId(1L);
                    return bookCopy;
                });

        BookCopyResponse response = bookCopyService.createBookCopy(request);

        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(new BookCopyResponse(
                        1L,
                        1L,
                        CopyStatus.AVAILABLE,
                        CopyCondition.GOOD
                ));

        verify(bookRepository).findById(1L);
        verify(bookCopyRepository).save(any(BookCopy.class));
    }

    @Test
    void createBookCopy_shouldThrowAnExceptionWhenBookNotFound() {

        CreateBookCopyRequest request = new CreateBookCopyRequest(
                1L
        );
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookCopyService.createBookCopy(request))
                .isInstanceOf(BookNotFoundException.class);

        verify(bookRepository).findById(1L);
        verify(bookCopyRepository, never()).save(any(BookCopy.class));
    }

    @Test
    void getBookCopyById_shouldThrowAnExceptionWhenBookCopyNotFound() {
        when(bookCopyRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookCopyService.getBookCopyById(1L))
                .isInstanceOf(BookCopyNotFoundException.class);

        verify(bookCopyRepository).findById(1L);
        verifyNoInteractions(bookRepository);
    }

    @Test
    void getBookCopyById_shouldReturnBookCopyResponse() {

        Book book = new Book();
        book.setId(1L);


        BookCopy bookCopy = new BookCopy();
        bookCopy.setId(1L);
        bookCopy.setBook(book);
        bookCopy.setStatus(CopyStatus.AVAILABLE);
        bookCopy.setConditionStatus(CopyCondition.GOOD);

        when(bookCopyRepository.findById(1L)).thenReturn(Optional.of(bookCopy));

        BookCopyResponse response = bookCopyService.getBookCopyById(1L);

        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(new BookCopyResponse(
                        1L,
                        1L,
                        CopyStatus.AVAILABLE,
                        CopyCondition.GOOD

                ));

        verify(bookCopyRepository).findById(1L);
        verifyNoInteractions(bookRepository);
    }

    @Test
    void getAllBookCopies_shouldReturnAllBookCopies() {
        Pageable pageable = PageRequest.of(0, 10);

        Book book = new Book();
        book.setId(1L);

        BookCopy bookCopy = new BookCopy();
        bookCopy.setId(1L);
        bookCopy.setBook(book);
        bookCopy.setStatus(CopyStatus.AVAILABLE);
        bookCopy.setConditionStatus(CopyCondition.GOOD);

        Page<BookCopy> bookCopies = new PageImpl<>(List.of(bookCopy));

        when(bookCopyRepository.findAll(pageable)).thenReturn(bookCopies);

        Page<BookCopyResponse> response = bookCopyService.getAllBookCopies(pageable);

        assertThat(response.getContent())
                .usingRecursiveComparison()
                .isEqualTo(List.of(new BookCopyResponse(
                        1L,
                        1L,
                        CopyStatus.AVAILABLE,
                        CopyCondition.GOOD
                )));

        verify(bookCopyRepository).findAll(pageable);
        verifyNoInteractions(bookRepository);
        assertThat(response.getTotalElements()).isEqualTo(1);
        assertThat(response.getNumber()).isEqualTo(0);
        assertThat(response.getSize()).isEqualTo(1);
    }

    @Test
    void updateBookCopy_shouldThrowAnExceptionWhenBookCopyNotFound() {

        UpdateBookCopyConditionRequest request = new UpdateBookCopyConditionRequest(
                CopyCondition.GOOD
        );

        when(bookCopyRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookCopyService.updateBookCopy(1L,request))
                .isInstanceOf(BookCopyNotFoundException.class);

        verify(bookCopyRepository).findById(1L);
        verifyNoInteractions(bookRepository);

    }
    @Test
    void updateBookCopy_shouldReturnUpdatedBookResponse() {
        UpdateBookCopyConditionRequest request = new UpdateBookCopyConditionRequest(
                CopyCondition.DAMAGED
        );

        Book book = new Book();
        book.setId(1L);

        BookCopy bookCopy = new BookCopy();
        bookCopy.setId(1L);
        bookCopy.setBook(book);
        bookCopy.setStatus(CopyStatus.AVAILABLE);
        bookCopy.setConditionStatus(CopyCondition.GOOD);


        when(bookCopyRepository.findById(1L)).thenReturn(Optional.of(bookCopy));

        BookCopyResponse response = bookCopyService.updateBookCopy(1L,request);

        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(new BookCopyResponse(
                        1L,
                        1L,
                        CopyStatus.AVAILABLE,
                        CopyCondition.DAMAGED
                ));
        assertThat(bookCopy.getConditionStatus())
                .isEqualTo(CopyCondition.DAMAGED);

        verify(bookCopyRepository).findById(1L);
        verifyNoInteractions(bookRepository);
    }

    @Test
    void deleteBookCopy_shouldThrowAnExceptionWhenBookCopyNotFound() {
        when(bookCopyRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookCopyService.deleteBookCopy(1L))
                .isInstanceOf(BookCopyNotFoundException.class);

        verify(bookCopyRepository).findById(1L);
        verify(bookCopyRepository, never()).deleteById(1L);
    }

    @Test
    void deleteBookCopy_shouldThrowAnExceptionWhenBookCopyAlreadyBorrowed() {
        BookCopy bookCopy = new BookCopy();
        bookCopy.setId(1L);
        bookCopy.setStatus(CopyStatus.BORROWED);

        when(bookCopyRepository.findById(1L)).thenReturn(Optional.of(bookCopy));

            assertThatThrownBy(() -> bookCopyService.deleteBookCopy(1L))
                    .isInstanceOf(BookCopyBorrowedException.class);


        verify(bookCopyRepository).findById(1L);
        verify(bookCopyRepository, never()).deleteById(1L);

    }
    @Test
    void deleteBookCopy_shouldThrowAnExceptionWhenBookCopyNotInGoodCondition() {
        BookCopy bookCopy = new BookCopy();
        bookCopy.setId(1L);
        bookCopy.setStatus(CopyStatus.AVAILABLE);
        bookCopy.setConditionStatus(CopyCondition.LOST);

        when(bookCopyRepository.findById(1L)).thenReturn(Optional.of(bookCopy));

            assertThatThrownBy(() -> bookCopyService.deleteBookCopy(1L))
                    .isInstanceOf(BookCopyNotInGoodCondition.class);


        verify(bookCopyRepository).findById(1L);
        verify(bookCopyRepository, never()).deleteById(1L);
    }

    @Test
    void deleteBookCopy_shouldDeleteBookCopy() {
        BookCopy bookCopy = new BookCopy();
        bookCopy.setId(1L);
        bookCopy.setStatus(CopyStatus.AVAILABLE);
        bookCopy.setConditionStatus(CopyCondition.GOOD);

        when(bookCopyRepository.findById(1L)).thenReturn(Optional.of(bookCopy));
        bookCopyService.deleteBookCopy(1L);

        verify(bookCopyRepository).findById(1L);
        verify(bookCopyRepository).deleteById(1L);

    }
}
