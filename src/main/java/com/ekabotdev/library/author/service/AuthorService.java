package com.ekabotdev.library.author.service;

import com.ekabotdev.library.author.dto.AuthorResponse;
import com.ekabotdev.library.author.dto.CreateAuthorRequest;
import com.ekabotdev.library.author.dto.UpdateAuthorRequest;
import com.ekabotdev.library.author.entity.Author;
import com.ekabotdev.library.author.exception.AuthorAlreadyExistsException;
import com.ekabotdev.library.author.exception.AuthorHasBooksException;
import com.ekabotdev.library.author.exception.AuthorNotFoundException;
import com.ekabotdev.library.author.repository.AuthorRepository;
import com.ekabotdev.library.book.repository.BookRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    @Transactional
    public AuthorResponse createAuthor(CreateAuthorRequest request) {
        if (authorRepository.existsByName(request.name().trim())) {
            throw new AuthorAlreadyExistsException
                    ("Author with name " +request.name() + " already exists");
        }

        Author author = new Author();
        author.setName(request.name());
        author.setBio(request.biography());

        Author savedAuthor = authorRepository.save(author);

        return toResponse(savedAuthor);
    }

    @Transactional(readOnly = true)
    public AuthorResponse getAuthorById(Long id) {

        Author author = authorRepository.findById(id).orElseThrow(()
                -> new AuthorNotFoundException("Author with id " + id + " not found"));

        return toResponse(author);
    }

    private AuthorResponse toResponse(Author author) {
        return new AuthorResponse(
                author.getId(),
                author.getName(),
                author.getBio()
        );
    }

    @Transactional(readOnly = true)
    public Page<AuthorResponse> getAuthors(Pageable pageable) {
         return authorRepository.findAll(pageable)
                 .map(this::toResponse);
    }

    @Transactional
    public AuthorResponse updateAuthor(Long id, UpdateAuthorRequest request) {
        Author author = authorRepository.findById(id).orElseThrow(()
                -> new AuthorNotFoundException("Author with id " + id + " not found"));

        author.setName(request.name());
        author.setBio(request.biography());

        return toResponse(author);
    }
    @Transactional
    public void deleteAuthor(Long id) {
        if (!authorRepository.existsById(id)) {
            throw new AuthorNotFoundException("Author with id " + id + " not found");
        }
        if (bookRepository.existsByAuthorId(id)) {
            throw new AuthorHasBooksException
                    ("Author with id " + id + " cannot be deleted because they still have some books");
        }
        authorRepository.deleteById(id);
    }
}
