package io.github.alineaos.librarymanager.util;

import io.github.alineaos.librarymanager.domain.entity.User;
import io.github.alineaos.librarymanager.dto.request.UserLoginRequest;

public class AuthFactory {
    private final UserFactory userFactory;

    public AuthFactory(UserFactory userFactory) {
        this.userFactory = userFactory;
    }

    public UserLoginRequest newUserLoginRequest() {
        User userSaved = userFactory.newUserList().getFirst();

        return new UserLoginRequest(userSaved.getEmail(),
                userSaved.getPassword());
    }
}
