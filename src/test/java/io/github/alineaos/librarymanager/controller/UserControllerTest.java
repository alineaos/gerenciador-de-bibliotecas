package io.github.alineaos.librarymanager.controller;

import io.github.alineaos.librarymanager.domain.entity.User;
import io.github.alineaos.librarymanager.domain.enums.UserRole;
import io.github.alineaos.librarymanager.dto.UserFilter;
import io.github.alineaos.librarymanager.dto.request.UserPatchRequest;
import io.github.alineaos.librarymanager.dto.request.UserPostRequest;
import io.github.alineaos.librarymanager.dto.response.UserGetResponse;
import io.github.alineaos.librarymanager.dto.response.UserPostResponse;
import io.github.alineaos.librarymanager.exception.NotFoundException;
import io.github.alineaos.librarymanager.security.config.SecurityConfig;
import io.github.alineaos.librarymanager.service.UserService;
import io.github.alineaos.librarymanager.util.FileUtils;
import io.github.alineaos.librarymanager.util.UserErrorFactory;
import io.github.alineaos.librarymanager.util.UserFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
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
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = UserController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@WithMockUser
@Import({FileUtils.class, UserFactory.class, SecurityConfig.class})
class UserControllerTest {
    private static final String URL = "/v1/users";
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private UserService service;
    @Autowired
    private UserFactory userFactory;
    @Autowired
    private FileUtils fileUtils;

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("userFilterParamsSource")
    @DisplayName("GET v1/users returns 200 (ok) and a list with filtered users when the user is an admin and filters are valid")
    @Order(1)
    @WithMockUser(authorities = "SCOPE_ADMIN")
    void findAll_ReturnsOkAndFilteredUsers_WhenUserIsAdminAndFiltersAreValid(String fileName, UserFilter filter, List<User> expectedUsers) throws Exception {
        String response = fileUtils.readResourceFile(fileName);
        List<UserGetResponse> expectedDtos = expectedUsers.stream()
                .map(u -> new UserGetResponse(u.getId(),
                        u.getFullName(),
                        u.getEmail(),
                        u.getCpf(),
                        u.getBirthDate(),
                        u.getRole(),
                        u.getCreatedAt(),
                        u.getUpdatedAt()))
                .toList();

        when(service.findAll(filter)).thenReturn(expectedDtos);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(URL);

        if (filter.name() != null) requestBuilder.queryParam("name", filter.name());
        if (filter.role() != null) requestBuilder.queryParam("role", filter.role().getRole().toUpperCase());

        mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @DisplayName("GET v1/users returns 403 (forbidden) when user is not an admin")
    @Order(2)
    @WithMockUser(authorities = "SCOPE_USER")
    void findAll_ReturnsForbidden_WhenUserIsNotAdmin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(URL))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @DisplayName("GET v1/users/2 returns 200 (ok) and an user with given id when the user is an admin")
    @Order(3)
    @WithMockUser(authorities = "SCOPE_ADMIN")
    void findById_ReturnsOkAndUserById_WhenUserIsAdmin() throws Exception {
        Long targetUserId = 2L;
        UserGetResponse foundUser = userFactory.newUserGetResponseById(targetUserId);

        when(service.findById(targetUserId)).thenReturn(foundUser);

        String response = fileUtils.readResourceFile("user/get-user-by-id-200.json");

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}", targetUserId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @DisplayName("GET v1/users/2 returns 200 (ok) and an user with given id when the user is the id owner")
    @Order(4)
    @WithMockUser(authorities = "SCOPE_USER")
    void findById_ReturnsOkAndUserById_WhenUserIsIdOwner() throws Exception {
        Long userId = 2L;
        UserGetResponse foundUser = userFactory.newUserGetResponseById(userId);

        when(service.findById(userId)).thenReturn(foundUser);

        String response = fileUtils.readResourceFile("user/get-user-by-id-200.json");

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}", userId)
                        .with(jwt().jwt(builder -> builder.claim("userId", userId))))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @DisplayName("GET v1/users/1 returns 403 (forbidden) when the user is not the id owner")
    @Order(5)
    @WithMockUser(authorities = "SCOPE_USER")
    void findById_ReturnsForbidden_WhenUserIsNotIdOwner() throws Exception {
        Long loggedUserId = 2L;
        Long targetUserId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}", targetUserId)
                        .with(jwt().jwt(builder -> builder.claim("userId", loggedUserId))))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @DisplayName("GET v1/users/999 returns 404 (not found) when the user is not found")
    @Order(6)
    @WithMockUser(authorities = "SCOPE_ADMIN")
    void findById_ReturnsNotFound_WhenUserIsNotFound() throws Exception {
        Long targetUserId = 999L;

        when(service.findById(targetUserId)).thenThrow(new NotFoundException("User not found."));

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}", targetUserId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("User not found."))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").value(matchesPattern("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.*$")));
    }

    @Test
    @DisplayName("POST v1/users returns 201 (created) and creates a user when the user is an admin and fields are valid")
    @Order(7)
    @WithMockUser(authorities = "SCOPE_ADMIN")
    void save_ReturnsCreatedAndCreatesUser_WhenUserIsAdminAndFieldsAreValid() throws Exception {
        String request = fileUtils.readResourceFile("user/post-request-user-201.json");
        String response = fileUtils.readResourceFile("user/post-response-user-201.json");

        UserPostResponse userSavedResponse = userFactory.newUserPostResponse();

        when(service.save(any(UserPostRequest.class))).thenReturn(userSavedResponse);

        mockMvc.perform(MockMvcRequestBuilders.post(URL)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @DisplayName("POST v1/users returns 403 (forbidden) when the user is not an admin")
    @Order(8)
    @WithMockUser(authorities = "SCOPE_USER")
    void save_ReturnsForbidden_WhenUserIsNotAdmin() throws Exception {
        String request = fileUtils.readResourceFile("user/post-request-user-403.json");

        mockMvc.perform(MockMvcRequestBuilders.post(URL)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @ParameterizedTest(name = "[{index}] {0} ")
    @MethodSource("postBadRequestSource")
    @DisplayName("POST v1/users returns 400 (bad request) when fields are invalid")
    @Order(9)
    @WithMockUser(authorities = "SCOPE_ADMIN")
    void save_ReturnsBadRequest_WhenFieldsAreInvalid(String fileName, List<String> errors) throws Exception {
        String request = fileUtils.readResourceFile("user/%s".formatted(fileName));

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
    @DisplayName("PATCH v1/users/1 returns 204 (no content) and updates an user with given id when the user is an admin")
    @Order(10)
    @WithMockUser(authorities = "SCOPE_ADMIN")
    void update_ReturnsNoContentAndUpdatesUserById_WhenUserIsAdmin() throws Exception {
        Long targetUserId = 1L;

        doNothing().when(service).update(eq(targetUserId), any(UserPatchRequest.class));

        String request = fileUtils.readResourceFile("user/patch-request-admin-user-204.json");

        mockMvc.perform(MockMvcRequestBuilders.patch(URL + "/{id}", targetUserId)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @DisplayName("PATCH v1/users/2 returns 204 (no content) and updates an user with given id when the user is the id owner")
    @Order(11)
    @WithMockUser(authorities = "SCOPE_USER")
    void update_ReturnsNoContentAndUpdatesUserById_WhenUserIsIdOwner() throws Exception {
        Long targetUserId = 2L;

        doNothing().when(service).update(eq(targetUserId), any(UserPatchRequest.class));

        String request = fileUtils.readResourceFile("user/patch-request-id-owner-user-204.json");

        mockMvc.perform(MockMvcRequestBuilders.patch(URL + "/{id}", targetUserId)
                        .with(jwt().jwt(builder -> builder.claim("userId", targetUserId)))
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @DisplayName("PATCH v1/users/1 returns 403 (forbidden) when the user is not the id owner")
    @Order(12)
    @WithMockUser(authorities = "SCOPE_USER")
    void update_ReturnsForbidden_WhenUserIsNotIdOwner() throws Exception {
        Long loggedUserId = 2L;
        Long targetUserId = 1L;

        String request = fileUtils.readResourceFile("user/patch-request-not-id-owner-user-403.json");

        mockMvc.perform(MockMvcRequestBuilders.patch(URL + "/{id}", targetUserId)
                        .with(jwt().jwt(builder -> builder.claim("userId", loggedUserId)))
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @DisplayName("PATCH v1/users/999 returns 404 (not found) when the user is not found")
    @Order(13)
    @WithMockUser(authorities = "SCOPE_ADMIN")
    void update_ReturnsNotFound_WhenUserIsNotFound() throws Exception {
        Long targetUserId = 999L;

        String request = fileUtils.readResourceFile("user/patch-request-invalid-id-404.json");

        doThrow(new NotFoundException("User not found.")).when(service).update(eq(targetUserId), any(UserPatchRequest.class));

        mockMvc.perform(MockMvcRequestBuilders.patch(URL + "/{id}", targetUserId)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("User not found."))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").value(matchesPattern("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.*$")));
    }

    @Test
    @DisplayName("PATCH v1/users/2 returns 400 (bad request) when fields are invalid")
    @Order(14)
    @WithMockUser(authorities = "SCOPE_ADMIN")
    void update_ReturnsBadRequest_WhenFieldsAreInvalid() throws Exception {
        Long targetUserId = 2L;

        String request = fileUtils.readResourceFile("user/patch-request-user-invalid-fields-400.json");

        List<String> errors = UserErrorFactory.emailNotValidAndDateNotPastErrors();

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.patch(URL + "/{id}", targetUserId)
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
    @DisplayName("DELETE v1/users/2 returns 204 (no content) and deletes an user with given id when the user is an admin")
    @Order(15)
    @WithMockUser(authorities = "SCOPE_ADMIN")
    void delete_ReturnsNoContentAndDeletesUserById_WhenUserIsAdmin() throws Exception {
        Long targetUserId = 2L;

        doNothing().when(service).delete(targetUserId);

        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/{id}", targetUserId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @DisplayName("DELETE v1/users/2 returns 403 (forbidden) when the user is not an admin")
    @Order(16)
    @WithMockUser(authorities = "SCOPE_USER")
    void delete_ReturnsForbidden_WhenUserIsNotAdmin() throws Exception {
        Long targetUserId = 2L;

        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/{id}", targetUserId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    private static Stream<Arguments> userFilterParamsSource() {
        UserFactory factory = new UserFactory();
        List<User> filteredList = factory.newUserList();
        String name = "Maria";
        return Stream.of(
                Arguments.of("user/get-user-null-name-null-role-200.json",
                        new UserFilter(null, null),
                        filteredList),

                Arguments.of("user/get-user-maria-name-admin-role-200.json",
                        new UserFilter(name, UserRole.ADMIN),
                        filteredList.stream()
                                .filter(u -> u.getFullName().contains(name))
                                .filter(u -> u.getRole() == UserRole.ADMIN)
                                .toList()
                ),

                Arguments.of("user/get-user-null-name-user-role-200.json",
                        new UserFilter(null, UserRole.USER),
                        filteredList.stream()
                                .filter(u -> u.getRole() == UserRole.USER)
                                .toList()
                ),

                Arguments.of("user/get-user-maria-name-null-role-200.json",
                        new UserFilter(name, null),
                        filteredList.stream()
                                .filter(u -> u.getFullName().contains(name))
                                .toList()
                ),

                Arguments.of("user/get-user-invalid-name-null-role-200.json",
                        new UserFilter("InvalidName", null),
                        List.of()
                )
        );
    }

    private static Stream<Arguments> postBadRequestSource() {
        List<String> allRequiredAndNotValidErrors = UserErrorFactory.allRequiredErrors();
        allRequiredAndNotValidErrors.addAll(UserErrorFactory.allNotValidErrors());

        List<String> invalidFieldErrors = UserErrorFactory.emailNotValidAndDateNotPastErrors();

        return Stream.of(
                Arguments.of("post-request-user-empty-fields-400.json", allRequiredAndNotValidErrors),
                Arguments.of("post-request-user-blank-fields-400.json", allRequiredAndNotValidErrors),
                Arguments.of("post-request-user-invalid-fields-400.json", invalidFieldErrors)
        );
    }
}