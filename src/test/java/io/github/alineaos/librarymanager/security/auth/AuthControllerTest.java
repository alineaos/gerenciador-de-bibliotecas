package io.github.alineaos.librarymanager.security.auth;

import io.github.alineaos.librarymanager.dto.request.UserLoginRequest;
import io.github.alineaos.librarymanager.security.service.TokenService;
import io.github.alineaos.librarymanager.util.AuthFactory;
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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.matchesPattern;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = AuthController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({FileUtils.class, UserFactory.class, AuthFactory.class})
class AuthControllerTest {
    private static final String LOGIN_URL = "/v1/auth/login";
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private AuthenticationManager authenticationManager;
    @MockitoBean
    private TokenService tokenService;
    @Autowired
    private AuthFactory authFactory;
    @Autowired
    private FileUtils fileUtils;

    @Test
    @DisplayName("POST v1/auth/login returns 200 (ok) and access token when the credentials are valid")
    @Order(1)
    void login_ReturnsOkAndAccessToken_WhenCredentialsAreValid() throws Exception {
        String request = fileUtils.readResourceFile("auth/post-login-request-200.json");
        String response = fileUtils.readResourceFile("auth/post-login-response-200.json");

        UserLoginRequest loginRequest = authFactory.newUserLoginRequest();
        String accessToken = "access-token-123";

        Authentication authentication = new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password());

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(tokenService.generateToken(authentication)).thenReturn(accessToken);

        mockMvc.perform(MockMvcRequestBuilders.post(LOGIN_URL)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @DisplayName("POST v1/auth/login returns 401 (unauthorized) when the credentials are invalid")
    @Order(2)
    void login_ReturnsUnauthorized_WhenCredentialsAreInvalid() throws Exception {
        String request = fileUtils.readResourceFile("auth/post-login-request-200.json");
        String errorMessage = "Invalid credentials";
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new BadCredentialsException(errorMessage));

        mockMvc.perform(MockMvcRequestBuilders.post(LOGIN_URL)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(HttpStatus.UNAUTHORIZED.value()))
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").value(matchesPattern("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.*$")));


    }

    @ParameterizedTest(name = "[{index}] {0} ")
    @MethodSource("loginBadRequestSource")
    @DisplayName("POST v1/auth/login returns 400 (bad request) when fields are invalid")
    @Order(3)
    @WithMockUser(authorities = "SCOPE_ADMIN")
    void login_ReturnsBadRequest_WhenFieldsAreInvalid(String fileName, List<String> errors) throws Exception {
        String request = fileUtils.readResourceFile("auth/%s".formatted(fileName));

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(LOGIN_URL)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        Exception resolvedException = mvcResult.getResolvedException();

        Assertions.assertThat(resolvedException).isNotNull();

        Assertions.assertThat(resolvedException.getMessage()).contains(errors);
    }

    private static Stream<Arguments> loginBadRequestSource() {
        List<String> allLoginFieldsErrors = UserErrorFactory.allLoginFieldsErrors();

        return Stream.of(
                Arguments.of("post-login-request-empty-fields-400.json", allLoginFieldsErrors),
                Arguments.of("post-login-request-blank-fields-400.json", allLoginFieldsErrors)
        );
    }
}