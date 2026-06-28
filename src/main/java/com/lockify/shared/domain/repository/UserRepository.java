package com.lockify.shared.domain.repository;

import com.lockify.shared.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    Optional<User> findByEmailOrUsername(String email, String username);

    /** Login ke liye credential saath fetch - N+1 avoid */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.credential LEFT JOIN FETCH u.roles WHERE u.email = :id OR u.username = :id")
    Optional<User> findByIdentifierWithCredential(@Param("id") String identifier);
}
