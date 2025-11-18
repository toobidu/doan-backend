package org.example.quizizz.repository;

import org.example.quizizz.model.entity.Permission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    @Query("""
        SELECT p FROM Permission p
        JOIN RolePermission rp ON p.id = rp.permissionId
        JOIN UserRole ur ON rp.roleId = ur.roleId
        WHERE ur.userId = :userId
    """)
    List<Permission> findPermissionsByUserId(@Param("userId") Long userId);

    @Query("SELECT p FROM Permission p WHERE p.permissionName = :permissionName")
    Optional<Permission> findByPermissionName(@Param("permissionName") String permissionName);

    Page<Permission> findByPermissionNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String permissionName, String description, Pageable pageable);

}
