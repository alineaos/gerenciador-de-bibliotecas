package io.github.alineaos.librarymanager.mapper;

import io.github.alineaos.librarymanager.domain.entity.User;
import io.github.alineaos.librarymanager.dto.request.UserPatchRequest;
import io.github.alineaos.librarymanager.dto.request.UserPostRequest;
import io.github.alineaos.librarymanager.dto.response.UserGetResponse;
import io.github.alineaos.librarymanager.dto.response.UserPostResponse;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    User toUser(UserPostRequest postRequest);

    List<UserGetResponse> toGetResponseList(List<User> userList);
    UserGetResponse toGetResponse(User user);

    UserPostResponse toPostResponse(User user);

    void mergeRequestToUser(UserPatchRequest patchRequest, @MappingTarget User user);

    @AfterMapping
    default void sanitizeCpf(UserPostRequest request, @MappingTarget User user) {
        if (request.cpf() != null) {
            user.setCpf(request.cpf().replaceAll("\\D", ""));
        }
    }
}
