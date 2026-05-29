package io.github.alineaos.librarymanager.repository;

import io.github.alineaos.librarymanager.domain.entity.Loan;
import io.github.alineaos.librarymanager.domain.enums.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long>, JpaSpecificationExecutor<Loan> {
    Optional<Loan> findByUserIdAndStatusIn(Long userId, Set<LoanStatus> activeStatus);

    Optional<Loan> findByBookIdAndStatusIn(Long bookId, Set<LoanStatus> activeStatus);

    @Query("SELECT l FROM Loan l JOIN FETCH l.user JOIN FETCH l.book WHERE l.id = :id")
    Optional<Loan> findByIdWithRelationships(Long id);
}
