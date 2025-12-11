package com.harnessdemo.repositories;

import com.harnessdemo.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    List<User> findByActive(boolean active);

    List<User> findByNameContainingIgnoreCase(String name);

    @Query("SELECT u FROM User u WHERE u.email LIKE %?1%")
    List<User> findByEmailDomain(String domain);

    boolean existsByEmail(String email);
}
