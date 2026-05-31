package io.github.alineaos.librarymanager.controller;

import io.github.alineaos.librarymanager.config.UnitTestConfig;
import io.github.alineaos.librarymanager.domain.entity.Loan;
import io.github.alineaos.librarymanager.domain.enums.LoanStatus;
import io.github.alineaos.librarymanager.dto.LoanFilter;
import io.github.alineaos.librarymanager.dto.request.LoanPostRequest;
import io.github.alineaos.librarymanager.dto.request.LoanReturnRequest;
import io.github.alineaos.librarymanager.dto.response.LoanGetResponse;
import io.github.alineaos.librarymanager.dto.response.LoanHistoryResponse;
import io.github.alineaos.librarymanager.dto.response.LoanPostResponse;
import io.github.alineaos.librarymanager.exception.BusinessException;
import io.github.alineaos.librarymanager.exception.NotFoundException;
import io.github.alineaos.librarymanager.security.config.SecurityConfig;
import io.github.alineaos.librarymanager.service.LoanService;
import io.github.alineaos.librarymanager.util.BookFactory;
import io.github.alineaos.librarymanager.util.FileUtils;
import io.github.alineaos.librarymanager.util.GenreFactory;
import io.github.alineaos.librarymanager.util.LoanErrorFactory;
import io.github.alineaos.librarymanager.util.LoanFactory;
import io.github.alineaos.librarymanager.util.UserFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.matchesPattern;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = LoanController.class)
@WithMockUser
@Import({FileUtils.class, UserFactory.class, BookFactory.class, GenreFactory.class, LoanFactory.class, SecurityConfig.class})
class LoanControllerTest extends UnitTestConfig {
    private static final String URL = "/v1/loans";
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private LoanService service;
    @Autowired
    private LoanFactory loanFactory;
    @Autowired
    private FileUtils fileUtils;

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("loanFilterSource")
    @DisplayName("GET v1/loans returns 200 (ok) and a list with filtered loans when the user is admin and filters are valid")
    @Order(1)
    @WithMockUser(authorities = "SCOPE_ADMIN")
    void findAll_ReturnsOkAndFilteredLoans_WhenUserIsAdminAndFiltersAreValid(String fileName, LoanFilter filter, List<Loan> expectedLoans) throws Exception {
        String response = fileUtils.readResourceFile("loan/%s".formatted(fileName));

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

        when(service.findAll(filter)).thenReturn(expectedDtos);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(URL);

        if (filter.userId() != null) requestBuilder.queryParam("userId", String.valueOf(filter.userId()));
        if (filter.bookId() != null) requestBuilder.queryParam("bookId", String.valueOf(filter.bookId()));
        if (filter.status() != null) requestBuilder.queryParam("status", String.valueOf(filter.status()));

        mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @DisplayName("GET v1/loans returns 403 (forbidden) when user is not an admin")
    @Order(2)
    @WithMockUser(authorities = "SCOPE_USER")
    void findAll_ReturnsForbidden_WhenUserIsNotAdmin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(URL))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @DisplayName("GET v1/loans/1 returns 200 (ok) and a loan with given id when the user is an admin")
    @Order(3)
    @WithMockUser(authorities = "SCOPE_ADMIN")
    void findById_ReturnsOkAndLoanById_WhenUserIsAdmin() throws Exception {
        Long targetLoanId = 1L;
        LoanGetResponse foundLoan = loanFactory.newLoanGetResponse();

        when(service.findById(targetLoanId)).thenReturn(foundLoan);

        String response = fileUtils.readResourceFile("loan/get-response-loan-by-id.json");

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}", targetLoanId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @DisplayName("GET v1/loans/1 returns 403 (forbidden) when the user is not an admin")
    @Order(4)
    @WithMockUser(authorities = "SCOPE_USER")
    void findById_ReturnsForbidden_WhenUserIsNotAdmin() throws Exception {
        Long targetLoanId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}", targetLoanId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @DisplayName("GET v1/loans/999 returns 404 (not found) when the loan is not found")
    @Order(5)
    @WithMockUser(authorities = "SCOPE_ADMIN")
    void findById_ReturnsNotFound_WhenLoanIsNotFound() throws Exception {
        Long targetLoanId = 999L;

        when(service.findById(targetLoanId)).thenThrow(new NotFoundException("Loan not found."));

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}", targetLoanId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("Loan not found."))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").value(matchesPattern("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.*$")));
    }

    @Test
    @DisplayName("GET v1/loans/my-history returns 200 (ok) and the user loan history when the user is logged")
    @Order(6)
    @WithMockUser(authorities = "SCOPE_USER")
    void findMyHistory_ReturnsOkAndTheUserLoanHistory_WhenUserIsLogged() throws Exception {
        Long userId = 1L;

        List<LoanHistoryResponse> expectedUserLoans = loanFactory.newLoanHistoryResponse();
        when(service.findMyHistory(userId)).thenReturn(expectedUserLoans);

        String response = fileUtils.readResourceFile("loan/get-response-loan-my-history.json");

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/my-history")
                        .with(jwt().jwt(builder -> builder.claim("userId", userId))))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @DisplayName("POST v1/loans returns 201 (created) and creates a loan when the user is an admin and fields are valid")
    @Order(7)
    @WithMockUser(authorities = "SCOPE_ADMIN")
    void save_ReturnsCreatedAndCreatesLoan_WhenUserIsAdminAndFieldsAreValid() throws Exception {
        String request = fileUtils.readResourceFile("loan/post-request-loan.json");
        String response = fileUtils.readResourceFile("loan/post-response-loan.json");

        LoanPostResponse loanSavedResponse = loanFactory.newLoanPostResponse();

        when(service.save(any(LoanPostRequest.class))).thenReturn(loanSavedResponse);

        mockMvc.perform(MockMvcRequestBuilders.post(URL)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @DisplayName("POST v1/loans returns 403 (forbidden) when the user is not an admin")
    @Order(8)
    @WithMockUser(authorities = "SCOPE_USER")
    void save_ReturnsForbidden_WhenUserIsNotAdmin() throws Exception {
        String request = fileUtils.readResourceFile("loan/post-request-loan.json");

        mockMvc.perform(MockMvcRequestBuilders.post(URL)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @ParameterizedTest(name = "[{index}] {0} ")
    @MethodSource("postBadRequestSource")
    @DisplayName("POST v1/loans returns 400 (bad request) when fields are invalid")
    @Order(9)
    @WithMockUser(authorities = "SCOPE_ADMIN")
    void save_ReturnsBadRequest_WhenFieldsAreInvalid(String fileName, List<String> errors) throws Exception {
        String request = fileUtils.readResourceFile("loan/%s".formatted(fileName));

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(URL)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        Exception resolvedException = mvcResult.getResolvedException();

        Assertions.assertThat(resolvedException).isNotNull();

        Assertions.assertThat(resolvedException.getMessage()).contains(errors);
    }

    @ParameterizedTest(name = "[{index}] {0} ")
    @MethodSource("postNotFoundExceptionSource")
    @DisplayName("POST v1/loans returns 404 (not found) when service throws NotFoundException")
    @Order(10)
    @WithMockUser(authorities = "SCOPE_ADMIN")
    void save_ReturnsNotFound_WhenServiceThrowsNotFoundException(NotFoundException exception) throws Exception {
        String request = fileUtils.readResourceFile("loan/post-request-loan.json");

        String errorMessage = exception.getMessage();

        when(service.save(any(LoanPostRequest.class))).thenThrow(exception);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(URL)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn();

        Exception resolvedException = mvcResult.getResolvedException();

        Assertions.assertThat(resolvedException).isNotNull();

        Assertions.assertThat(resolvedException.getMessage()).contains(errorMessage);
    }

    @ParameterizedTest(name = "[{index}] {0} ")
    @MethodSource("postBusinessExceptionSource")
    @DisplayName("POST v1/loans returns 400 (bad request) when service throws BusinessException")
    @Order(11)
    @WithMockUser(authorities = "SCOPE_ADMIN")
    void save_ReturnsBadRequest_WhenServiceThrowsBusinessException(BusinessException exception) throws Exception {
        String request = fileUtils.readResourceFile("loan/post-request-loan.json");

        String errorMessage = exception.getMessage();

        when(service.save(any(LoanPostRequest.class))).thenThrow(exception);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(URL)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        Exception resolvedException = mvcResult.getResolvedException();

        Assertions.assertThat(resolvedException).isNotNull();

        Assertions.assertThat(resolvedException.getMessage()).isEqualTo(errorMessage);
    }

    @Test
    @DisplayName("PATCH v1/loans/1/renew returns 204 (no content) and renews the loan when user is an admin")
    @Order(12)
    @WithMockUser(authorities = "SCOPE_ADMIN")
    void renew_ReturnsNoContentAndRenewsLoan_WhenUserIsAdmin() throws Exception {
        Long loanId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.patch(URL + "/{id}/renew", loanId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        verify(service, times(1)).renew(loanId);
    }

    @Test
    @DisplayName("PATCH v1/loans/1/renew returns 403 (forbidden) when user is not an admin")
    @Order(13)
    @WithMockUser(authorities = "SCOPE_USER")
    void renew_ReturnsForbidden_WhenUserIsNotAdmin() throws Exception {
        Long loanId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.patch(URL + "/{id}/renew", loanId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());

        verify(service, times(0)).renew(loanId);
    }

    @Test
    @DisplayName("PATCH v1/loans/1/renew returns 404 (not found) when service throws NotFoundException")
    @Order(14)
    @WithMockUser(authorities = "SCOPE_ADMIN")
    void renew_ReturnsNotFound_WhenServiceThrowsNotFoundException() throws Exception {
        Long loanId = 1L;
        String errorMessage = "Loan not found.";

        doThrow(new NotFoundException(errorMessage)).when(service).renew(loanId);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.patch(URL + "/{id}/renew", loanId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn();

        Exception resolvedException = mvcResult.getResolvedException();

        Assertions.assertThat(resolvedException).isNotNull();

        Assertions.assertThat(resolvedException.getMessage()).contains(errorMessage);
    }

    @Test
    @DisplayName("PATCH v1/loans/1/renew returns 400 (bad request) when service throws BusinessException")
    @Order(15)
    @WithMockUser(authorities = "SCOPE_ADMIN")
    void renew_ReturnsBadRequest_WhenServiceThrowsBusinessException() throws Exception {
        Long loanId = 1L;
        String errorMessage = "A Loan can be renewed only once.";

        doThrow(new BusinessException(errorMessage)).when(service).renew(loanId);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.patch(URL + "/{id}/renew", loanId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        Exception resolvedException = mvcResult.getResolvedException();

        Assertions.assertThat(resolvedException).isNotNull();

        Assertions.assertThat(resolvedException.getMessage()).isEqualTo(errorMessage);
    }

    @Test
    @DisplayName("PATCH v1/loans/1/return returns 204 (no content) and finalizes the loan when user is an admin")
    @Order(16)
    @WithMockUser(authorities = "SCOPE_ADMIN")
    void finalize_ReturnsNoContentAndFinalizesLoan_WhenUserIsAdmin() throws Exception {
        Long loanId = 1L;
        LoanReturnRequest returnRequest = new LoanReturnRequest(LocalDate.parse("2026-05-31"));
        String request = fileUtils.readResourceFile("loan/patch-request-loan-finalize.json");

        mockMvc.perform(MockMvcRequestBuilders.patch(URL + "/{id}/return", loanId)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        verify(service, times(1)).finalize(loanId, returnRequest);
    }

    @Test
    @DisplayName("PATCH v1/loans/1/return returns 403 (forbidden) when user is not an admin")
    @Order(17)
    @WithMockUser(authorities = "SCOPE_USER")
    void finalize_ReturnsForbidden_WhenUserIsNotAdmin() throws Exception {
        Long loanId = 1L;
        LoanReturnRequest returnRequest = new LoanReturnRequest(LocalDate.parse("2026-05-31"));
        String request = fileUtils.readResourceFile("loan/patch-request-loan-finalize.json");

        mockMvc.perform(MockMvcRequestBuilders.patch(URL + "/{id}/return", loanId)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());

        verify(service, times(0)).finalize(loanId, returnRequest);
    }

    @Test
    @DisplayName("PATCH v1/loans/1/return returns 404 (not found) when service throws NotFoundException")
    @Order(18)
    @WithMockUser(authorities = "SCOPE_ADMIN")
    void finalize_ReturnsNotFound_WhenServiceThrowsNotFoundException() throws Exception {
        Long loanId = 1L;
        LoanReturnRequest returnRequest = new LoanReturnRequest(LocalDate.parse("2026-05-31"));
        String request = fileUtils.readResourceFile("loan/patch-request-loan-finalize.json");
        String errorMessage = "Loan not found.";

        doThrow(new NotFoundException(errorMessage)).when(service).finalize(loanId, returnRequest);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.patch(URL + "/{id}/return", loanId)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn();

        Exception resolvedException = mvcResult.getResolvedException();

        Assertions.assertThat(resolvedException).isNotNull();

        Assertions.assertThat(resolvedException.getMessage()).contains(errorMessage);
    }

    @ParameterizedTest(name = "[{index}] {0} ")
    @MethodSource("patchFinalizeBusinessExceptionSource")
    @DisplayName("PATCH v1/loans/1/return returns 400 (bad request) when service throws BusinessException")
    @Order(19)
    @WithMockUser(authorities = "SCOPE_ADMIN")
    void finalize_ReturnsBadRequest_WhenServiceThrowsBusinessException(BusinessException exception) throws Exception {
        Long loanId = 1L;
        LoanReturnRequest returnRequest = new LoanReturnRequest(LocalDate.parse("2026-05-31"));
        String request = fileUtils.readResourceFile("loan/patch-request-loan-finalize.json");

        String errorMessage = exception.getMessage();

        doThrow(new BusinessException(errorMessage)).when(service).finalize(loanId, returnRequest);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.patch(URL + "/{id}/return", loanId)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        Exception resolvedException = mvcResult.getResolvedException();

        Assertions.assertThat(resolvedException).isNotNull();

        Assertions.assertThat(resolvedException.getMessage()).isEqualTo(errorMessage);
    }

    @Test
    @DisplayName("PATCH v1/loans/1/cancel returns 204 (no content) and cancels the loan when user is an admin")
    @Order(20)
    @WithMockUser(authorities = "SCOPE_ADMIN")
    void cancel_ReturnsNoContentAndCancelsLoan_WhenUserIsAdmin() throws Exception {
        Long loanId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.patch(URL + "/{id}/cancel", loanId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        verify(service, times(1)).cancel(loanId);
    }

    @Test
    @DisplayName("PATCH v1/loans/1/cancel returns 403 (forbidden) when user is not an admin")
    @Order(21)
    @WithMockUser(authorities = "SCOPE_USER")
    void cancel_ReturnsForbidden_WhenUserIsNotAdmin() throws Exception {
        Long loanId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.patch(URL + "/{id}/cancel", loanId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());

        verify(service, times(0)).cancel(loanId);
    }

    @Test
    @DisplayName("PATCH v1/loans/1/cancel returns 404 (not found) when service throws NotFoundException")
    @Order(22)
    @WithMockUser(authorities = "SCOPE_ADMIN")
    void cancel_ReturnsNotFound_WhenServiceThrowsNotFoundException() throws Exception {
        Long loanId = 1L;
        String errorMessage = "Loan not found.";

        doThrow(new NotFoundException(errorMessage)).when(service).cancel(loanId);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.patch(URL + "/{id}/cancel", loanId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn();

        Exception resolvedException = mvcResult.getResolvedException();

        Assertions.assertThat(resolvedException).isNotNull();

        Assertions.assertThat(resolvedException.getMessage()).contains(errorMessage);
    }

    @Test
    @DisplayName("PATCH v1/loans/1/cancel returns 400 (bad request) when service throws BusinessException")
    @Order(23)
    @WithMockUser(authorities = "SCOPE_ADMIN")
    void cancel_ReturnsBadRequest_WhenServiceThrowsBusinessException() throws Exception {
        Long loanId = 1L;
        String errorMessage = "The Loan has already been finalized";

        doThrow(new BusinessException(errorMessage)).when(service).cancel(loanId);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.patch(URL + "/{id}/cancel", loanId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        Exception resolvedException = mvcResult.getResolvedException();

        Assertions.assertThat(resolvedException).isNotNull();

        Assertions.assertThat(resolvedException.getMessage()).isEqualTo(errorMessage);
    }

    @Test
    @DisplayName("PATCH v1/loans/1/lost returns 204 (no content) and marks the loan as lost when user is an admin")
    @Order(24)
    @WithMockUser(authorities = "SCOPE_ADMIN")
    void lost_ReturnsNoContentAndMarksTheLoanAsLost_WhenUserIsAdmin() throws Exception {
        Long loanId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.patch(URL + "/{id}/lost", loanId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        verify(service, times(1)).lost(loanId);
    }

    @Test
    @DisplayName("PATCH v1/loans/1/lost returns 403 (forbidden) when user is not an admin")
    @Order(25)
    @WithMockUser(authorities = "SCOPE_USER")
    void lost_ReturnsForbidden_WhenUserIsNotAdmin() throws Exception {
        Long loanId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.patch(URL + "/{id}/lost", loanId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());

        verify(service, times(0)).lost(loanId);
    }

    @Test
    @DisplayName("PATCH v1/loans/1/lost returns 404 (not found) when service throws NotFoundException")
    @Order(26)
    @WithMockUser(authorities = "SCOPE_ADMIN")
    void lost_ReturnsNotFound_WhenServiceThrowsNotFoundException() throws Exception {
        Long loanId = 1L;
        String errorMessage = "Loan not found.";

        doThrow(new NotFoundException(errorMessage)).when(service).lost(loanId);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.patch(URL + "/{id}/lost", loanId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn();

        Exception resolvedException = mvcResult.getResolvedException();

        Assertions.assertThat(resolvedException).isNotNull();

        Assertions.assertThat(resolvedException.getMessage()).contains(errorMessage);
    }

    @Test
    @DisplayName("PATCH v1/loans/1/lost returns 400 (bad request) when service throws BusinessException")
    @Order(27)
    @WithMockUser(authorities = "SCOPE_ADMIN")
    void lost_ReturnsBadRequest_WhenServiceThrowsBusinessException() throws Exception {
        Long loanId = 1L;
        String errorMessage = "The Loan has already been finalized";

        doThrow(new BusinessException(errorMessage)).when(service).lost(loanId);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.patch(URL + "/{id}/lost", loanId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        Exception resolvedException = mvcResult.getResolvedException();

        Assertions.assertThat(resolvedException).isNotNull();

        Assertions.assertThat(resolvedException.getMessage()).isEqualTo(errorMessage);
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
                Arguments.of("get-response-loan-empty-params.json",
                        new LoanFilter(null, null, null, null, null, null, null),
                        filteredList),

                Arguments.of("get-response-loan-user-id-renewed-status.json",
                        new LoanFilter(userId, null, null, null, null, null, null),
                        filteredList.stream()
                                .filter(l -> l.getUser().getId().equals(userId))
                                .toList()
                ),

                Arguments.of("get-response-loan-book-id.json",
                        new LoanFilter(null, bookId, null, null, null, null, null),
                        filteredList.stream()
                                .filter(l -> l.getBook().getId().equals(bookId))
                                .toList()
                ),

                Arguments.of("get-response-loan-user-id-renewed-status.json",
                        new LoanFilter(userId, null, renewedStatus, null, null, null, null),
                        filteredList.stream()
                                .filter(l -> l.getUser().getId().equals(userId))
                                .filter(l -> l.getStatus().equals(renewedStatus))
                                .toList()
                ),

                Arguments.of("get-response-loan-invalid-param.json",
                        new LoanFilter(invalidUserId, null, null, null, null, null, null),
                        List.of()
                )
        );
    }

    private static Stream<Arguments> postBadRequestSource() {
        List<String> allRequiredErrors = LoanErrorFactory.allRequiredErrors();

        List<String> allNotValidErrors = LoanErrorFactory.allNotValidErrors();

        return Stream.of(
                Arguments.of("post-request-loan-empty-fields.json", allRequiredErrors),
                Arguments.of("post-request-loan-blank-fields.json", allRequiredErrors),
                Arguments.of("post-request-loan-invalid-fields.json", allNotValidErrors)
        );
    }

    private static Stream<NotFoundException> postNotFoundExceptionSource() {
        return Stream.of(
                new NotFoundException("Book not found."),
                new NotFoundException("User not found.")

        );
    }

    private static Stream<BusinessException> postBusinessExceptionSource() {
        String exampleUserFullName = "Lucas Castro";
        String exampleBookName = "Dom Casmurro";
        return Stream.of(
                new BusinessException("The user '%s' has an active loan.".formatted(exampleUserFullName)),
                new BusinessException("The book '%s' is not available.".formatted(exampleBookName))

        );
    }

    private static Stream<BusinessException> patchFinalizeBusinessExceptionSource(){
        return Stream.of(
                new BusinessException("The Loan has already been finalized"),
                new BusinessException("The return date cannot be before the loan date")

        );
    }
}