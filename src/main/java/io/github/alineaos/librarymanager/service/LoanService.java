package io.github.alineaos.librarymanager.service;

import io.github.alineaos.librarymanager.domain.entity.Book;
import io.github.alineaos.librarymanager.domain.entity.Loan;
import io.github.alineaos.librarymanager.domain.entity.User;
import io.github.alineaos.librarymanager.domain.enums.LoanStatus;
import io.github.alineaos.librarymanager.dto.LoanFilter;
import io.github.alineaos.librarymanager.dto.request.LoanPostRequest;
import io.github.alineaos.librarymanager.dto.response.BookBasicResponse;
import io.github.alineaos.librarymanager.dto.response.LoanGetResponse;
import io.github.alineaos.librarymanager.dto.response.LoanPostResponse;
import io.github.alineaos.librarymanager.dto.response.UserBasicResponse;
import io.github.alineaos.librarymanager.exception.BusinessException;
import io.github.alineaos.librarymanager.mapper.LoanMapper;
import io.github.alineaos.librarymanager.repository.LoanRepository;
import io.github.alineaos.librarymanager.repository.specification.LoanSpecification;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Validated
@Service
public class LoanService {
    private final LoanRepository repository;
    private final UserService userService;
    private final BookService bookService;
    private final LoanMapper mapper;

    private static final Set<LoanStatus> ACTIVE_STATUS = LoanStatus.getActiveStatus();

    public List<LoanGetResponse> findAll(LoanFilter loanFilter) {
        List<Loan> loans = repository.findAll(
                LoanSpecification.fetchRelationships()
                        .and(LoanSpecification.hasUserId(loanFilter.userId()))
                        .and(LoanSpecification.hasBookId(loanFilter.bookId()))
                        .and(LoanSpecification.hasLoanStatus(loanFilter.status()))
                        .and(LoanSpecification.hasRenewed(loanFilter.renewed()))
                        .and(LoanSpecification.hasBorrowedAt(loanFilter.borrowedAt()))
                        .and(LoanSpecification.hasDueAt(loanFilter.dueAt()))
                        .and(LoanSpecification.hasReturnedAt(loanFilter.returnedAt()))
        );

        return mapper.toLoanGetResponseList(loans);
    }

    public LoanPostResponse save(@Valid LoanPostRequest postRequest) {
        User user = userService.getUserByIdOrThrowNotFound(postRequest.userId());
        Book book = bookService.getBookByIdOrThrowNotFound(postRequest.bookId());

        assertUserDoesNotHaveActiveLoan(postRequest.userId());
        assertBookIsNotInActiveLoan(postRequest.bookId());

        LocalDate loanDate = postRequest.borrowedAt() == null ? LocalDate.now() : postRequest.borrowedAt();

        Loan loan = mapper.toLoan(postRequest);

        loan.setUser(user);
        loan.setBook(book);
        loan.setBorrowedAt(loanDate);
        loan.setDueAt(loanDate.plusDays(14));
        loan.setStatus(LoanStatus.BORROWED);

        Loan savedLoan = repository.save(loan);

        return mapper.toLoanPostResponse(savedLoan, newUserBasicResponse(user), newBookBasicResponse(book));
    }

    private UserBasicResponse newUserBasicResponse(User user) {
        return new UserBasicResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail()
        );
    }

    private BookBasicResponse newBookBasicResponse(Book book) {
        return new BookBasicResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor()
        );
    }

    private void assertUserDoesNotHaveActiveLoan(Long userId) {
        repository.findByUserIdAndStatusIn(userId, ACTIVE_STATUS)
                .ifPresent(loan -> throwUserHasActiveLoan(loan.getUser()));
    }

    private void assertBookIsNotInActiveLoan(Long bookId) {
        repository.findByBookIdAndStatusIn(bookId, ACTIVE_STATUS)
                .ifPresent(loan -> throwBookIsNotAvailable(loan.getBook()));
    }

    private void throwUserHasActiveLoan(User user) {
        throw new BusinessException("The user '%s' has an active loan.".formatted(user.getFullName()));
    }

    private void throwBookIsNotAvailable(Book book) {
        throw new BusinessException("The book '%s' is not available.".formatted(book.getTitle()));
    }
}
