package com.lockify.shared.domain.repository;

import com.lockify.shared.domain.entity.Role;
import com.lockify.shared.domain.entity.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(RoleName name);
}
