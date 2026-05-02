package io.github.alineaos.librarymanager.service;

import io.github.alineaos.librarymanager.domain.entity.User;
import io.github.alineaos.librarymanager.dto.UserFilter;
import io.github.alineaos.librarymanager.dto.request.UserPatchRequest;
import io.github.alineaos.librarymanager.dto.request.UserPostRequest;
import io.github.alineaos.librarymanager.dto.response.UserGetResponse;
import io.github.alineaos.librarymanager.dto.response.UserPostResponse;
import io.github.alineaos.librarymanager.exception.BusinessException;
import io.github.alineaos.librarymanager.exception.NotFoundException;
import io.github.alineaos.librarymanager.mapper.UserMapper;
import io.github.alineaos.librarymanager.repository.UserRepository;
import io.github.alineaos.librarymanager.repository.specification.UserSpecification;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@RequiredArgsConstructor
@Validated
@Service
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository repository;
    private final UserMapper mapper;

    public List<UserGetResponse> findAll(UserFilter filter) {
        List<User> users = repository.findAll(
                UserSpecification.hasName(filter.name())
                        .and(UserSpecification.hasUserRole(filter.role()))
        );

        return mapper.toGetResponseList(users);
    }

    public UserGetResponse findById(Long id) {
        User user = findByIdOrThrowNotFound(id);

        return mapper.toGetResponse(user);
    }

    public UserPostResponse save(@Valid UserPostRequest userPostRequest) {
        assertEmailDoesNotExists(userPostRequest.email());
        assertCpfDoesNotExists(userPostRequest.cpf());

        String encodedPassword = passwordEncoder.encode(userPostRequest.password());
        User userToSave = mapper.toUser(userPostRequest, encodedPassword);

        User savedUser = repository.save(userToSave);

        return mapper.toPostResponse(savedUser);
    }

    public void update(Long id, @Valid UserPatchRequest userPatchRequest) {
        User userToUpdate = findByIdOrThrowNotFound(id);

        if (userPatchRequest.email() != null) {
            assertEmailDoesNotExists(userPatchRequest.email(), id);
        }


        String encodedPassword = (userPatchRequest.password() != null && !userPatchRequest.password().isBlank())
                ? passwordEncoder.encode(userPatchRequest.password())
                : userToUpdate.getPassword();

        mapper.mergeRequestToUser(userPatchRequest, encodedPassword, userToUpdate);

        repository.save(userToUpdate);
    }

    public void delete(Long id) {
        User userToDelete = findByIdOrThrowNotFound(id);

        repository.delete(userToDelete);
    }

    private void assertEmailDoesNotExists(String email) {
        repository.findByEmail(email).ifPresent(this::throwEmailExistsException);
    }

    private void assertCpfDoesNotExists(String cpf) {
        repository.findByCpf(cpf).ifPresent(this::throwCpfExistsException);
    }

    private void assertEmailDoesNotExists(String email, Long id) {
        repository.findByEmailAndIdNot(email, id).ifPresent(this::throwEmailExistsException);
    }

    private void throwEmailExistsException(User user) {
        throw new BusinessException("E-mail '%s' already exists".formatted(user.getEmail()));
    }

    private void throwCpfExistsException(User user) {
        throw new BusinessException("CPF '%s' already exists".formatted(user.getCpf()));
    }

    private User findByIdOrThrowNotFound(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new NotFoundException("User not found."));
    }
}
