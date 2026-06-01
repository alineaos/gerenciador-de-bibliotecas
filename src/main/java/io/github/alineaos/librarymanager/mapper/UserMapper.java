package io.github.alineaos.librarymanager.mapper;

import io.github.alineaos.librarymanager.domain.entity.User;
import io.github.alineaos.librarymanager.dto.users.UserBasicResponse;
import io.github.alineaos.librarymanager.dto.users.UserUpdateRequest;
import io.github.alineaos.librarymanager.dto.users.UserCreateRequest;
import io.github.alineaos.librarymanager.dto.users.UserInfoResponse;
import io.github.alineaos.librarymanager.dto.users.UserCreateResponse;
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
    User toUser(UserCreateRequest request, String encodedPassword);

    UserCreateResponse toUserCreateResponse(User user);

    UserInfoResponse toUserInfoResponse(User user);
    List<UserInfoResponse> toUserInfoResponseList(List<User> users);

    UserBasicResponse toUserBasicResponse(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "password", source = "encodedPassword")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cpf", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void mergeRequestToUser(UserUpdateRequest request, String encodedPassword, @MappingTarget User user);

    @AfterMapping
    default void sanitizeCpf(UserCreateRequest request, @MappingTarget User user) {
        if (request.cpf() != null) {
            user.setCpf(request.cpf().replaceAll("\\D", ""));
        }
    }
}
