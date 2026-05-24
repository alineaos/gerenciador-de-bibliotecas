package io.github.alineaos.librarymanager.repository;

import io.github.alineaos.librarymanager.domain.entity.BookGenre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookGenreRepository extends JpaRepository<BookGenre, Long> {

    @Query("SELECT bg from BookGenre bg JOIN FETCH bg.genre g WHERE bg.book.id IN :bookIds")
    List<BookGenre> findByBookIds(List<Long> bookIds);

    @Query("SELECT bg from BookGenre bg JOIN FETCH bg.genre g WHERE bg.book.id = :bookId")
    List<BookGenre> findByBookId(Long bookId);
}
