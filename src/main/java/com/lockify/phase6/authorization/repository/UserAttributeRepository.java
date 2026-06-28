package com.lockify.phase6.authorization.repository;

import com.lockify.phase6.authorization.entity.UserAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAttributeRepository extends JpaRepository<UserAttribute, Long> {

    Optional<UserAttribute> findByUserId(Long userId);
}
