package com.ekabotdev.library.unit.loan;


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
import com.ekabotdev.library.loan.service.LoanService;
import com.ekabotdev.library.member.entity.Member;
import com.ekabotdev.library.security.CurrentUserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static com.ekabotdev.library.enums.LoanStatus.ACTIVE;
import static com.ekabotdev.library.enums.LoanStatus.RETURNED;
import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoanServiceTest {

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Mock
    private BookCopyRepository bookCopyRepository;

    @Mock
    private LoanRepository loanRepository;


    @InjectMocks
    private LoanService loanService;

    @Mock
    private CurrentUserService currentUserService;


    @Test
    void createLoan_shouldThrowAnExceptionWhenMemberNotActive() {

        Member member = new Member();
        member.setId(1L);
        member.setMembershipStatus(MembershipStatus.SUSPENDED);

        CreateLoanRequest request = new CreateLoanRequest(1L);

        Authentication authentication =
                new UsernamePasswordAuthenticationToken("member@example.com", null);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(currentUserService.getCurrentMember(authentication))
                .thenReturn(member);

        assertThatThrownBy(() -> loanService.createLoan(request))
                .isInstanceOf(MemberNotActiveException.class);

        verify(currentUserService).getCurrentMember(authentication);
        verifyNoInteractions(bookCopyRepository, loanRepository);
    }

    @Test
    void createLoan_shouldThrowAnExceptionWhenBookCopyNotFound() {

        Member member = new Member();
        member.setId(1L);
        member.setMembershipStatus(MembershipStatus.ACTIVE);

        CreateLoanRequest request = new CreateLoanRequest(1L);

        Authentication authentication = new UsernamePasswordAuthenticationToken("member@example.com", null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(currentUserService.getCurrentMember(authentication)).thenReturn(member);

        when(bookCopyRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loanService.createLoan(request))
                .isInstanceOf(BookCopyNotFoundException.class);

        verify(currentUserService).getCurrentMember(authentication);
        verify(bookCopyRepository).findById(1L);
        verifyNoInteractions(loanRepository);
    }

    @Test
    void createLoan_shouldThrowAnExceptionWhenBookCopyStatusIsNotAvailableOrConditionIsNotGood() {

        Member member = new Member();
        member.setId(1L);
        member.setMembershipStatus(MembershipStatus.ACTIVE);

        CreateLoanRequest request = new CreateLoanRequest(1L);

        Authentication authentication = new UsernamePasswordAuthenticationToken("member@example.com", null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(currentUserService.getCurrentMember(authentication)).thenReturn(member);

        BookCopy bookCopy = new BookCopy();
        bookCopy.setId(1L);
        bookCopy.setStatus(CopyStatus.BORROWED);
        bookCopy.setConditionStatus(CopyCondition.DAMAGED);

        when(bookCopyRepository.findById(1L)).thenReturn(Optional.of(bookCopy));

        assertThatThrownBy(() -> loanService.createLoan(request))
                .isInstanceOf(InvalidBookCopyStateException.class);

        verify(currentUserService).getCurrentMember(authentication);
        verify(bookCopyRepository).findById(1L);
        verifyNoInteractions(loanRepository);

    }

    @Test
    void createLoan_shouldSaveAndReturnLoanResponse() {

        Member member = new Member();
        member.setId(1L);
        member.setMembershipStatus(MembershipStatus.ACTIVE);

        CreateLoanRequest request = new CreateLoanRequest(1L);

        Authentication authentication = new UsernamePasswordAuthenticationToken("member@example.com", null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(currentUserService.getCurrentMember(authentication)).thenReturn(member);

        BookCopy bookCopy = new BookCopy();
        bookCopy.setId(1L);
        bookCopy.setStatus(CopyStatus.AVAILABLE);
        bookCopy.setConditionStatus(CopyCondition.GOOD);

        when(bookCopyRepository.findById(1L)).thenReturn(Optional.of(bookCopy));
        when(loanRepository.save(any(Loan.class)))
                .thenAnswer(invocation -> {
                    Loan loan = invocation.getArgument(0);
                    loan.setId(1L);
                    return loan;
                });

        LoanResponse response = loanService.createLoan(request);

        assertThat(response.borrowedAt()).isNotNull();
        assertThat(response.dueDate()).isEqualTo(response.borrowedAt().plusDays(14));
        assertThat(bookCopy.getStatus()).isEqualTo(CopyStatus.BORROWED);

        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(new LoanResponse(
                        1L,
                        1L,
                        1L,
                        response.borrowedAt(),
                        response.borrowedAt().plusDays(14),
                        null,
                        ACTIVE
                ));

        verify(currentUserService).getCurrentMember(authentication);
        verify(bookCopyRepository).findById(1L);
        verify(loanRepository).save(any(Loan.class));
    }

    @Test
    void returnLoan_shouldThrowAnExceptionWhenLoanNotFound() {

        when(loanRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loanService.returnLoan(1L))
                .isInstanceOf(LoanNotFoundException.class);

        verify(loanRepository).findById(1L);
        verifyNoInteractions(bookCopyRepository);
        verify(loanRepository, never()).save(any(Loan.class));
    }

    @Test
    void returnLoan_shouldThrowAnExceptionWhenMemberDoesNotOwnLoan() {

        Member member = new Member();
        member.setId(1L);
        member.setMembershipStatus(MembershipStatus.ACTIVE);

        Member member2 = new Member();
        member2.setId(2L);

        Loan loan = new Loan();
        loan.setId(1L);
        loan.setMember(member2);

        Authentication authentication = new UsernamePasswordAuthenticationToken("member@example.com", null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

        when(currentUserService.getCurrentMember(authentication)).thenReturn(member);
        assertThatThrownBy(() -> loanService.returnLoan(1L))
                .isInstanceOf(LoanOwnershipException.class);

        verify(currentUserService).getCurrentMember(authentication);
        verify(loanRepository).findById(1L);
        verifyNoInteractions(bookCopyRepository);
        verify(loanRepository, never()).save(any(Loan.class));
    }

    @Test
    void returnLoan_shouldThrowAnExceptionWhenLoanAlreadyReturned() {

        Member member2 = new Member();
        member2.setId(1L);


        Loan loan = new Loan();
        loan.setId(1L);
        loan.setMember(member2);
        LocalDateTime originalReturnedAt = LocalDateTime.now();
        loan.setReturnedAt(originalReturnedAt);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

        Member member = new Member();
        member.setId(1L);
        member.setMembershipStatus(MembershipStatus.ACTIVE);

        Authentication authentication = new UsernamePasswordAuthenticationToken("member@example.com", null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(currentUserService.getCurrentMember(authentication)).thenReturn(member);

        assertThat(loan.getReturnedAt()).isEqualTo(originalReturnedAt);
        assertThatThrownBy(() -> loanService.returnLoan(1L))
                .isInstanceOf(LoanAlreadyReturnedException.class);

        verify(currentUserService).getCurrentMember(authentication);
        verify(loanRepository).findById(1L);
        verify(loanRepository, never()).save(any(Loan.class));
        verifyNoInteractions(bookCopyRepository);
    }

    @Test
    void returnLoan_shouldSaveAndReturnLoanResponse() {


        Member member = new Member();
        member.setId(1L);
        member.setMembershipStatus(MembershipStatus.ACTIVE);

        BookCopy bookCopy = new BookCopy();
        bookCopy.setId(1L);
        bookCopy.setConditionStatus(CopyCondition.GOOD);

        Loan loan = new Loan();
        loan.setId(1L);
        loan.setBookCopy(bookCopy);
        loan.setMember(member);
        loan.setReturnedAt(null);
        LocalDateTime borrowedAt = LocalDateTime.now();
        loan.setBorrowedAt(borrowedAt);
        loan.setDueDate(borrowedAt.plusDays(14));

        Authentication authentication = new UsernamePasswordAuthenticationToken("member@example.com", null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(currentUserService.getCurrentMember(authentication)).thenReturn(member);
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);


        LocalDateTime returnedAt = LocalDateTime.now();

        LoanResponse response = loanService.returnLoan(1L);
        assertThat(response.returnedAt())
                .isCloseTo(returnedAt, within(1, ChronoUnit.SECONDS));

        assertThat(response.borrowedAt()).isEqualTo(loan.getBorrowedAt());
        assertThat(response.dueDate()).isEqualTo(loan.getDueDate());
        assertThat(bookCopy.getStatus()).isEqualTo(CopyStatus.AVAILABLE);
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.memberId()).isEqualTo(1L);
        assertThat(response.bookCopyId()).isEqualTo(1L);
        assertThat(response.status()).isEqualTo(LoanStatus.RETURNED);

        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(new LoanResponse(
                        1L,
                        1L,
                        1L,
                        response.borrowedAt(),
                        response.dueDate(),
                        response.returnedAt(),
                        LoanStatus.RETURNED
                ));

        verify(currentUserService).getCurrentMember(authentication);
        verify(loanRepository).findById(1L);
        verify(loanRepository).save(any(Loan.class));

    }

    @Test
    void returnLoan_shouldSetBookCopyToMaintenanceWhenConditionIsDamaged() {

        Member member = new Member();
        member.setId(1L);
        member.setMembershipStatus(MembershipStatus.ACTIVE);

        BookCopy copy = new BookCopy();
        copy.setId(1L);
        copy.setStatus(CopyStatus.BORROWED);
        copy.setConditionStatus(CopyCondition.DAMAGED);

        Loan loan = new Loan();
        loan.setId(1L);
        loan.setBookCopy(copy);
        loan.setMember(member);
        loan.setReturnedAt(null);
        LocalDateTime borrowedAt = LocalDateTime.now();
        loan.setBorrowedAt(borrowedAt);
        loan.setDueDate(borrowedAt.plusDays(14));

        Authentication authentication = new UsernamePasswordAuthenticationToken("member@example.com", null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(currentUserService.getCurrentMember(authentication)).thenReturn(member);
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);


        LocalDateTime returnedAt = LocalDateTime.now();

        LoanResponse response = loanService.returnLoan(1L);
        assertThat(response.returnedAt())
                .isCloseTo(returnedAt, within(1, ChronoUnit.SECONDS));

        assertThat(response.borrowedAt()).isEqualTo(loan.getBorrowedAt());
        assertThat(response.dueDate()).isEqualTo(loan.getDueDate());
        assertThat(copy.getStatus()).isEqualTo(CopyStatus.MAINTENANCE);
        assertThat(copy.getConditionStatus()).isEqualTo(CopyCondition.DAMAGED);
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.memberId()).isEqualTo(1L);
        assertThat(response.bookCopyId()).isEqualTo(1L);
        assertThat(response.status()).isEqualTo(LoanStatus.RETURNED);

        verify(currentUserService).getCurrentMember(authentication);
        verify(loanRepository).findById(1L);
        verify(loanRepository).save(any(Loan.class));
    }

    @Test
    void myLoans_shouldReturnLoansBelongingToTheAuthenticatedMember() {

        Pageable pageable = PageRequest.of(0, 10);


        Member member = new Member();
        member.setId(1L);
        member.setMembershipStatus(MembershipStatus.ACTIVE);

        BookCopy copy = new BookCopy();
        copy.setId(1L);

        Loan loan = new Loan();
        loan.setId(1L);
        loan.setBookCopy(copy);
        loan.setMember(member);
        LocalDateTime returnedAt = LocalDateTime.now();
        loan.setReturnedAt(returnedAt);
        LocalDateTime borrowedAt = LocalDateTime.now();
        loan.setBorrowedAt(borrowedAt);
        loan.setDueDate(borrowedAt.plusDays(14));

        Authentication authentication = new UsernamePasswordAuthenticationToken("member@example.com", null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Page<Loan> loans = new PageImpl<>(List.of(loan),pageable,1);

        when(currentUserService.getCurrentMember(authentication)).thenReturn(member);
        when(loanRepository.findAllByMemberId(member.getId(), pageable)).thenReturn(loans);

        Page<LoanResponse> response = loanService.myLoans(pageable);

        assertThat(response.getContent())
                .usingRecursiveComparison()
                .isEqualTo(List.of(new LoanResponse(
                        1L,
                        1L,
                        1L,
                        borrowedAt,
                        borrowedAt.plusDays(14),
                        returnedAt,
                        LoanStatus.RETURNED
                )));

        verify(loanRepository).findAllByMemberId(member.getId(), pageable);
        verify(currentUserService).getCurrentMember(authentication);
        assertThat(response.getTotalElements()).isEqualTo(1);
        assertThat(response.getNumber()).isEqualTo(0);
        assertThat(response.getSize()).isEqualTo(10);

    }

    @Test
    void getAllLoans_shouldReturnAllLoans() {
        Pageable pageable = PageRequest.of(0, 10);

        Member member = new Member();
        member.setId(1L);
        member.setMembershipStatus(MembershipStatus.ACTIVE);


        BookCopy copy = new BookCopy();
        copy.setId(1L);

        Loan loan = new Loan();
        loan.setId(1L);
        loan.setBookCopy(copy);
        loan.setMember(member);
        LocalDateTime returnedAt = LocalDateTime.now();
        loan.setReturnedAt(returnedAt);
        LocalDateTime borrowedAt = LocalDateTime.now();
        loan.setBorrowedAt(borrowedAt);
        loan.setDueDate(borrowedAt.plusDays(14));

        Page<Loan> loans = new PageImpl<>(List.of(loan),pageable,1);

        when(loanRepository.findAll(pageable)).thenReturn(loans);

        Page<LoanResponse> response = loanService.getAllLoans(null, pageable);

        assertThat(response.getContent())
                .usingRecursiveComparison()
                        .isEqualTo(List.of(new LoanResponse(
                                1L,
                                1L,
                                1L,
                                borrowedAt,
                                borrowedAt.plusDays(14),
                                returnedAt,
                                LoanStatus.RETURNED
                        )));


        verify(loanRepository).findAll(pageable);
        assertThat(response.getTotalElements()).isEqualTo(1);
        assertThat(response.getNumber()).isEqualTo(0);
        assertThat(response.getSize()).isEqualTo(10);
    }

    @Test
    void getAllLoans_shouldReturnAllLoansWhenStatusIsProvided() {

        Pageable pageable = PageRequest.of(0, 10);
        Member member = new Member();
        member.setId(1L);
        member.setMembershipStatus(MembershipStatus.ACTIVE);

        BookCopy copy = new BookCopy();
        copy.setId(1L);

        Loan loan = new Loan();
        loan.setId(1L);
        loan.setBookCopy(copy);
        loan.setMember(member);
        LocalDateTime returnedAt = LocalDateTime.now();
        loan.setReturnedAt(returnedAt);
        LocalDateTime borrowedAt = LocalDateTime.now();
        loan.setBorrowedAt(borrowedAt);
        loan.setDueDate(borrowedAt.plusDays(14));

        Page<Loan> loans = new PageImpl<>(List.of(loan),pageable,1);

        when(loanRepository.findAll(ArgumentMatchers.<Specification<Loan>>any(),eq(pageable))).thenReturn(loans);

        Page<LoanResponse> response = loanService.getAllLoans(RETURNED, pageable);

        assertThat(response.getContent())
                .usingRecursiveComparison()
                .isEqualTo(List.of(new LoanResponse(
                        1L,
                        1L,
                        1L,
                        borrowedAt,
                        borrowedAt.plusDays(14),
                        returnedAt,
                        RETURNED
                )));

        verify(loanRepository).findAll(ArgumentMatchers.<Specification<Loan>>argThat
                (specification -> specification != null),eq(pageable));
        assertThat(response.getTotalElements()).isEqualTo(1);
        assertThat(response.getNumber()).isEqualTo(0);
        assertThat(response.getSize()).isEqualTo(10);
    }
}
