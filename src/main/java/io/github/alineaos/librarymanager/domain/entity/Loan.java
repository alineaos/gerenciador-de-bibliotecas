package io.github.alineaos.librarymanager.domain.entity;

import io.github.alineaos.librarymanager.domain.enums.LoanStatus;
import io.github.alineaos.librarymanager.domain.enums.converter.LoanStatusConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@ToString
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Loan {
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User user;

    @ManyToOne(optional = false)
    private Book book;

    @Convert(converter = LoanStatusConverter.class)
    @Column(nullable = false)
    private LoanStatus status;

    @Column(nullable = false)
    private boolean renewed = false;

    @Column(nullable = false, updatable = false)
    private LocalDate borrowedAt;

    @Column(nullable = false)
    private LocalDate dueAt;

    @Column
    private LocalDate returnedAt;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
