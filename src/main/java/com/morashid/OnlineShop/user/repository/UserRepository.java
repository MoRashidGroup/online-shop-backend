package com.morashid.OnlineShop.user.repository;

import com.morashid.OnlineShop.user.entity.Role;
import com.morashid.OnlineShop.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByRole(Role role);

    long countByRole(Role role);

    Optional<User> findByPhoneNo(String phoneNo);

    boolean existsByPhoneNo(String phoneNo);

    // ============================================
    // ADMIN METHODS
    // ============================================

    List<User> findByRoleOrderByCreatedAtDesc(Role role);

    List<User> findByEnabled(Boolean enabled);

    long countByEnabled(Boolean enabled);
}