package io.github.alineaos.librarymanager.util;

import io.github.alineaos.librarymanager.domain.entity.Book;
import io.github.alineaos.librarymanager.domain.entity.Loan;
import io.github.alineaos.librarymanager.domain.entity.User;
import io.github.alineaos.librarymanager.domain.enums.LoanStatus;
import io.github.alineaos.librarymanager.dto.loans.LoanCreateRequest;
import io.github.alineaos.librarymanager.dto.loans.LoanReturnRequest;
import io.github.alineaos.librarymanager.dto.books.BookBasicResponse;
import io.github.alineaos.librarymanager.dto.loans.LoanInfoResponse;
import io.github.alineaos.librarymanager.dto.loans.LoanHistoryResponse;
import io.github.alineaos.librarymanager.dto.loans.LoanCreateResponse;
import io.github.alineaos.librarymanager.dto.users.UserBasicResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LoanFactory {
    private final UserFactory userFactory;
    private final BookFactory bookFactory;

    public LoanFactory(UserFactory userFactory, BookFactory bookFactory) {
        this.userFactory = userFactory;
        this.bookFactory = bookFactory;
    }


    public List<Loan> newLoanList(){
        List<User> userList = userFactory.newUserList();
        User maria = userList.get(0);
        User gabriel = userList.get(1);
        User ana = userList.get(2);

        List<Book> bookList = bookFactory.newBookList();
        Book capitaesDaAreia = bookList.get(0);
        Book jogosVorazes = bookList.get(1);
        Book horaDaEstrela = bookList.get(2);

        LocalDate loanDate = LocalDate.parse("2026-05-31");
        LocalDate dueDate = loanDate.plusDays(14);

        Loan mariaCapitaesDaAreia = Loan.builder()
                .id(1L)
                .user(maria)
                .book(capitaesDaAreia)
                .status(LoanStatus.BORROWED)
                .renewed(false)
                .borrowedAt(loanDate)
                .dueAt(dueDate)
                .returnedAt(null)
                .createdAt(LocalDateTime.parse("2026-04-24T18:00:33"))
                .updatedAt(LocalDateTime.parse("2026-04-24T18:00:33"))
                .build();

        Loan gabrielJogosVorazes = Loan.builder()
                .id(2L)
                .user(gabriel)
                .book(jogosVorazes)
                .status(LoanStatus.BORROWED)
                .renewed(false)
                .borrowedAt(loanDate)
                .dueAt(dueDate)
                .returnedAt(null)
                .createdAt(LocalDateTime.parse("2026-04-24T18:01:33"))
                .updatedAt(LocalDateTime.parse("2026-04-24T18:01:33"))
                .build();

        Loan anaHoraDaEstrela = Loan.builder()
                .id(3L)
                .user(ana)
                .book(horaDaEstrela)
                .status(LoanStatus.RENEWED)
                .renewed(true)
                .borrowedAt(loanDate)
                .dueAt(dueDate.plusDays(14))
                .returnedAt(null)
                .createdAt(LocalDateTime.parse("2026-04-24T18:02:33"))
                .updatedAt(LocalDateTime.parse("2026-04-24T18:02:33"))
                .build();

        return new ArrayList<>(List.of(mariaCapitaesDaAreia, gabrielJogosVorazes, anaHoraDaEstrela));
    }

    public Loan newLoanSaved(){
        User userSaved = userFactory.newUserSaved();
        Book bookSaved = bookFactory.newBookSaved();

        LocalDate loanDate = LocalDate.parse("2026-05-31");
        LocalDate dueDate = loanDate.plusDays(14);

        return Loan.builder()
                .id(99L)
                .user(userSaved)
                .book(bookSaved)
                .status(LoanStatus.BORROWED)
                .renewed(false)
                .borrowedAt(loanDate)
                .dueAt(dueDate)
                .returnedAt(null)
                .createdAt(LocalDateTime.parse("2026-04-24T18:00:33"))
                .updatedAt(LocalDateTime.parse("2026-04-24T18:00:33"))
                .build();
    }

    public LoanCreateRequest newLoanCreateRequest(){
        Loan loan = newLoanSaved();

        return new LoanCreateRequest(
                loan.getUser().getId(),
                loan.getBook().getId(),
                loan.getBorrowedAt()
        );
    }

    public LoanCreateResponse newLoanCreateResponse(){
        Loan loan = newLoanSaved();

        return new LoanCreateResponse(
                loan.getId(),
                newUserBasicResponse(loan.getUser()),
                newBookBasicResponse(loan.getBook()),
                loan.getStatus(),
                loan.getBorrowedAt(),
                loan.getDueAt(),
                loan.getCreatedAt()
        );
    }

    public LoanInfoResponse newLoanInfoResponse(){
        Loan loan = newLoanList().getFirst();

        return new LoanInfoResponse(
                loan.getId(),
                newUserBasicResponse(loan.getUser()),
                newBookBasicResponse(loan.getBook()),
                loan.getStatus(),
                loan.isRenewed(),
                loan.getBorrowedAt(),
                loan.getDueAt(),
                loan.getReturnedAt(),
                loan.getCreatedAt(),
                loan.getUpdatedAt()
        );
    }

    public List<LoanHistoryResponse> newLoanHistoryResponse(){
        Loan loan = newLoanList().getFirst();
        Book book = loan.getBook();
        LoanHistoryResponse loanHistoryResponse = new LoanHistoryResponse(
                loan.getId(),
                newBookBasicResponse(book),
                loan.getStatus(),
                loan.isRenewed(),
                loan.getBorrowedAt(),
                loan.getDueAt(),
                loan.getReturnedAt()
        );

        return List.of(loanHistoryResponse);
    }

    public LoanReturnRequest newLoanReturnRequest(){
        LocalDate returnedAt = LocalDate.now();

        return new LoanReturnRequest(returnedAt);
    }

    public UserBasicResponse newUserBasicResponse(User user) {
        return new UserBasicResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail());
    }

    public BookBasicResponse newBookBasicResponse(Book book) {
        return new BookBasicResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor());
    }
}
