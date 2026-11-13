package com.ekabotdev.library.loan.repository;

import com.ekabotdev.library.loan.entity.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long>,
        JpaSpecificationExecutor<Loan> {
    Long id(Long id);
    Page<Loan> findAllByMemberId(Long memberId, Pageable pageable);
}
