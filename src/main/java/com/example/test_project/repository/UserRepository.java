package com.example.test_project.repository;

import com.example.test_project.entity.Permission;
import com.example.test_project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    boolean existsByName(String name);

    boolean existsByCode(String code);

    Optional<User> findByCode(String code);

    @Query("""
        SELECT DISTINCT u
        FROM User u
        LEFT JOIN FETCH u.userRoles ur
        LEFT JOIN FETCH ur.role r
        WHERE u.code = :code
    """)
    Optional<User> findByCodeWithRoles(@Param("code") String code);

    @Query("""
        SELECT DISTINCT p
        FROM User u
        JOIN u.userRoles ur
        JOIN ur.role r
        JOIN r.rolePermissions rp
        JOIN rp.permission p
        WHERE u.code = :code
        AND ur.status = true
        AND rp.status = true
    """)
    List<Permission> findUserPermissions(@Param("code") String code);
}
