package com.ekabotdev.library.loan.specification;

import com.ekabotdev.library.enums.LoanStatus;
import com.ekabotdev.library.loan.entity.Loan;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class LoanSpecification {


    public static Specification<Loan> hasStatus (LoanStatus status, LocalDateTime now) {
        switch (status) {
            case RETURNED -> {
                return (root, query, criteriaBuilder) ->
                        criteriaBuilder.isNotNull(root.get("returnedAt")
                        );
            }
            case OVERDUE -> {
                return  (root, query, criteriaBuilder) ->
                        criteriaBuilder.and(criteriaBuilder.isNull(root.get("returnedAt")),
                                criteriaBuilder.lessThan(root.get("dueDate"), now));
            }
            case ACTIVE -> {
                return  (root, query, criteriaBuilder) ->
                        criteriaBuilder.and(criteriaBuilder.isNull(root.get("returnedAt")),
                                criteriaBuilder.greaterThanOrEqualTo(root.get("dueDate"), now));
            }
        }
        return null;
    }
}
