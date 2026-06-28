package com.lockify.phase6.authorization.repository;

import com.lockify.phase6.authorization.entity.UserResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserResourceRepository extends JpaRepository<UserResource, Long> {

    List<UserResource> findByOwnerId(Long ownerId);

    Optional<UserResource> findByIdAndOwnerId(Long id, Long ownerId);
}
