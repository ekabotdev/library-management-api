package com.ekabotdev.library.author.repository;

import com.ekabotdev.library.author.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<Author,Long> {
    boolean existsById(Long id);
    boolean existsByName(String name);
}
