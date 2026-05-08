package io.github.alineaos.librarymanager.controller;

import io.github.alineaos.librarymanager.config.UnitTestConfig;
import io.github.alineaos.librarymanager.domain.entity.Genre;
import io.github.alineaos.librarymanager.dto.GenreFilter;
import io.github.alineaos.librarymanager.dto.request.GenrePostRequest;
import io.github.alineaos.librarymanager.dto.request.GenrePutRequest;
import io.github.alineaos.librarymanager.dto.response.GenreGetResponse;
import io.github.alineaos.librarymanager.dto.response.GenrePostResponse;
import io.github.alineaos.librarymanager.exception.NotFoundException;
import io.github.alineaos.librarymanager.security.config.SecurityConfig;
import io.github.alineaos.librarymanager.service.GenreService;
import io.github.alineaos.librarymanager.util.FileUtils;
import io.github.alineaos.librarymanager.util.GenreFactory;
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

@WebMvcTest(controllers = GenreController.class)
@WithMockUser
@Import({FileUtils.class, GenreFactory.class, SecurityConfig.class})
class GenreControllerTest extends UnitTestConfig {
    private static final String URL = "/v1/genres";
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private GenreService service;
    @Autowired
    private GenreFactory genreFactory;
    @Autowired
    private FileUtils fileUtils;

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("genreFilterParamsSource")
    @DisplayName("GET v1/genres returns 200 (ok) and a list with filtered genres when the user is an admin and filters are valid")
    @Order(1)
    @WithMockUser(authorities = "SCOPE_ADMIN")
    void findAll_ReturnsOkAndFilteredGenres_WhenUserIsAdminAndFiltersAreValid(String fileName, GenreFilter filter, List<Genre> expectedGenres) throws Exception {
        String response = fileUtils.readResourceFile("genre/%s".formatted(fileName));

        List<GenreGetResponse> expectedDtos = expectedGenres.stream()
                .map(g -> new GenreGetResponse(
                        g.getId(),
                        g.getName(),
                        g.getCreatedAt(),
                        g.getUpdatedAt()
                ))
                .toList();

        when(service.findAll(filter)).thenReturn(expectedDtos);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(URL);

        if (filter.name() != null) requestBuilder.queryParam("name", filter.name());

        mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @DisplayName("GET v1/genres returns 403 (forbidden) when the user is not an admin")
    @Order(2)
    @WithMockUser(authorities = "SCOPE_USER")
    void findAll_ReturnsForbidden_WhenUserIsNotAdmin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(URL))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @DisplayName("GET v1/genres/1 returns 200 (ok) and a genre with given id when the user is an admin")
    @Order(3)
    @WithMockUser(authorities = "SCOPE_ADMIN")
    void findById_ReturnsOkAndGenreById_WhenUserIsAdmin() throws Exception {
        Long targetGenreId = 1L;
        GenreGetResponse foundGenre = genreFactory.newGenreGetResponseById(targetGenreId);

        when(service.findById(targetGenreId)).thenReturn(foundGenre);

        String response = fileUtils.readResourceFile("genre/get-genre-by-id-200.json");


        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}", targetGenreId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @DisplayName("GET v1/genres/1 returns 403 (forbidden) when the user is not an admin")
    @Order(4)
    @WithMockUser(authorities = "SCOPE_USER")
    void findById_ReturnsForbidden_WhenUserIsNotAdmin() throws Exception {
        Long targetGenreId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}", targetGenreId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @DisplayName("GET v1/genres/999 returns 404 (not found) when the genre is not found")
    @Order(5)
    @WithMockUser(authorities = "SCOPE_ADMIN")
    void findById_ReturnsNotFound_WhenGenreIsNotFound() throws Exception {
        Long targetGenreId = 999L;

        when(service.findById(targetGenreId)).thenThrow(new NotFoundException("Genre not found."));

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}", targetGenreId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("Genre not found."))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").value(matchesPattern("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.*$")));
    }

    @Test
    @DisplayName("POST v1/genres returns 201 (created) and creates a genre when the user is an admin and fields are valid")
    @Order(6)
    @WithMockUser(authorities = "SCOPE_ADMIN")
    void save_ReturnsCreatedAndCreatesGenre_WhenUserIsAdminAndFieldsAreValid() throws Exception {
        String request = fileUtils.readResourceFile("genre/post-request-genre-201.json");
        String response = fileUtils.readResourceFile("genre/post-response-genre-201.json");

        GenrePostResponse genreSavedResponse = genreFactory.newGenrePostResponse();

        when(service.save(any(GenrePostRequest.class))).thenReturn(genreSavedResponse);

        mockMvc.perform(MockMvcRequestBuilders.post(URL)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @DisplayName("POST v1/genres returns 403 (forbidden) when the user is not an admin")
    @Order(7)
    @WithMockUser(authorities = "SCOPE_USER")
    void save_ReturnsForbidden_WhenUserIsNotAdmin() throws Exception {
        String request = fileUtils.readResourceFile("genre/post-request-genre-403.json");

        mockMvc.perform(MockMvcRequestBuilders.post(URL)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @ParameterizedTest(name = "[{index}] {0} ")
    @MethodSource("postBadRequestSource")
    @DisplayName("POST v1/genres returns 400 (bad request) when field is invalid")
    @Order(8)
    @WithMockUser(authorities = "SCOPE_ADMIN")
    void save_ReturnsBadRequest_WhenFieldIsInvalid(String fileName, String error) throws Exception {
        String request = fileUtils.readResourceFile("genre/%s".formatted(fileName));

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(URL)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        Exception resolvedException = mvcResult.getResolvedException();

        Assertions.assertThat(resolvedException).isNotNull();

        Assertions.assertThat(resolvedException.getMessage()).contains(error);
    }

    @Test
    @DisplayName("PUT v1/genre/1 returns 204 (no content) and updates a genre with given id when the user is an admin")
    @Order(9)
    @WithMockUser(authorities = "SCOPE_ADMIN")
    void update_ReturnsNoContentAndUpdatesGenreById_WhenUserIsAdmin() throws Exception {
        Long targetGenreId = 1L;

        doNothing().when(service).update(eq(targetGenreId), any(GenrePutRequest.class));

        String request = fileUtils.readResourceFile("genre/put-request-genre-204.json");

        mockMvc.perform(MockMvcRequestBuilders.put(URL + "/{id}", targetGenreId)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @DisplayName("PUT v1/genres/1 returns 403 (forbidden) when the user is not an admin")
    @Order(10)
    @WithMockUser(authorities = "SCOPE_USER")
    void update_ReturnsForbidden_WhenUserIsNotAdmin() throws Exception {
        Long targetGenreId = 1L;

        doNothing().when(service).update(eq(targetGenreId), any(GenrePutRequest.class));

        String request = fileUtils.readResourceFile("genre/post-request-genre-403.json");

        mockMvc.perform(MockMvcRequestBuilders.put(URL+ "/{id}", targetGenreId)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @DisplayName("PUT v1/genres/999 returns 404 (not found) when the genre is not found")
    @Order(11)
    @WithMockUser(authorities = "SCOPE_ADMIN")
    void update_ReturnsNotFound_WhenGenreIsNotFound() throws Exception {
        Long targetGenreId = 999L;

        String request = fileUtils.readResourceFile("genre/post-request-genre-404.json");

        doThrow(new NotFoundException("Genre not found.")).when(service).update(eq(targetGenreId), any(GenrePutRequest.class));

        mockMvc.perform(MockMvcRequestBuilders.put(URL + "/{id}", targetGenreId)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("Genre not found."))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").value(matchesPattern("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.*$")));
    }

    @ParameterizedTest(name = "[{index}] {0} ")
    @MethodSource("putBadRequestSource")
    @DisplayName("PUT v1/genres/1 returns 400 (bad request) when field is invalid")
    @Order(12)
    @WithMockUser(authorities = "SCOPE_ADMIN")
    void update_ReturnsBadRequest_WhenFieldIsInvalid(String fileName, String error) throws Exception {
        Long targetGenreId = 1L;

        String request = fileUtils.readResourceFile("genre/%s".formatted(fileName));

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put(URL+ "/{id}", targetGenreId)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        Exception resolvedException = mvcResult.getResolvedException();

        Assertions.assertThat(resolvedException).isNotNull();

        Assertions.assertThat(resolvedException.getMessage()).contains(error);
    }

    @Test
    @DisplayName("DELETE v1/genre/1 returns 204 (no content) and updates a genre with given id when the user is an admin")
    @Order(13)
    @WithMockUser(authorities = "SCOPE_ADMIN")
    void delete_ReturnsNoContentAndDeletesGenreById_WhenUserIsAdmin() throws Exception {
        Long targetGenreId = 1L;

        doNothing().when(service).delete(targetGenreId);

        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/{id}", targetGenreId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @DisplayName("DELETE v1/genres/1 returns 403 (forbidden) when the user is not an admin")
    @Order(14)
    @WithMockUser(authorities = "SCOPE_USER")
    void delete_ReturnsForbidden_WhenUserIsNotAdmin() throws Exception {
        Long targetGenreId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.delete(URL+ "/{id}", targetGenreId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    private static Stream<Arguments> genreFilterParamsSource() {
        GenreFactory factory = new GenreFactory();
        List<Genre> filteredList = factory.newGenreList();
        String name = "Romance";
        return Stream.of(
                Arguments.of("get-genre-null-name-200.json",
                        new GenreFilter(null),
                        filteredList
                ),

                Arguments.of("get-genre-romance-name-200.json",
                        new GenreFilter(name),
                        filteredList.stream()
                                .filter(g -> g.getName().equalsIgnoreCase(name))
                                .toList()
                ),

                Arguments.of("get-genre-invalid-name-200.json",
                        new GenreFilter("InvalidName"),
                        List.of()
                )
        );
    }

    private static Stream<Arguments> postBadRequestSource() {
        String nameError = nameRequiredError();

        return Stream.of(
                Arguments.of("post-request-genre-empty-fields-400.json", nameError),
                Arguments.of("post-request-genre-blank-fields-400.json", nameError)
        );
    }

    private static Stream<Arguments> putBadRequestSource() {
        String nameError = nameRequiredError();

        return Stream.of(
                Arguments.of("put-request-genre-empty-fields-400.json", nameError),
                Arguments.of("put-request-genre-blank-fields-400.json", nameError)
        );
    }

    private static String nameRequiredError(){
        return "The field 'name' is required.";
    }

}