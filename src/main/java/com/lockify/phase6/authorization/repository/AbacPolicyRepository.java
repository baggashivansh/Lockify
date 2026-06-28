package com.lockify.phase6.authorization.repository;

import com.lockify.phase6.authorization.entity.AbacPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AbacPolicyRepository extends JpaRepository<AbacPolicy, Long> {

    List<AbacPolicy> findByResourceAndActionAndActiveTrue(String resource, String action);
}
