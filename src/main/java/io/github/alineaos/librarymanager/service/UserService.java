package io.github.alineaos.librarymanager.service;

import io.github.alineaos.librarymanager.domain.entity.User;
import io.github.alineaos.librarymanager.dto.UserFilter;
import io.github.alineaos.librarymanager.dto.request.UserPostRequest;
import io.github.alineaos.librarymanager.dto.response.UserGetResponse;
import io.github.alineaos.librarymanager.dto.response.UserPostResponse;
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

    public List<UserGetResponse> findAll(UserFilter filter){
        List<User> users = repository.findAll(
                UserSpecification.hasName(filter.name())
                        .and(UserSpecification.hasUserRole(filter.role()))
        );

        return mapper.toGetResponseList(users);
    }

    public UserPostResponse save(@Valid UserPostRequest userPostRequest){
        User userToSave = mapper.toUser(userPostRequest);

        User savedUser = repository.save(userToSave);

        return mapper.toPostResponse(savedUser);
    }
}
