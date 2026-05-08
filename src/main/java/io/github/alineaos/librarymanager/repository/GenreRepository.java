package io.github.alineaos.librarymanager.repository;

import io.github.alineaos.librarymanager.domain.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface GenreRepository extends JpaRepository<Genre, Long>, JpaSpecificationExecutor<Genre> {
    Optional<Genre> findByNameIgnoreCase(String name);

    Optional<Genre> findByNameIgnoreCaseAndIdNot(String name, Long id);
}
