package io.github.alineaos.librarymanager.service;

import io.github.alineaos.librarymanager.domain.entity.User;
import io.github.alineaos.librarymanager.dto.UserFilter;
import io.github.alineaos.librarymanager.dto.request.UserPostRequest;
import io.github.alineaos.librarymanager.dto.response.UserGetResponse;
import io.github.alineaos.librarymanager.dto.response.UserPostResponse;
import io.github.alineaos.librarymanager.exception.BussinessException;
import io.github.alineaos.librarymanager.mapper.UserMapper;
import io.github.alineaos.librarymanager.repository.UserRepository;
import io.github.alineaos.librarymanager.repository.specification.UserSpecification;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@RequiredArgsConstructor
@Validated
@Service
public class UserService {
    private final UserRepository repository;
    private final UserMapper mapper;

    public List<UserGetResponse> findAll(UserFilter filter) {
        List<User> users = repository.findAll(
                UserSpecification.hasName(filter.name())
                        .and(UserSpecification.hasUserRole(filter.role()))
        );

        return mapper.toGetResponseList(users);
    }

    public UserPostResponse save(@Valid UserPostRequest userPostRequest) {
        User userToSave = mapper.toUser(userPostRequest);

        assertEmailDoesNotExists(userToSave.getEmail());
        assertCpfDoesNotExists(userToSave.getCpf());

        User savedUser = repository.save(userToSave);

        return mapper.toPostResponse(savedUser);
    }

    private void assertEmailDoesNotExists(String email) {
        repository.findByEmail(email).ifPresent(this::throwEmailExistsException);
    }

    private void assertCpfDoesNotExists(String cpf) {
        repository.findByCpf(cpf).ifPresent(this::throwCpfExistsException);
    }

    private void throwEmailExistsException(User user) {
        throw new BussinessException("E-mail '%s' already exists".formatted(user.getEmail()));
    }

    private void throwCpfExistsException(User user) {
        throw new BussinessException("CPF '%s' already exists".formatted(user.getCpf()));
    }
}
