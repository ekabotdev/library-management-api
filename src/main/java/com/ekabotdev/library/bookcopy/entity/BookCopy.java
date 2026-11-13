package com.ekabotdev.library.bookcopy.entity;

import com.ekabotdev.library.book.entity.Book;
import com.ekabotdev.library.common.BaseEntity;
import com.ekabotdev.library.enums.CopyCondition;
import com.ekabotdev.library.enums.CopyStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "book_copies")
@Getter
@Setter
public class BookCopy extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Enumerated(EnumType.STRING)
    @Column(name = "copy_status", nullable = false, length = 20)
    private CopyStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "condition_status", nullable = false, length = 20)
    private CopyCondition conditionStatus;
}
