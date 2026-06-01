package io.github.alineaos.librarymanager.service;

import io.github.alineaos.librarymanager.domain.entity.Book;
import io.github.alineaos.librarymanager.domain.entity.Loan;
import io.github.alineaos.librarymanager.domain.entity.User;
import io.github.alineaos.librarymanager.domain.enums.LoanStatus;
import io.github.alineaos.librarymanager.dto.loans.LoanFilter;
import io.github.alineaos.librarymanager.dto.loans.LoanCreateRequest;
import io.github.alineaos.librarymanager.dto.loans.LoanReturnRequest;
import io.github.alineaos.librarymanager.dto.books.BookBasicResponse;
import io.github.alineaos.librarymanager.dto.loans.LoanInfoResponse;
import io.github.alineaos.librarymanager.dto.loans.LoanHistoryResponse;
import io.github.alineaos.librarymanager.dto.loans.LoanCreateResponse;
import io.github.alineaos.librarymanager.dto.users.UserBasicResponse;
import io.github.alineaos.librarymanager.exception.BusinessException;
import io.github.alineaos.librarymanager.exception.NotFoundException;
import io.github.alineaos.librarymanager.mapper.BookMapper;
import io.github.alineaos.librarymanager.mapper.LoanMapper;
import io.github.alineaos.librarymanager.mapper.UserMapper;
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
    private final LoanMapper mapper;
    private final UserService userService;
    private final BookService bookService;
    private final UserMapper userMapper;
    private final BookMapper bookMapper;

    private static final Set<LoanStatus> ACTIVE_STATUS = LoanStatus.getActiveStatus();

    public List<LoanInfoResponse> findAll(LoanFilter filter) {
        List<Loan> loans = repository.findAll(
                LoanSpecification.fetchRelationships()
                        .and(LoanSpecification.hasUserId(filter.userId()))
                        .and(LoanSpecification.hasBookId(filter.bookId()))
                        .and(LoanSpecification.hasLoanStatus(filter.status()))
                        .and(LoanSpecification.hasRenewed(filter.renewed()))
                        .and(LoanSpecification.hasBorrowedAt(filter.borrowedAt()))
                        .and(LoanSpecification.hasDueAt(filter.dueAt()))
                        .and(LoanSpecification.hasReturnedAt(filter.returnedAt()))
        );

        return mapper.toLoanInfoResponseList(loans);
    }

    public LoanInfoResponse findById(Long id) {
        Loan loan = findByIdOrThrowNotFound(id);

        return mapper.toLoanInfoResponse(loan);
    }

    public List<LoanHistoryResponse> findMyHistory(Long id) {
        List<Loan> loans = repository.findByUserIdIn(id);

        return mapper.toLoanHistoryResponse(loans);
    }

    public LoanCreateResponse save(@Valid LoanCreateRequest request) {
        User user = userService.getUserByIdOrThrowNotFound(request.userId());
        Book book = bookService.getBookByIdOrThrowNotFound(request.bookId());

        assertUserDoesNotHaveActiveLoan(request.userId());
        assertBookIsAvailable(request.bookId());

        LocalDate loanDate = request.borrowedAt() == null ? LocalDate.now() : request.borrowedAt();

        Loan loan = mapper.toLoan(request);

        loan.setUser(user);
        loan.setBook(book);
        loan.setBorrowedAt(loanDate);
        loan.setDueAt(loanDate.plusDays(14));
        loan.setStatus(LoanStatus.BORROWED);

        Loan savedLoan = repository.save(loan);

        UserBasicResponse userResponse = userMapper.toUserBasicResponse(user);
        BookBasicResponse bookResponse = bookMapper.toBookBasicResponse(book);

        return mapper.toLoanCreateResponse(savedLoan, userResponse, bookResponse);
    }

    public void renew(Long id) {
        Loan loan = findByIdOrThrowNotFound(id);

        assertLoanHasNeverBeenRenewed(loan);

        LocalDate newDueAt = loan.getDueAt().plusDays(14);

        loan.setStatus(LoanStatus.RENEWED);
        loan.setRenewed(true);
        loan.setDueAt(newDueAt);

        repository.save(loan);
    }

    public void finalize(Long id, @Valid LoanReturnRequest returnRequest) {
        Loan loan = findByIdOrThrowNotFound(id);

        assertLoanIsNotFinalized(loan);

        if (returnRequest.returnedAt() != null){
            assertReturnDateIsValid(loan, returnRequest.returnedAt());
        }

        LocalDate returnedDate = returnRequest.returnedAt() == null ? LocalDate.now() : returnRequest.returnedAt();

        loan.setStatus(LoanStatus.RETURNED);
        loan.setReturnedAt(returnedDate);

        repository.save(loan);
    }

    public void lost(Long id) {
        Loan loan = findByIdOrThrowNotFound(id);

        assertLoanIsNotFinalized(loan);

        loan.setStatus(LoanStatus.LOST);

        repository.save(loan);
    }

    public void cancel(Long id) {
        Loan loan = findByIdOrThrowNotFound(id);

        assertLoanIsNotFinalized(loan);

        loan.setStatus(LoanStatus.CANCELLED);

        repository.save(loan);
    }

    private void assertUserDoesNotHaveActiveLoan(Long userId) {
        repository.findByUserIdAndStatusIn(userId, ACTIVE_STATUS)
                .ifPresent(loan -> throwUserHasActiveLoan(loan.getUser()));
    }

    private void assertBookIsAvailable(Long bookId) {
        repository.findByBookIdAndStatusIn(bookId, ACTIVE_STATUS)
                .ifPresent(loan -> throwBookIsNotAvailable(loan.getBook()));
    }

    private void throwUserHasActiveLoan(User user) {
        throw new BusinessException("The user '%s' has an active loan.".formatted(user.getFullName()));
    }

    private void throwBookIsNotAvailable(Book book) {
        throw new BusinessException("The book '%s' is not available.".formatted(book.getTitle()));
    }

    private Loan findByIdOrThrowNotFound(Long id) {
        return repository.findByIdWithRelationships(id).orElseThrow(
                () -> new NotFoundException("Loan not found.")
        );
    }

    private void assertLoanHasNeverBeenRenewed(Loan loan) {
        if (loan.isRenewed()) throw new BusinessException("A Loan can be renewed only once.");
    }

    private void assertLoanIsNotFinalized(Loan loan) {
        LoanStatus status = loan.getStatus();

        if (status == LoanStatus.RETURNED || status == LoanStatus.CANCELLED) {
            throw new BusinessException("The Loan has already been finalized");
        }
    }

    private void assertReturnDateIsValid(Loan loan, LocalDate returnDate){
        if (returnDate.isBefore(loan.getBorrowedAt())) {
            throw new BusinessException("The return date cannot be before the loan date");
        }
    }
}
