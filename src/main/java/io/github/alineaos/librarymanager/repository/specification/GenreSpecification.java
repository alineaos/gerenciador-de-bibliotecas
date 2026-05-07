package io.github.alineaos.librarymanager.repository.specification;

import io.github.alineaos.librarymanager.domain.entity.Genre;
import org.springframework.data.jpa.domain.Specification;

public class GenreSpecification {

    public static Specification<Genre> hasName(String name){
        return (root, query, cb) ->
                name == null ? null : cb.like(root.get("name"), "%" + name.toLowerCase() + "%");
    }
}
