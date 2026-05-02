package io.github.alineaos.librarymanager.service;

import io.github.alineaos.librarymanager.domain.entity.User;
import io.github.alineaos.librarymanager.domain.enums.UserRole;
import io.github.alineaos.librarymanager.dto.UserFilter;
import io.github.alineaos.librarymanager.dto.request.UserPatchRequest;
import io.github.alineaos.librarymanager.dto.request.UserPostRequest;
import io.github.alineaos.librarymanager.dto.response.UserGetResponse;
import io.github.alineaos.librarymanager.dto.response.UserPostResponse;
import io.github.alineaos.librarymanager.exception.BusinessException;
import io.github.alineaos.librarymanager.mapper.UserMapper;
import io.github.alineaos.librarymanager.repository.UserRepository;
import io.github.alineaos.librarymanager.util.UserFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserServiceTest {
    @InjectMocks
    private UserService service;
    @Mock
    private UserRepository repository;
    @Spy
    private UserMapper mapper = Mappers.getMapper(UserMapper.class);
    @Mock
    private PasswordEncoder passwordEncoder;
    private final UserFactory userFactory = new UserFactory();
    private List<User> userList;

    @BeforeEach
    void init() {
        userList = userFactory.newUserList();
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("userFilterSource")
    @DisplayName("findAll returns a list with filtered users when the filter is valid")
    @Order(1)
    void findAll_ReturnsFilteredUsers_WhenFilterIsValid(UserFilter filter, List<User> expectedUsers) {
        when(repository.findAll(ArgumentMatchers.<Specification<User>>any())).thenReturn(expectedUsers);
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

        List<UserGetResponse> result = service.findAll(filter);

        Assertions.assertThat(result).isNotNull().hasSize(expectedDtos.size());
    }

    @Test
    @DisplayName("findById return a user with given id")
    @Order(2)
    void findById_ReturnsUserById_WhenSuccessful() {
        User expectedUser = userList.getFirst();
        UserGetResponse expectedDto = userFactory.newUserGetResponse();

        when(repository.findById(expectedUser.getId())).thenReturn(Optional.of(expectedUser));

        UserGetResponse result = service.findById(expectedUser.getId());

        Assertions.assertThat(result).isEqualTo(expectedDto);
    }

    @Test
    @DisplayName("findById throws NotFoundException when user is not found")
    @Order(3)
    void findById_ThrowsNotFoundException_WhenUserIsNotFound() {
        User expectedUser = userList.getFirst();

        when(repository.findById(expectedUser.getId())).thenReturn(Optional.empty());

        Assertions.assertThatException()
                .isThrownBy(() -> service.findById(expectedUser.getId()))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    @DisplayName("save creates a user")
    @Order(4)
    void save_CreatesUser_WhenSuccessful() {
        User userSaved = userFactory.newUserSaved();

        when(repository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(repository.findByCpf(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn(userSaved.getPassword());
        when(repository.save(any(User.class))).thenReturn(userSaved);

        UserPostResponse result = service.save(userFactory.newUserPostRequest());

        Assertions.assertThat(result.id()).isEqualTo(userSaved.getId());
    }

    @ParameterizedTest(name = "[{index}] {0} already exists")
    @MethodSource("uniqueAttributeSource")
    @DisplayName("save throws BusinessException when unique attribute already exists")
    @Order(5)
    void save_ThrowsBusinessException_WhenEmailAlreadyExists(String field) {
        User userSaved = userFactory.newUserSaved();
        UserPostRequest expectedDto = userFactory.newUserPostRequest();

        if (field.equalsIgnoreCase("Email")) {
            when(repository.findByEmail(expectedDto.email())).thenReturn(Optional.of(userSaved));
        }

        if (field.equalsIgnoreCase("CPF")) {
            when(repository.findByCpf(expectedDto.cpf())).thenReturn(Optional.of(userSaved));
        }

        Assertions.assertThatException()
                .isThrownBy(() -> service.save(expectedDto))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("update updates a user")
    @Order(6)
    void update_UpdatesUser_WhenSuccessful() {
        User userToUpdate = userList.getFirst();
        UserPatchRequest expectedDto = userFactory.newUserPatchRequest();

        Long id = userToUpdate.getId();
        String email = expectedDto.email();

        when(repository.findById(id)).thenReturn(Optional.of(userToUpdate));
        when(repository.findByEmailAndIdNot(email, id)).thenReturn(Optional.empty());
        when(repository.save(userToUpdate)).thenReturn(userToUpdate);

        service.update(id, expectedDto);

        Assertions.assertThat(userToUpdate.getFullName()).isEqualTo(expectedDto.fullName());
        Assertions.assertThat(userToUpdate.getEmail()).isEqualTo(expectedDto.email());
        Assertions.assertThat(userToUpdate.getPassword()).isEqualTo(expectedDto.password());
    }

    @Test
    @DisplayName("update updates a user password when password is not null")
    @Order(7)
    void update_UpdatesUserPassword_WhenPasswordIsNotNull() {
        User userToUpdate = userList.getFirst();
        UserPatchRequest expectedDto = new UserPatchRequest(null, null, null, null, "newPassword");

        Long id = userToUpdate.getId();
        String encodedPassword = "encodedPassword";

        when(repository.findById(id)).thenReturn(Optional.of(userToUpdate));
        when(passwordEncoder.encode(expectedDto.password())).thenReturn(encodedPassword);
        when(repository.save(any())).thenReturn(userToUpdate);

        service.update(id, expectedDto);

        Assertions.assertThat(userToUpdate.getPassword()).isEqualTo(encodedPassword);
    }

    @ParameterizedTest(name = "[{index}] password = \"{0}\"")
    @MethodSource("invalidPasswordSource")
    @DisplayName("update must keep the old password when password is invalid")
    @Order(8)
    void update_MustKeepOldPassword_WhenPasswordIsInvalid(String invalidPassword) {
        User userToUpdate = userList.getFirst();
        UserPatchRequest expectedDto = new UserPatchRequest(null, null, null, null, invalidPassword);

        Long id = userToUpdate.getId();
        String originalPassword = userToUpdate.getPassword();

        when(repository.findById(id)).thenReturn(Optional.of(userToUpdate));
        when(repository.save(userToUpdate)).thenReturn(userToUpdate);

        service.update(id, expectedDto);

        Assertions.assertThat(userToUpdate.getPassword()).isEqualTo(originalPassword);
    }

    @Test
    @DisplayName("update throws NotFoundException when user is not found")
    @Order(9)
    void update_ThrowsNotFoundException_WhenUserIsNotFound() {
        User userToUpdate = userList.getFirst();
        UserPatchRequest expectedDto = userFactory.newUserPatchRequest();

        Long id = userToUpdate.getId();

        when(repository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThatException()
                .isThrownBy(() -> service.update(id, expectedDto))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    @DisplayName("update throws BusinessException when email already exists")
    @Order(10)
    void update_ThrowsBusinessException_WhenEmailAlreadyExists() {
        User userToUpdate = userList.getFirst();
        UserPatchRequest expectedDto = userFactory.newUserPatchRequest();

        Long id = userToUpdate.getId();
        String email = expectedDto.email();

        User userFromDb = userFactory.newUserSaved();
        when(repository.findById(id)).thenReturn(Optional.of(userToUpdate));
        when(repository.findByEmailAndIdNot(email, id)).thenReturn(Optional.of(userFromDb));

        Assertions.assertThatException()
                .isThrownBy(() -> service.update(id, expectedDto))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("delete removes a user")
    @Order(10)
    void delete_Removes_WhenSuccessful() {
        User userToDelete = userList.getFirst();

        when(repository.findById(userToDelete.getId())).thenReturn(Optional.of(userToDelete));
        doNothing().when(repository).delete(userToDelete);

        Assertions.assertThatNoException()
                .isThrownBy(() -> service.delete(userToDelete.getId()));
    }

    @Test
    @DisplayName("delete throws NotFoundException when user is not found")
    @Order(11)
    void delete_ThrowsNotFoundException_WhenUserIsNotFound() {
        User userToDelete = userList.getFirst();

        when(repository.findById(userToDelete.getId())).thenReturn(Optional.empty());

        Assertions.assertThatException()
                .isThrownBy(() -> service.delete(userToDelete.getId()))
                .isInstanceOf(ResponseStatusException.class);
    }

    private static Stream<Arguments> userFilterSource() {
        UserFactory factory = new UserFactory();
        List<User> filteredList = factory.newUserList();
        String name = "Maria";
        return Stream.of(
                Arguments.of(new UserFilter(null, null),
                        filteredList),

                Arguments.of(new UserFilter(name, UserRole.ADMIN),
                        filteredList.stream()
                                .filter(u -> u.getFullName().contains(name))
                                .filter(u -> u.getRole() == UserRole.ADMIN)
                                .toList()
                ),

                Arguments.of(new UserFilter(null, UserRole.USER),
                        filteredList.stream()
                                .filter(u -> u.getRole() == UserRole.USER)
                                .toList()
                ),

                Arguments.of(new UserFilter(name, null),
                        filteredList.stream()
                                .filter(u -> u.getFullName().contains(name))
                                .toList()
                ),

                Arguments.of(new UserFilter("InvalidName", null),
                        List.of()
                )
        );
    }

    private static Stream<String> uniqueAttributeSource() {
        return Stream.of("Email", "CPF");
    }

    private static Stream<String> invalidPasswordSource() {
        return Stream.of(null, "", "   ");
    }
}