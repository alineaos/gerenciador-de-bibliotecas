package io.github.alineaos.librarymanager.repository;

import io.github.alineaos.librarymanager.domain.entity.BookGenre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookGenreRepository extends JpaRepository<BookGenre, Long> {
}
