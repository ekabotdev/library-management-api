package com.ekabotdev.library.unit.author;


import com.ekabotdev.library.author.dto.AuthorResponse;
import com.ekabotdev.library.author.dto.CreateAuthorRequest;
import com.ekabotdev.library.author.dto.UpdateAuthorRequest;
import com.ekabotdev.library.author.entity.Author;
import com.ekabotdev.library.author.exception.AuthorAlreadyExistsException;
import com.ekabotdev.library.author.exception.AuthorHasBooksException;
import com.ekabotdev.library.author.exception.AuthorNotFoundException;
import com.ekabotdev.library.author.repository.AuthorRepository;
import com.ekabotdev.library.author.service.AuthorService;
import com.ekabotdev.library.book.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
 class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private AuthorService authorService;

    @Test
    void createAuthor_shouldSaveAndReturnAuthorResponse() {

        CreateAuthorRequest request = new CreateAuthorRequest(
                "Robert C martin",
                "Joshua Bloch"
        );

        when(authorRepository.existsByName("Robert C martin")).thenReturn(false);

        when(authorRepository.save(any(Author.class)))
                .then(invocation -> invocation.getArgument(0));

        AuthorResponse response = authorService.createAuthor(request);

        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(new AuthorResponse(null,"Robert C martin",
                        "Joshua Bloch"
                ));

        verify(authorRepository).existsByName("Robert C martin");
        verify(authorRepository).save(any(Author.class));
        verifyNoInteractions(bookRepository);

    }

    @Test
    void createAuthor_shouldThrowAnExceptionWhenAuthorAlreadyExists() {

        CreateAuthorRequest request = new CreateAuthorRequest(
                "Robert C martin",
                "Joshua Bloch"
        );

        when(authorRepository.existsByName("Robert C martin")).thenReturn(true);

        assertThatThrownBy(()->authorService.createAuthor(request))
                .isInstanceOf(AuthorAlreadyExistsException.class);

        verify(authorRepository).existsByName("Robert C martin");
        verify(authorRepository, never()).save(any(Author.class));
        verifyNoInteractions(bookRepository);
    }

    @Test
    void getAuthorById_shouldReturnAuthorResponse() {

        Author author = new Author();
        author.setId(1L);
        author.setName("Robert C martin");
        author.setBio("Joshua Bloch");

        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));

        AuthorResponse response = authorService.getAuthorById(1L);

        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(new AuthorResponse(1L, "Robert C martin",
                        "Joshua Bloch"));

        verify(authorRepository).findById(1L);
        verifyNoInteractions(bookRepository);
    }

    @Test
    void getAuthorById_shouldThrowAnExceptionWhenAuthorNotFound() {

        when(authorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(()->authorService.getAuthorById(1L))
        .isInstanceOf(AuthorNotFoundException.class);

        verify(authorRepository).findById(1L);
        verifyNoInteractions(bookRepository);
    }

    @Test
    void updateAuthor_shouldReturnUpdatedAuthorResponse() {


        Author author = new Author();
        author.setId(1L);
        author.setName("Robert C martin");
        author.setBio("Joshua Bloch");
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));

        UpdateAuthorRequest request = new UpdateAuthorRequest(
                "Avatar","Mavel"
        );
        AuthorResponse response = authorService.updateAuthor(1L,request);
        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(new AuthorResponse(1L,"Avatar","Mavel"));
        assertThat(author.getName()).isEqualTo("Avatar");
        assertThat(author.getBio()).isEqualTo("Mavel");

        verify(authorRepository).findById(1L);
        verifyNoInteractions(bookRepository);

    }

    @Test
    void updateAuthor_shouldThrowAnExceptionWhenAuthorNotFound() {

        when(authorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authorService.updateAuthor
                (1L,new UpdateAuthorRequest("Robert C martin","Mavel")))
                .isInstanceOf(AuthorNotFoundException.class);

        verify(authorRepository).findById(1L);
        verifyNoInteractions(bookRepository);


    }

    @Test
    void deleteAuthor_shouldThrowAnExceptionWhenAuthorNotFound() {

        when(authorRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(()->authorService.deleteAuthor(1L))
                .isInstanceOf(AuthorNotFoundException.class);

        verify(authorRepository).existsById(1L);
        verifyNoInteractions(bookRepository);
        verify(authorRepository, never()).deleteById(1L);
    }

    @Test
    void deleteAuthor_shouldThrowAnExceptionWhenAuthorHasBooks() {
        when(authorRepository.existsById(1L)).thenReturn(true);

        when(bookRepository.existsByAuthorId(1L)).thenReturn(true);

        assertThatThrownBy(()->authorService.deleteAuthor(1L))
                .isInstanceOf(AuthorHasBooksException.class);

        verify(authorRepository).existsById(1L);
        verify(bookRepository).existsByAuthorId(1L);
        verify(authorRepository, never()).deleteById(1L);
        verifyNoMoreInteractions(authorRepository, bookRepository);
    }

    @Test
    void deleteAuthor_shouldTestThatAuthorExistsButHasNoBooks() {

        when(authorRepository.existsById(1L)).thenReturn(true);
        when(bookRepository.existsByAuthorId(1L)).thenReturn(false);

        authorService.deleteAuthor(1L);

        verify(authorRepository).existsById(1L);
        verify(bookRepository).existsByAuthorId(1L);
        verify(authorRepository).deleteById(1L);
    }
}

