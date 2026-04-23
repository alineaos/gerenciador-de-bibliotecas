package io.github.alineaos.librarymanager.service;

import io.github.alineaos.librarymanager.domain.entity.User;
import io.github.alineaos.librarymanager.dto.request.UserPostRequest;
import io.github.alineaos.librarymanager.dto.response.UserPostResponse;
import io.github.alineaos.librarymanager.mapper.UserMapper;
import io.github.alineaos.librarymanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository repository;
    private final UserMapper mapper;

    public UserPostResponse save(UserPostRequest userPostRequest){
        User userToSave = mapper.toUser(userPostRequest);

        User savedUser = repository.save(userToSave);

        return mapper.toPostResponse(savedUser);
    }
}
