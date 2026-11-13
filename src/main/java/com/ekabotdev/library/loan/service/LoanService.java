package com.ekabotdev.library.loan.service;


import com.ekabotdev.library.bookcopy.entity.BookCopy;
import com.ekabotdev.library.bookcopy.exception.BookCopyNotFoundException;
import com.ekabotdev.library.bookcopy.repository.BookCopyRepository;
import com.ekabotdev.library.enums.CopyCondition;
import com.ekabotdev.library.enums.CopyStatus;
import com.ekabotdev.library.enums.LoanStatus;
import com.ekabotdev.library.enums.MembershipStatus;
import com.ekabotdev.library.loan.dto.CreateLoanRequest;
import com.ekabotdev.library.loan.dto.LoanResponse;
import com.ekabotdev.library.loan.entity.Loan;
import com.ekabotdev.library.loan.exception.*;
import com.ekabotdev.library.loan.repository.LoanRepository;
import com.ekabotdev.library.loan.specification.LoanSpecification;
import com.ekabotdev.library.member.entity.Member;
import com.ekabotdev.library.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;
    private final CurrentUserService currentUserService;
    private final BookCopyRepository bookCopyRepository;



    private LoanStatus calculateStatus(Loan loan) {
        if (loan.getReturnedAt() != null) {
            return LoanStatus.RETURNED;
        }

        if (!LocalDateTime.now().isAfter(loan.getDueDate())) {
            return LoanStatus.ACTIVE;
        }
        return LoanStatus.OVERDUE;
    }

    @Transactional
    public LoanResponse createLoan(CreateLoanRequest request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Member member = currentUserService.getCurrentMember(authentication);

        if (member.getMembershipStatus() != MembershipStatus.ACTIVE) {
            throw new MemberNotActiveException(
                    "Only active members can borrow books"
            );
        }
        BookCopy bookCopy = bookCopyRepository.findById(request.bookCopyId()).orElseThrow(() -> new
                BookCopyNotFoundException("Book copy with id " + request.bookCopyId() + " not found"));

        if (bookCopy.getStatus() != CopyStatus.AVAILABLE
                || bookCopy.getConditionStatus() != CopyCondition.GOOD) {

            throw new InvalidBookCopyStateException(
                    String.format("Book copy with ID %s cannot be processed. Current Status: %s, Condition: %s. " +
                                    "An eligible book must be AVAILABLE and in GOOD condition.",
                            request.bookCopyId(), bookCopy.getStatus(), bookCopy.getConditionStatus())
            );
        }

        Loan loan = new Loan();
        loan.setBookCopy(bookCopy);
        loan.setMember(member);
        LocalDateTime borrowedAt = LocalDateTime.now();
        loan.setBorrowedAt(borrowedAt);
        LocalDateTime dueDate = borrowedAt.plusDays(14);
        loan.setDueDate(dueDate);




        Loan savedLoan = loanRepository.save(loan);
        bookCopy.setStatus(CopyStatus.BORROWED);

        return toLoanResponse(savedLoan);
    }

    private LoanResponse toLoanResponse(Loan loan) {
        return  new LoanResponse(
        loan.getId(),
        loan.getMember().getId(),
        loan.getBookCopy().getId(),
        loan.getBorrowedAt(),
        loan.getDueDate(),
        loan.getReturnedAt(),
        calculateStatus(loan)
         );
    }

    @Transactional
    public LoanResponse returnLoan(Long id) {

        Loan loan = loanRepository.findById(id).orElseThrow(()
                -> new LoanNotFoundException("Loan with id " + id + " not found"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Member member = currentUserService.getCurrentMember(authentication);

        if (!member.getId().equals(loan.getMember().getId())) {
            throw new LoanOwnershipException("You don't own the loan");
        }

        if(loan.getReturnedAt() != null) {
            throw new LoanAlreadyReturnedException
                    ("Loan with id " + id + " is already returned");
        }

            loan.setReturnedAt(LocalDateTime.now());

            BookCopy copy = loan.getBookCopy();
            if(copy.getConditionStatus() == CopyCondition.GOOD) {
                copy.setStatus(CopyStatus.AVAILABLE);
            }else {
                copy.setStatus(CopyStatus.MAINTENANCE);
            }

            Loan returnedLoan = loanRepository.save(loan);
            return toLoanResponse(returnedLoan);
    }

    @Transactional(readOnly = true)
    public Page<LoanResponse> myLoans(Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Member member = currentUserService.getCurrentMember(authentication);
        return loanRepository
                .findAllByMemberId(member.getId(), pageable)
                .map(this::toLoanResponse);
    }
    @Transactional(readOnly = true)
    public Page<LoanResponse> getAllLoans(LoanStatus status,Pageable pageable) {
        if (status != null) {
           Specification<Loan> specification = LoanSpecification.hasStatus
                   (status,LocalDateTime.now());
            return loanRepository.findAll(specification, pageable)
                   .map(this::toLoanResponse);
        }
        return loanRepository
                .findAll(pageable)
                .map(this::toLoanResponse);
    }
}
