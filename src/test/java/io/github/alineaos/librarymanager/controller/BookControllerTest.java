package io.github.alineaos.librarymanager.controller;

import io.github.alineaos.librarymanager.config.UnitTestConfig;
import io.github.alineaos.librarymanager.domain.entity.Book;
import io.github.alineaos.librarymanager.dto.BookFilter;
import io.github.alineaos.librarymanager.dto.request.BookPatchRequest;
import io.github.alineaos.librarymanager.dto.request.BookPostRequest;
import io.github.alineaos.librarymanager.dto.response.BookGetResponse;
import io.github.alineaos.librarymanager.dto.response.BookPostResponse;
import io.github.alineaos.librarymanager.exception.NotFoundException;
import io.github.alineaos.librarymanager.security.config.SecurityConfig;
import io.github.alineaos.librarymanager.service.BookService;
import io.github.alineaos.librarymanager.util.BookErrorFactory;
import io.github.alineaos.librarymanager.util.BookFactory;
import io.github.alineaos.librarymanager.util.FileUtils;
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

import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.matchesPattern;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = BookController.class)
@WithMockUser
@Import({FileUtils.class, BookFactory.class, SecurityConfig.class})
class BookControllerTest extends UnitTestConfig {
    private static final String URL = "/v1/books";
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private BookService service;
    @Autowired
    private BookFactory bookFactory;
    @Autowired
    private FileUtils fileUtils;

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("bookFilterParamsSource")
    @DisplayName("GET v1/books returns 200 (ok) and a list with filtered books when user is authenticated and filters are valid")
    @Order(1)
    @WithMockUser(authorities = "SCOPE_USER")
    void findAll_ReturnsOkAndFilteredBooks_WhenUserIsAuthenticatedAndFiltersAreValid(String fileName, BookFilter filter, List<Book> expectedBooks) throws Exception {
        String response = fileUtils.readResourceFile("book/%s".formatted(fileName));
        List<BookGetResponse> expectedDtos = expectedBooks.stream()
                .map(b -> new BookGetResponse(b.getId(),
                        b.getTitle(),
                        b.getAuthor(),
                        b.getPublisher(),
                        b.getYear(),
                        b.getEdition(),
                        b.getIsbn(),
                        b.getCreatedAt(),
                        b.getUpdatedAt()))
                .toList();

        when(service.findAll(filter)).thenReturn(expectedDtos);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(URL);

        if (filter.title() != null) requestBuilder.queryParam("title", filter.title());
        if (filter.author() != null) requestBuilder.queryParam("author", filter.author());
        if (filter.publisher() != null) requestBuilder.queryParam("publisher", filter.publisher());

        mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @DisplayName("GET v1/books/2 returns 200 (ok) and an book with given id when the user is authenticated")
    @Order(2)
    @WithMockUser(authorities = "SCOPE_USER")
    void findById_ReturnsOkAndBookById_UserIsAuthenticated() throws Exception {
        Long targetBookId = 2L;
        BookGetResponse foundBook = bookFactory.newBookGetResponseById(targetBookId);

        when(service.findById(targetBookId)).thenReturn(foundBook);

        String response = fileUtils.readResourceFile("book/get-book-by-id-200.json");

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}", targetBookId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @DisplayName("GET v1/books/999 returns 404 (not found) when the book is not found")
    @Order(3)
    @WithMockUser(authorities = "SCOPE_USER")
    void findById_ReturnsNotFound_WhenBookIsNotFound() throws Exception {
        Long targetBookId = 999L;

        when(service.findById(targetBookId)).thenThrow(new NotFoundException("Book not found."));

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}", targetBookId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("Book not found."))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").value(matchesPattern("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.*$")));
    }

    @Test
    @DisplayName("POST v1/books returns 201 (created) and creates a book when the user is an admin and fields are valid")
    @Order(4)
    @WithMockUser(authorities = "SCOPE_ADMIN")
    void save_ReturnsCreatedAndCreatesBook_WhenUserIsAdminAndFieldsAreValid() throws Exception {
        String request = fileUtils.readResourceFile("book/post-request-book-201.json");
        String response = fileUtils.readResourceFile("book/post-response-book-201.json");

        BookPostResponse bookSavedResponse = bookFactory.newBookPostResponse();

        when(service.save(any(BookPostRequest.class))).thenReturn(bookSavedResponse);

        mockMvc.perform(MockMvcRequestBuilders.post(URL)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @DisplayName("POST v1/books returns 403 (forbidden) when the book is not an admin")
    @Order(5)
    @WithMockUser(authorities = "SCOPE_USER")
    void save_ReturnsForbidden_WhenUserIsNotAdmin() throws Exception {
        String request = fileUtils.readResourceFile("book/post-request-book-403.json");

        mockMvc.perform(MockMvcRequestBuilders.post(URL)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @ParameterizedTest(name = "[{index}] {0} ")
    @MethodSource("postBadRequestSource")
    @DisplayName("POST v1/books returns 400 (bad request) when fields are invalid")
    @Order(6)
    @WithMockUser(authorities = "SCOPE_ADMIN")
    void save_ReturnsBadRequest_WhenFieldsAreInvalid(String fileName, List<String> errors) throws Exception {
        String request = fileUtils.readResourceFile("book/%s".formatted(fileName));

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

    @Test
    @DisplayName("PATCH v1/books/1 returns 204 (no content) and updates an book with given id when the user is an admin")
    @Order(7)
    @WithMockUser(authorities = "SCOPE_ADMIN")
    void update_ReturnsNoContentAndUpdatesBookById_WhenUserIsAdmin() throws Exception {
        Long targetBookId = 1L;

        doNothing().when(service).update(eq(targetBookId), any(BookPatchRequest.class));

        String request = fileUtils.readResourceFile("book/patch-request-admin-book-204.json");

        mockMvc.perform(MockMvcRequestBuilders.patch(URL + "/{id}", targetBookId)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @DisplayName("PATCH v1/books/1 returns 403 (forbidden) when the user is not an admin")
    @Order(8)
    @WithMockUser(authorities = "SCOPE_USER")
    void update_ReturnsForbidden_WhenUserIsNotAdmin() throws Exception {
        Long targetBookId = 1L;
        String request = fileUtils.readResourceFile("book/patch-request-not-admin-book-403.json");

        mockMvc.perform(MockMvcRequestBuilders.patch(URL + "/{id}", targetBookId)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @DisplayName("PATCH v1/books/999 returns 404 (not found) when the book is not found")
    @Order(9)
    @WithMockUser(authorities = "SCOPE_ADMIN")
    void update_ReturnsNotFound_WhenBookIsNotFound() throws Exception {
        Long targetBookId = 999L;

        String request = fileUtils.readResourceFile("book/patch-request-invalid-id-404.json");

        doThrow(new NotFoundException("Book not found.")).when(service).update(eq(targetBookId), any(BookPatchRequest.class));

        mockMvc.perform(MockMvcRequestBuilders.patch(URL + "/{id}", targetBookId)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("Book not found."))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").value(matchesPattern("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.*$")));
    }

    @Test
    @DisplayName("PATCH v1/books/2 returns 400 (bad request) when fields are invalid")
    @Order(10)
    @WithMockUser(authorities = "SCOPE_ADMIN")
    void update_ReturnsBadRequest_WhenFieldsAreInvalid() throws Exception {
        Long targetBookId = 2L;

        String request = fileUtils.readResourceFile("book/patch-request-book-invalid-fields-400.json");

        List<String> errors = BookErrorFactory.notValidAndYearNotFutureErrors();

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.patch(URL + "/{id}", targetBookId)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        Exception resolvedException = mvcResult.getResolvedException();

        Assertions.assertThat(resolvedException).isNotNull();

        Assertions.assertThat(resolvedException.getMessage()).contains(errors);
    }

    @Test
    @DisplayName("DELETE v1/books/2 returns 204 (no content) and deletes an book with given id when the user is an admin")
    @Order(11)
    @WithMockUser(authorities = "SCOPE_ADMIN")
    void delete_ReturnsNoContentAndDeletesBookById_WhenUserIsAdmin() throws Exception {
        Long targetBookId = 2L;

        doNothing().when(service).delete(targetBookId);

        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/{id}", targetBookId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @DisplayName("DELETE v1/books/2 returns 403 (forbidden) when the user is not an admin")
    @Order(12)
    @WithMockUser(authorities = "SCOPE_USER")
    void delete_ReturnsForbidden_WhenUserIsNotAdmin() throws Exception {
        Long targetBookId = 2L;

        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/{id}", targetBookId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    private static Stream<Arguments> bookFilterParamsSource() {
        BookFactory factory = new BookFactory();
        List<Book> filteredList = factory.newBookList();
        String title = "estrela";
        String publisher = "Rocco";
        return Stream.of(
                Arguments.of("get-book-empty-params-200.json",
                        new BookFilter(null, null, null, null, null, null),
                        filteredList),

                Arguments.of("get-book-estrela-title-200.json",
                        new BookFilter(title, null, null, null, null, null),
                        filteredList.stream()
                                .filter(b -> b.getTitle().contains(title))
                                .toList()
                ),

                Arguments.of("get-book-rocco-publisher-200.json",
                        new BookFilter(null, null, publisher, null, null, null),
                        filteredList.stream()
                                .filter(b -> b.getPublisher().contains(publisher))
                                .toList()
                ),

                Arguments.of("get-book-estrela-title-rocco-publisher-200.json",
                        new BookFilter(title, null, publisher, null, null, null),
                        filteredList.stream()
                                .filter(u -> u.getTitle().contains(title))
                                .filter(b -> b.getPublisher().contains(publisher))
                                .toList()
                ),

                Arguments.of("get-book-invalid-param-200.json",
                        new BookFilter("InvalidTitle", null, null, null, null, null),
                        List.of()
                )
        );
    }

    private static Stream<Arguments> postBadRequestSource() {
        List<String> allRequiredAndNotValidErrors = BookErrorFactory.allRequiredErrors();
        allRequiredAndNotValidErrors.addAll(BookErrorFactory.allNotValidErrors());

        List<String> invalidFieldErrors = BookErrorFactory.notValidAndYearNotFutureErrors();

        return Stream.of(
                Arguments.of("post-request-book-empty-fields-400.json", allRequiredAndNotValidErrors),
                Arguments.of("post-request-book-blank-fields-400.json", allRequiredAndNotValidErrors),
                Arguments.of("post-request-book-invalid-fields-400.json", invalidFieldErrors)
        );
    }
}