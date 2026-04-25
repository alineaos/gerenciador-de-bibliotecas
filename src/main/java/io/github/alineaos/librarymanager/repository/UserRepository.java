package io.github.alineaos.librarymanager.repository;

import io.github.alineaos.librarymanager.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByEmail(String email);
    Optional<User> findByCpf(String cpf);

    Optional<User> findByEmailAndIdNot(String email, Long id);
}
