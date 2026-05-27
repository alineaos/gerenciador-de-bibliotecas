package io.github.alineaos.librarymanager.domain.enums;

import lombok.Getter;

import java.util.Set;

@Getter
public enum LoanStatus {
    BORROWED("Borrowed"),
    RENEWED("Renewed"),
    RETURNED("Returned"),
    OVERDUE("Overdue"),
    LOST("Lost"),
    CANCELLED("Cancelled");

    private final String status;

    private final static Set<LoanStatus> ACTIVE_STATUS = Set.of(BORROWED, RENEWED, OVERDUE);
    private final static Set<LoanStatus> FINALIZED_STATUS = Set.of(RETURNED, LOST, CANCELLED);

    LoanStatus(String status) {
        this.status = status;
    }

    public static Set<LoanStatus> getActiveStatus(){
        return ACTIVE_STATUS;
    }

    public static Set<LoanStatus> getFinalizedStatus(){
        return FINALIZED_STATUS;
    }
}
