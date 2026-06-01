package io.github.alineaos.librarymanager.mapper;

import io.github.alineaos.librarymanager.domain.entity.Loan;
import io.github.alineaos.librarymanager.dto.loans.LoanCreateRequest;
import io.github.alineaos.librarymanager.dto.books.BookBasicResponse;
import io.github.alineaos.librarymanager.dto.loans.LoanInfoResponse;
import io.github.alineaos.librarymanager.dto.loans.LoanHistoryResponse;
import io.github.alineaos.librarymanager.dto.loans.LoanCreateResponse;
import io.github.alineaos.librarymanager.dto.users.UserBasicResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LoanMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "borrowedAt", ignore = true)
    @Mapping(target = "dueAt", ignore = true)
    @Mapping(target = "returnedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Loan toLoan(LoanCreateRequest request);

    @Mapping(target = "id", source = "loan.id")
    LoanCreateResponse toLoanCreateResponse(Loan loan, UserBasicResponse user, BookBasicResponse book);

    LoanInfoResponse toLoanInfoResponse(Loan loan);
    List<LoanInfoResponse> toLoanInfoResponseList(List<Loan> loans);

    List<LoanHistoryResponse> toLoanHistoryResponse(List<Loan> loans);
}
