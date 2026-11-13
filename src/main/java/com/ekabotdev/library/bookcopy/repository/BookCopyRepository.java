package com.ekabotdev.library.bookcopy.repository;

import com.ekabotdev.library.bookcopy.entity.BookCopy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookCopyRepository extends JpaRepository<BookCopy, Long> {
    boolean existsByBookId(Long bookId);
}
