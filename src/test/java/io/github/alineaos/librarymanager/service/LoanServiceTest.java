package io.github.alineaos.librarymanager.service;

import io.github.alineaos.librarymanager.config.UnitTestConfig;
import io.github.alineaos.librarymanager.domain.entity.Book;
import io.github.alineaos.librarymanager.domain.entity.Loan;
import io.github.alineaos.librarymanager.domain.entity.User;
import io.github.alineaos.librarymanager.domain.enums.LoanStatus;
import io.github.alineaos.librarymanager.dto.LoanFilter;
import io.github.alineaos.librarymanager.dto.request.LoanReturnRequest;
import io.github.alineaos.librarymanager.dto.response.LoanGetResponse;
import io.github.alineaos.librarymanager.dto.response.LoanHistoryResponse;
import io.github.alineaos.librarymanager.dto.response.LoanPostResponse;
import io.github.alineaos.librarymanager.exception.BusinessException;
import io.github.alineaos.librarymanager.exception.NotFoundException;
import io.github.alineaos.librarymanager.mapper.LoanMapper;
import io.github.alineaos.librarymanager.repository.LoanRepository;
import io.github.alineaos.librarymanager.util.BookFactory;
import io.github.alineaos.librarymanager.util.GenreFactory;
import io.github.alineaos.librarymanager.util.LoanFactory;
import io.github.alineaos.librarymanager.util.UserFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest extends UnitTestConfig {
    @InjectMocks
    private LoanService service;
    @Mock
    private LoanRepository repository;
    @Mock
    private UserService userService;
    @Mock
    private BookService bookService;
    @Spy
    private LoanMapper mapper = Mappers.getMapper(LoanMapper.class);

    private final UserFactory userFactory = new UserFactory();
    private final GenreFactory genreFactory = new GenreFactory();
    private final BookFactory bookFactory = new BookFactory(genreFactory);
    private final LoanFactory loanFactory = new LoanFactory(userFactory, bookFactory);

    private List<Loan> loanList;

    @BeforeEach
    void init() {
        loanList = loanFactory.newLoanList();
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("loanFilterSource")
    @DisplayName("findAll a list with filtered loans when the filter is valid")
    @Order(1)
    void findAll_ListWithFilteredLoans_WhenFilterIsValid(LoanFilter filter, List<Loan> expectedLoans) {

        List<LoanGetResponse> expectedDtos = expectedLoans.stream()
                .map(l -> new LoanGetResponse(l.getId(),
                        loanFactory.newUserBasicResponse(l.getUser()),
                        loanFactory.newBookBasicResponse(l.getBook()),
                        l.getStatus(),
                        l.isRenewed(),
                        l.getBorrowedAt(),
                        l.getDueAt(),
                        l.getReturnedAt(),
                        l.getCreatedAt(),
                        l.getUpdatedAt()))
                .toList();

        when(repository.findAll(ArgumentMatchers.<Specification<Loan>>any())).thenReturn(expectedLoans);

        List<LoanGetResponse> result = service.findAll(filter);

        Assertions.assertThat(result).isNotNull().containsExactlyElementsOf(expectedDtos);
    }

    @Test
    @Order(2)
    @DisplayName("findById return a loan with given id")
    void findById_ReturnsLoanById_WhenSuccessful() {
        Loan expectedLoan = loanList.getFirst();
        Long loanId = expectedLoan.getId();
        LoanGetResponse expectedDto = loanFactory.newLoanGetResponse();

        when(repository.findByIdWithRelationships(loanId)).thenReturn(Optional.of(expectedLoan));

        LoanGetResponse result = service.findById(loanId);

        Assertions.assertThat(result).isEqualTo(expectedDto);
    }

    @Test
    @Order(3)
    @DisplayName("findById throws NotFoundException when loan is not found")
    void findById_ThrowsNotFoundException_WhenLoanIsNotFound() {
        Loan expectedLoan = loanList.getFirst();
        Long loanId = expectedLoan.getId();

        when(repository.findByIdWithRelationships(loanId)).thenReturn(Optional.empty());

        Assertions.assertThatException()
                .isThrownBy(() -> service.findById(loanId))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    @Order(4)
    @DisplayName("findMyHistory returns loan history")
    void findMyHistory_ReturnsLoanHistory_WhenSuccessful() {
        Long userId = 1L;
        List<Loan> expectedUserLoans = loanList.stream()
                .filter(l -> l.getUser().getId().equals(userId))
                .toList();

        when(repository.findByUserIdIn(userId)).thenReturn(expectedUserLoans);

        List<LoanHistoryResponse> result = service.findMyHistory(userId);

        Assertions.assertThat(result).hasSize(expectedUserLoans.size());
    }

    @Test
    @Order(5)
    @DisplayName("save creates a loan")
    void save_CreatesLoan_WhenSuccessful() {
        Loan loanSaved = loanFactory.newLoanSaved();
        User user = loanSaved.getUser();
        Book book = loanSaved.getBook();

        when(userService.getUserByIdOrThrowNotFound(user.getId())).thenReturn(user);
        when(bookService.getBookByIdOrThrowNotFound(book.getId())).thenReturn(book);
        when(repository.findByUserIdAndStatusIn(user.getId(), LoanStatus.getActiveStatus())).thenReturn(Optional.empty());
        when(repository.findByBookIdAndStatusIn(book.getId(), LoanStatus.getActiveStatus())).thenReturn(Optional.empty());
        when(repository.save(any(Loan.class))).thenReturn(loanSaved);

        LoanPostResponse result = service.save(loanFactory.newLoanPostRequest());

        Assertions.assertThat(result.id()).isEqualTo(loanSaved.getId());
        Assertions.assertThat(result.borrowedAt().plusDays(14)).isEqualTo(result.dueAt());
    }

    @Test
    @Order(6)
    @DisplayName("save throws NotFoundException when book is null")
    void save_ThrowsNotFoundException_WhenBookIsNull() {
        Loan loanSaved = loanFactory.newLoanSaved();
        User user = loanSaved.getUser();

        when(userService.getUserByIdOrThrowNotFound(user.getId())).thenReturn(user);
        when(bookService.getBookByIdOrThrowNotFound(any())).thenThrow(new NotFoundException("Book not found."));

        Assertions.assertThatException()
                .isThrownBy(() -> service.save(loanFactory.newLoanPostRequest()))
                .isInstanceOf(ResponseStatusException.class)
                .withMessageContaining("Book not found.");
    }

    @Test
    @Order(7)
    @DisplayName("save throws NotFoundException when user is null")
    void save_ThrowsNotFoundException_WhenUserIsNull() {
        when(userService.getUserByIdOrThrowNotFound(any())).thenThrow(new NotFoundException("User not found."));

        Assertions.assertThatException()
                .isThrownBy(() -> service.save(loanFactory.newLoanPostRequest()))
                .isInstanceOf(ResponseStatusException.class)
                .withMessageContaining("User not found.");
    }

    @Test
    @Order(8)
    @DisplayName("save throws BusinessException when user has an active loan")
    void save_ThrowsBusinessException_WhenUserHasActiveLoan() {
        Loan loanSaved = loanFactory.newLoanSaved();
        User user = loanSaved.getUser();
        Book book = loanSaved.getBook();

        when(userService.getUserByIdOrThrowNotFound(user.getId())).thenReturn(user);
        when(bookService.getBookByIdOrThrowNotFound(book.getId())).thenReturn(book);
        when(repository.findByUserIdAndStatusIn(user.getId(), LoanStatus.getActiveStatus()))
                .thenThrow(new BusinessException("The user '%s' has an active loan.".formatted(user.getFullName())));

        Assertions.assertThatException()
                .isThrownBy(() -> service.save(loanFactory.newLoanPostRequest()))
                .isInstanceOf(BusinessException.class)
                .withMessage("The user '%s' has an active loan.".formatted(user.getFullName()));
    }

    @Test
    @Order(9)
    @DisplayName("save throws BusinessException when book is not available")
    void save_ThrowsBusinessException_WhenBookIsNotAvailable() {
        Loan loanSaved = loanFactory.newLoanSaved();
        User user = loanSaved.getUser();
        Book book = loanSaved.getBook();

        when(userService.getUserByIdOrThrowNotFound(user.getId())).thenReturn(user);
        when(bookService.getBookByIdOrThrowNotFound(book.getId())).thenReturn(book);
        when(repository.findByUserIdAndStatusIn(user.getId(), LoanStatus.getActiveStatus())).thenReturn(Optional.empty());
        when(repository.findByBookIdAndStatusIn(book.getId(), LoanStatus.getActiveStatus()))
                .thenThrow(new BusinessException("The book '%s' is not available.".formatted(book.getTitle())));

        Assertions.assertThatException()
                .isThrownBy(() -> service.save(loanFactory.newLoanPostRequest()))
                .isInstanceOf(BusinessException.class)
                .withMessage("The book '%s' is not available.".formatted(book.getTitle()));
    }

    @Test
    @Order(10)
    @DisplayName("renew updates loan status and extends due date")
    void renew_UpdatesLoanStatusAndExtendsDueDate_WhenSuccessful() {
        Loan loanToRenew = loanList.getFirst();
        Long loanId = loanToRenew.getId();
        LocalDate newDueDate = loanToRenew.getDueAt().plusDays(14);

        when(repository.findByIdWithRelationships(loanId)).thenReturn(Optional.of(loanToRenew));

        service.renew(loanId);

        Assertions.assertThat(loanToRenew.isRenewed()).isTrue();
        Assertions.assertThat(newDueDate).isEqualTo(loanToRenew.getDueAt());
        Assertions.assertThat(loanToRenew.getStatus()).isEqualTo(LoanStatus.RENEWED);
    }

    @Test
    @Order(11)
    @DisplayName("renew throws NotFoundException when loan is not found")
    void renew_ThrowsNotFoundException_WhenLoanIsNotFound() {
        Loan loanToRenew = loanList.getFirst();
        Long loanId = loanToRenew.getId();

        when(repository.findByIdWithRelationships(loanId)).thenReturn(Optional.empty());

        Assertions.assertThatException()
                .isThrownBy(() -> service.renew(loanId))
                .isInstanceOf(NotFoundException.class)
                .withMessageContaining("Loan not found.");
    }

    @Test
    @Order(12)
    @DisplayName("renew throws BusinessException when loan already been renewed once")
    void renew_ThrowsBusinessException_WhenLoanAlreadyBeenRenewedOnce() {
        Loan alreadyRenewedLoan = loanList.getLast();
        Long loanId = alreadyRenewedLoan.getId();

        when(repository.findByIdWithRelationships(loanId)).thenReturn(Optional.of(alreadyRenewedLoan));

        Assertions.assertThatException()
                .isThrownBy(() -> service.renew(loanId))
                .isInstanceOf(BusinessException.class)
                .withMessage("A Loan can be renewed only once.");
    }

    @Test
    @Order(13)
    @DisplayName("finalize updates the loan status to returned and releases user and book")
    void finalize_UpdatesLoanStatusAndReleasesUserAndBook_WhenSuccessful() {
        Loan loanToReturn = loanList.getFirst();
        Long loanId = loanToReturn.getId();

        LoanReturnRequest returnRequest = loanFactory.newLoanReturnRequest();

        when(repository.findByIdWithRelationships(loanId)).thenReturn(Optional.of(loanToReturn));

        service.finalize(loanId, returnRequest);

        Assertions.assertThat(loanToReturn.getStatus()).isEqualTo(LoanStatus.RETURNED);
        Assertions.assertThat(loanToReturn.getReturnedAt()).isNotNull().isEqualTo(returnRequest.returnedAt());
    }

    @ParameterizedTest
    @MethodSource("loanCorrectReturnDateSource")
    @Order(14)
    @DisplayName("finalize sets the correct returnedAt date based on request")
    void finalize_SetsCorrectReturnedAtDate_WhenSuccessful(LocalDate inputDate) {
        Loan loanToReturn = loanList.getFirst();
        Long loanId = loanToReturn.getId();

        LoanReturnRequest returnRequest = new LoanReturnRequest(inputDate);

        when(repository.findByIdWithRelationships(loanId)).thenReturn(Optional.of(loanToReturn));

        LocalDate returnedDate = returnRequest.returnedAt() == null ? LocalDate.now() : returnRequest.returnedAt();

        service.finalize(loanId, returnRequest);

        Assertions.assertThat(loanToReturn.getReturnedAt()).isNotNull().isEqualTo(returnedDate);
    }

    @Test
    @Order(15)
    @DisplayName("finalize throws Not Found Exception when loan is not found")
    void finalize_ThrowsNotFoundException_WhenLoanIsNotFound() {
        Loan loanToReturn = loanList.getFirst();
        Long loanId = loanToReturn.getId();

        LoanReturnRequest returnRequest = loanFactory.newLoanReturnRequest();

        when(repository.findByIdWithRelationships(loanId)).thenReturn(Optional.empty());

        Assertions.assertThatException()
                .isThrownBy(() -> service.finalize(loanId, returnRequest))
                .isInstanceOf(NotFoundException.class)
                .withMessageContaining("Loan not found.");
    }

    @ParameterizedTest
    @MethodSource("loanStatusFinalizedSource")
    @Order(16)
    @DisplayName("finalize throws BusinessException when loan has already been finalized")
    void finalize_ThrowsBusinessException_WhenLoanAlreadyBeenFinalized(LoanStatus finalizedStatus) {
        Loan loanToReturn = loanList.getFirst();
        Long loanId = loanToReturn.getId();

        loanToReturn.setStatus(finalizedStatus);

        LoanReturnRequest returnRequest = loanFactory.newLoanReturnRequest();

        when(repository.findByIdWithRelationships(loanId)).thenReturn(Optional.of(loanToReturn));

        Assertions.assertThatException()
                .isThrownBy(() -> service.finalize(loanId, returnRequest))
                .isInstanceOf(BusinessException.class)
                .withMessage("The Loan has already been finalized");
    }

    @Test
    @Order(17)
    @DisplayName("finalize throws BusinessException when return date is before the loan date")
    void finalize_ThrowsBusinessException_WhenReturnDateIsBeforeTheLoanDate() {
        Loan loanToReturn = loanList.getFirst();
        Long loanId = loanToReturn.getId();

        LocalDate wrongReturnDate = loanToReturn.getBorrowedAt().minusDays(1);
        LoanReturnRequest returnRequest = new LoanReturnRequest(wrongReturnDate);

        when(repository.findByIdWithRelationships(loanId)).thenReturn(Optional.of(loanToReturn));

        Assertions.assertThatException()
                .isThrownBy(() -> service.finalize(loanId, returnRequest))
                .isInstanceOf(BusinessException.class)
                .withMessage("The return date cannot be before the loan date");
    }

    @Test
    @Order(18)
    @DisplayName("lost updates the loan status")
    void lost_UpdatesTheLoanStatus_WhenSuccessful() {
        Loan loanToUpdate = loanList.getFirst();
        Long loanId = loanToUpdate.getId();

        when(repository.findByIdWithRelationships(loanId)).thenReturn(Optional.of(loanToUpdate));

        service.lost(loanId);

        Assertions.assertThat(loanToUpdate.getStatus()).isEqualTo(LoanStatus.LOST);
    }

    @Test
    @Order(19)
    @DisplayName("lost throws NotFoundException when loan is not found")
    void lost_ThrowsNotFoundException_WhenLoanIsNotFound() {
        Loan loanToUpdate = loanList.getFirst();
        Long loanId = loanToUpdate.getId();

        when(repository.findByIdWithRelationships(loanId)).thenReturn(Optional.empty());

        Assertions.assertThatException()
                .isThrownBy(() -> service.lost(loanId))
                .isInstanceOf(NotFoundException.class)
                .withMessageContaining("Loan not found.");
    }

    @ParameterizedTest
    @MethodSource("loanStatusFinalizedSource")
    @Order(20)
    @DisplayName("lost throws BusinessException when loan has already been finalized")
    void lost_ThrowsBusinessException_WhenLoanAlreadyBeenFinalized(LoanStatus finalizedStatus) {
        Loan loanToUpdate = loanList.getFirst();
        Long loanId = loanToUpdate.getId();

        loanToUpdate.setStatus(finalizedStatus);

        when(repository.findByIdWithRelationships(loanId)).thenReturn(Optional.of(loanToUpdate));

        Assertions.assertThatException()
                .isThrownBy(() -> service.lost(loanId))
                .isInstanceOf(BusinessException.class)
                .withMessage("The Loan has already been finalized");
    }

    @Test
    @Order(21)
    @DisplayName("cancel updates the loan status")
    void cancel_UpdatesTheLoanStatus_WhenSuccessful() {
        Loan loanToCancel = loanList.getFirst();
        Long loanId = loanToCancel.getId();

        when(repository.findByIdWithRelationships(loanId)).thenReturn(Optional.of(loanToCancel));

        service.cancel(loanId);

        Assertions.assertThat(loanToCancel.getStatus()).isEqualTo(LoanStatus.CANCELLED);
    }

    @Test
    @Order(22)
    @DisplayName("cancel throws NotFoundException when loan is not found")
    void cancel_ThrowsNotFoundException_WhenLoanIsNotFound() {
        Loan loanToCancel = loanList.getFirst();
        Long loanId = loanToCancel.getId();

        when(repository.findByIdWithRelationships(loanId)).thenReturn(Optional.empty());

        Assertions.assertThatException()
                .isThrownBy(() -> service.cancel(loanId))
                .isInstanceOf(NotFoundException.class)
                .withMessageContaining("Loan not found.");
    }

    @ParameterizedTest
    @MethodSource("loanStatusFinalizedSource")
    @Order(23)
    @DisplayName("cancel throws BusinessException when loan has already been finalized")
    void cancel_ThrowsBusinessException_WhenLoanAlreadyBeenFinalized(LoanStatus finalizedStatus) {
        Loan loanToCancel = loanList.getFirst();
        Long loanId = loanToCancel.getId();

        loanToCancel.setStatus(finalizedStatus);

        when(repository.findByIdWithRelationships(loanId)).thenReturn(Optional.of(loanToCancel));

        Assertions.assertThatException()
                .isThrownBy(() -> service.cancel(loanId))
                .isInstanceOf(BusinessException.class)
                .withMessage("The Loan has already been finalized");
    }

    private static Stream<Arguments> loanFilterSource() {
        UserFactory filterUserFactory = new UserFactory();
        GenreFactory filterGenreFactory = new GenreFactory();
        BookFactory filterBookFactory = new BookFactory(filterGenreFactory);
        LoanFactory filterLoanFactory = new LoanFactory(filterUserFactory, filterBookFactory);

        List<Loan> filteredList = filterLoanFactory.newLoanList();
        Long userId = 3L;
        Long bookId = 2L;
        LoanStatus renewedStatus = LoanStatus.RENEWED;
        Long invalidUserId = 9999L;

        return Stream.of(
                Arguments.of(new LoanFilter(null, null, null, null, null, null, null),
                        filteredList),

                Arguments.of(new LoanFilter(userId, null, null, null, null, null, null),
                        filteredList.stream()
                                .filter(l -> l.getUser().getId().equals(userId))
                                .toList()
                ),

                Arguments.of(new LoanFilter(null, bookId, null, null, null, null, null),
                        filteredList.stream()
                                .filter(l -> l.getBook().getId().equals(bookId))
                                .toList()
                ),

                Arguments.of(new LoanFilter(userId, null, renewedStatus, null, null, null, null),
                        filteredList.stream()
                                .filter(l -> l.getUser().getId().equals(userId))
                                .filter(l -> l.getStatus().equals(renewedStatus))
                                .toList()
                ),

                Arguments.of(new LoanFilter(invalidUserId, null, null, null, null, null, null),
                        List.of()
                )
        );
    }

    private static Stream<LocalDate> loanCorrectReturnDateSource() {
        return Stream.of(
                LocalDate.now().plusMonths(2),
                null
        );
    }

    private static Stream<LoanStatus> loanStatusFinalizedSource() {
        return Stream.of(
                LoanStatus.RETURNED,
                LoanStatus.CANCELLED
        );
    }
}