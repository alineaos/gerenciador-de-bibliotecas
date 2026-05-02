package io.github.alineaos.librarymanager.util;

import io.github.alineaos.librarymanager.domain.entity.User;
import io.github.alineaos.librarymanager.domain.enums.UserRole;
import io.github.alineaos.librarymanager.dto.request.UserPatchRequest;
import io.github.alineaos.librarymanager.dto.request.UserPostRequest;
import io.github.alineaos.librarymanager.dto.response.UserGetResponse;

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
                .password("$2a$12$TpZk9KErD6NhBv/rxeG7NOt6jI8Km0J3JicF6.rvp38qhsfhlIHmW")
                .createdAt(LocalDateTime.parse("2026-04-24T18:00:33"))
                .updatedAt(LocalDateTime.parse("2026-04-24T18:00:33"))
                .build();

        User gabriel = User.builder()
                .id(1L)
                .fullName("Gabriel Souza")
                .email("gabriel.Souza@testes.com")
                .cpf("12345678922")
                .birthDate(LocalDate.parse("1992-03-05"))
                .role(UserRole.USER)
                .password("$2a$12$TpZk9KErD6NhBv/rxeG7NOt6jI8Km0J3JicF6.rvp38qhsfhlIHmW")
                .createdAt(LocalDateTime.parse("2026-04-24T18:01:33"))
                .updatedAt(LocalDateTime.parse("2026-04-24T18:01:33"))
                .build();

        User ana = User.builder()
                .id(1L)
                .fullName("Ana Santana")
                .email("ana.santana@testes.com")
                .cpf("12345678933")
                .birthDate(LocalDate.parse("2001-09-28"))
                .role(UserRole.USER)
                .password("$2a$12$TpZk9KErD6NhBv/rxeG7NOt6jI8Km0J3JicF6.rvp38qhsfhlIHmW")
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
                .password("$2a$12$TpZk9KErD6NhBv/rxeG7NOt6jI8Km0J3JicF6.rvp38qhsfhlIHmW")
                .createdAt(LocalDateTime.parse("2026-04-24T18:44:33"))
                .updatedAt(LocalDateTime.parse("2026-04-24T18:44:33"))
                .build();
    }

    public UserGetResponse newUserGetResponse() {
        User user = newUserList().getFirst();

        return new UserGetResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getCpf(),
                user.getBirthDate(),
                user.getRole(),
                user.getCreatedAt(),
                user.getUpdatedAt());
    }

    public UserPostRequest newUserPostRequest() {
        User user = newUserSaved();

        return new UserPostRequest(
                user.getFullName(),
                user.getEmail(),
                user.getCpf(),
                user.getBirthDate(),
                user.getRole(),
                user.getPassword());
    }

    public UserPatchRequest newUserPatchRequest() {
        User user = newUserList().getFirst();

        return new UserPatchRequest(
                "Marta Silva",
                "marta.silva@testes.com",
                user.getBirthDate(),
                user.getRole(),
                user.getPassword());
    }

}
