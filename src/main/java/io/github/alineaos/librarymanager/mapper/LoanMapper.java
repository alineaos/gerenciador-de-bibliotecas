package io.github.alineaos.librarymanager.mapper;

import io.github.alineaos.librarymanager.domain.entity.Loan;
import io.github.alineaos.librarymanager.dto.request.LoanPostRequest;
import io.github.alineaos.librarymanager.dto.response.BookBasicResponse;
import io.github.alineaos.librarymanager.dto.response.LoanGetResponse;
import io.github.alineaos.librarymanager.dto.response.LoanHistoryResponse;
import io.github.alineaos.librarymanager.dto.response.LoanPostResponse;
import io.github.alineaos.librarymanager.dto.response.UserBasicResponse;
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
    Loan toLoan(LoanPostRequest postRequest);

    @Mapping(target = "id", source = "loan.id")
    LoanPostResponse toLoanPostResponse(Loan loan, UserBasicResponse user, BookBasicResponse book);

    LoanGetResponse toLoanGetResponse(Loan loan);

    List<LoanGetResponse> toLoanGetResponseList(List<Loan> loans);

    List<LoanHistoryResponse> toLoanHistoryResponse(List<Loan> loans);
}
