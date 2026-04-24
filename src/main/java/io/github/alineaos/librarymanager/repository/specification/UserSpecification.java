package io.github.alineaos.librarymanager.repository.specification;

import io.github.alineaos.librarymanager.domain.entity.User;
import io.github.alineaos.librarymanager.domain.enums.UserRole;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {

    public static Specification<User> hasName(String name){
        return (root, query, cb) ->
                name == null ? null : cb.like(root.get("fullName"), "%" + name.toLowerCase() + "%");
    }

    public static Specification<User> hasUserRole(UserRole role){
        return (root, query, cb) ->
                role == null ? null : cb.equal(root.get("role"), role);
    }
}
