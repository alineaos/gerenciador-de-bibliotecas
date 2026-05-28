package io.github.alineaos.librarymanager.repository.specification;

import io.github.alineaos.librarymanager.domain.entity.Loan;
import io.github.alineaos.librarymanager.domain.enums.LoanStatus;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class LoanSpecification {

    public static Specification<Loan> fetchRelationships(){
        return (root, query, cb) -> {
            if (query != null && query.getResultType() != Long.class){
                root.fetch("user", JoinType.INNER);
                root.fetch("book", JoinType.INNER);
            }
            return null;
        };
    }

    public static Specification<Loan> hasUserId(Long userId) {
        return (root, query, cb) ->
            userId == null ? null : cb.equal(root.get("user").get("id"), userId);
    }

    public static Specification<Loan> hasBookId(Long bookId) {
        return (root, query, cb) ->
                bookId == null ? null : cb.equal(root.get("book").get("id"), bookId);
    }

    public static Specification<Loan> hasLoanStatus(LoanStatus status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Loan> hasRenewed(Boolean renewed) {
        return (root, query, cb) ->
                renewed == null ? null : cb.equal(root.get("renewed"), renewed);
    }

    public static Specification<Loan> hasBorrowedAt(LocalDate borrowedAt) {
        return (root, query, cb) ->
                borrowedAt == null ? null : cb.equal(root.get("borrowedAt"), borrowedAt);
    }

    public static Specification<Loan> hasDueAt(LocalDate dueAt) {
        return (root, query, cb) ->
                dueAt == null ? null : cb.equal(root.get("dueAt"), dueAt);
    }

    public static Specification<Loan> hasReturnedAt(LocalDate returnedAt) {
        return (root, query, cb) ->
                returnedAt == null ? null : cb.equal(root.get("returnedAt"), returnedAt);
    }
}
