package io.github.alineaos.librarymanager.service;

import io.github.alineaos.librarymanager.domain.entity.User;
import io.github.alineaos.librarymanager.domain.enums.UserRole;
import io.github.alineaos.librarymanager.dto.users.UserFilter;
import io.github.alineaos.librarymanager.dto.users.UserUpdateRequest;
import io.github.alineaos.librarymanager.dto.users.UserCreateRequest;
import io.github.alineaos.librarymanager.dto.users.UserInfoResponse;
import io.github.alineaos.librarymanager.dto.users.UserCreateResponse;
import io.github.alineaos.librarymanager.exception.AccessDeniedException;
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

    public List<UserInfoResponse> findAll(UserFilter filter) {
        List<User> users = repository.findAll(
                UserSpecification.hasName(filter.name())
                        .and(UserSpecification.hasUserRole(filter.role()))
        );

        return mapper.toUserInfoResponseList(users);
    }

    public UserInfoResponse findById(Long id) {
        User user = findByIdOrThrowNotFound(id);

        return mapper.toUserInfoResponse(user);
    }

    public UserCreateResponse save(@Valid UserCreateRequest request) {
        assertEmailDoesNotExists(request.email());
        assertCpfDoesNotExists(request.cpf());

        String encodedPassword = passwordEncoder.encode(request.password());
        User userToSave = mapper.toUser(request, encodedPassword);

        User savedUser = repository.save(userToSave);

        return mapper.toUserCreateResponse(savedUser);
    }

    public void update(Long id, @Valid UserUpdateRequest request) {
        User userToUpdate = findByIdOrThrowNotFound(id);

        if (request.role() != null && userToUpdate.getRole() != UserRole.ADMIN){
            throw new AccessDeniedException("Access Denied: Only Admins can update the user role.");
        }

        if (request.email() != null) {
            assertEmailDoesNotExists(request.email(), id);
        }

        String encodedPassword = (request.password() != null && !request.password().isBlank())
                ? passwordEncoder.encode(request.password())
                : userToUpdate.getPassword();

        mapper.mergeRequestToUser(request, encodedPassword, userToUpdate);

        repository.save(userToUpdate);
    }

    public void delete(Long id) {
        User userToDelete = findByIdOrThrowNotFound(id);

        repository.delete(userToDelete);
    }

    public User getUserByIdOrThrowNotFound(Long id){
        return findByIdOrThrowNotFound(id);
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
