package io.github.alineaos.librarymanager.util;

import io.github.alineaos.librarymanager.domain.entity.User;
import io.github.alineaos.librarymanager.domain.enums.UserRole;
import io.github.alineaos.librarymanager.dto.users.UserUpdateRequest;
import io.github.alineaos.librarymanager.dto.users.UserCreateRequest;
import io.github.alineaos.librarymanager.dto.users.UserInfoResponse;
import io.github.alineaos.librarymanager.dto.users.UserCreateResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserFactory {
    public List<User> newUserList() {
        User maria = User.builder()
                .id(1L)
                .fullName("Maria Silva")
                .email("maria.silva@testes.com")
                .cpf("12345678911")
                .birthDate(LocalDate.parse("1989-02-08"))
                .role(UserRole.ADMIN)
                .password("$2a$12$e2OOOy5iYEkbotlXCK1lueTIoyNdj/NT.fbSVJFrvHqYV3oxk9L7q")
                .createdAt(LocalDateTime.parse("2026-04-24T18:00:33"))
                .updatedAt(LocalDateTime.parse("2026-04-24T18:00:33"))
                .build();

        User gabriel = User.builder()
                .id(2L)
                .fullName("Gabriel Souza")
                .email("gabriel.souza@testes.com")
                .cpf("12345678922")
                .birthDate(LocalDate.parse("1992-03-05"))
                .role(UserRole.USER)
                .password("$2a$12$e2OOOy5iYEkbotlXCK1lueTIoyNdj/NT.fbSVJFrvHqYV3oxk9L7q")
                .createdAt(LocalDateTime.parse("2026-04-24T18:01:33"))
                .updatedAt(LocalDateTime.parse("2026-04-24T18:01:33"))
                .build();

        User ana = User.builder()
                .id(3L)
                .fullName("Ana Santana")
                .email("ana.santana@testes.com")
                .cpf("12345678933")
                .birthDate(LocalDate.parse("2001-09-28"))
                .role(UserRole.USER)
                .password("$2a$12$e2OOOy5iYEkbotlXCK1lueTIoyNdj/NT.fbSVJFrvHqYV3oxk9L7q")
                .createdAt(LocalDateTime.parse("2026-04-24T18:02:33"))
                .updatedAt(LocalDateTime.parse("2026-04-24T18:02:33"))
                .build();

        return new ArrayList<>(List.of(maria, gabriel, ana));
    }

    public User newUserSaved() {

        return User.builder()
                .id(99L)
                .fullName("Lucas Castro")
                .email("lucas.castro@testes.com")
                .cpf("12345678944")
                .birthDate(LocalDate.parse("1998-11-25"))
                .role(UserRole.USER)
                .password("$2a$12$e2OOOy5iYEkbotlXCK1lueTIoyNdj/NT.fbSVJFrvHqYV3oxk9L7q")
                .createdAt(LocalDateTime.parse("2026-04-24T18:44:33"))
                .updatedAt(LocalDateTime.parse("2026-04-24T18:44:33"))
                .build();
    }

    public UserInfoResponse newUserInfoResponse() {
        User user = newUserList().getFirst();

        return new UserInfoResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getCpf(),
                user.getBirthDate(),
                user.getRole(),
                user.getCreatedAt(),
                user.getUpdatedAt());
    }

    public UserInfoResponse newUserInfoResponseById(Long id) {
        User user = newUserList().stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Invalid Test: Id Not Found in UserFactory: " + id));

        return new UserInfoResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getCpf(),
                user.getBirthDate(),
                user.getRole(),
                user.getCreatedAt(),
                user.getUpdatedAt());
    }

    public UserCreateRequest newUserCreateRequest() {
        User user = newUserSaved();

        return new UserCreateRequest(
                user.getFullName(),
                user.getEmail(),
                user.getCpf(),
                user.getBirthDate(),
                user.getRole(),
                user.getPassword());
    }

    public UserCreateResponse newUserCreateResponse() {
        User user = newUserSaved();

        return new UserCreateResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt());
    }

    public UserUpdateRequest newUserUpdateRequest() {
        User user = newUserList().getFirst();

        return new UserUpdateRequest(
                "Marta Silva",
                "marta.silva@testes.com",
                user.getBirthDate(),
                user.getRole(),
                user.getPassword());
    }

    public UserUpdateRequest newUserUpdateRequestUpdateRole() {
        User user = newUserList().getLast();

        return new UserUpdateRequest(
                user.getFullName(),
                user.getEmail(),
                user.getBirthDate(),
                UserRole.ADMIN,
                user.getPassword());
    }
}
