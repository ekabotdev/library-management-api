package com.ekabotdev.library.book.specification;

import com.ekabotdev.library.book.entity.Book;
import org.springframework.data.jpa.domain.Specification;

public class BookSpecification {

    public static Specification<Book> hasTitle(String keyword) {
        return (root, query, criteriaBuilder) ->
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("title")),
                                "%" + keyword.toLowerCase().trim() + "%"
                        );
    }

    public static Specification<Book> hasIsbn(String keyword) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("isbn")),
                        "%" +  keyword.toLowerCase().trim() + "%");
    }
    public static Specification<Book> hasAuthorName(String keyword) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(
                        criteriaBuilder.lower(root.join("author").get("name"))
                        , "%" + keyword.toLowerCase().trim() + "%"
                );
    }

    public static Specification<Book> search(String keyword) {
     return hasTitle(keyword).or(hasIsbn(keyword).or(hasAuthorName(keyword)));
   }
}
