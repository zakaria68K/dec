package com.decathlon.users;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.decathlon.users.models.User;

public interface UserRepository extends JpaRepository<User, Long> {
    public Optional<User> findByEmail(String email);
}
