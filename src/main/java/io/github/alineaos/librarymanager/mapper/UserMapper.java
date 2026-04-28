package io.github.alineaos.librarymanager.mapper;

import io.github.alineaos.librarymanager.domain.entity.User;
import io.github.alineaos.librarymanager.dto.request.UserPatchRequest;
import io.github.alineaos.librarymanager.dto.request.UserPostRequest;
import io.github.alineaos.librarymanager.dto.response.UserGetResponse;
import io.github.alineaos.librarymanager.dto.response.UserPostResponse;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    @Mapping(target = "password", source = "encodedPassword")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toUser(UserPostRequest postRequest, String encodedPassword);

    List<UserGetResponse> toGetResponseList(List<User> userList);
    UserGetResponse toGetResponse(User user);

    UserPostResponse toPostResponse(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "password", source = "encodedPassword")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cpf", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void mergeRequestToUser(UserPatchRequest patchRequest, String encodedPassword, @MappingTarget User user);

    @AfterMapping
    default void sanitizeCpf(UserPostRequest request, @MappingTarget User user) {
        if (request.cpf() != null) {
            user.setCpf(request.cpf().replaceAll("\\D", ""));
        }
    }
}
