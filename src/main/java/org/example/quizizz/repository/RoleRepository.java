package org.example.quizizz.repository;

import org.example.quizizz.model.entity.Permission;
import org.example.quizizz.model.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(String roleName);

    @Query("SELECT ur.userId FROM UserRole ur WHERE ur.roleId = :roleId")
    List<Long> findUserIdsByRoleId(@Param("roleId") Long roleId);

    @Query("SELECT p.permissionName FROM Permission p " +
            "JOIN RolePermission rp ON p.id = rp.permissionId " +
            "JOIN Role r ON rp.roleId = r.id " +
            "WHERE r.roleName = :roleName")
    List<String> findPermissionNamesByRoleName(@Param("roleName") String roleName);

    @Query("SELECT p FROM Permission p " +
            "JOIN RolePermission rp ON p.id = rp.permissionId " +
            "WHERE rp.roleId IN :roleIds")
    List<Permission> getPermissionsByRoleIds(@Param("roleIds") Set<Long> roleIds);
}
