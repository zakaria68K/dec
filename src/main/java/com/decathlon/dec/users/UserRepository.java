package com.decathlon.dec.users;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.decathlon.dec.users.enumerations.UserRole;
import com.decathlon.dec.users.models.User;

public interface UserRepository extends JpaRepository<User, Long> {
    
    public Optional<User> findByEmail(String email);

	public long deleteByFirstName(String firstName);

	public Page<User> findAll(Pageable pageable);

	public Page<User> findAllByRole(UserRole role, Pageable pageable);

	public Page<User> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String firstName, String lastName, String email, Pageable pageable);

	public Page<User> findByFirstNameContainingIgnoreCaseAndRoleOrLastNameContainingIgnoreCaseAndRoleOrEmailContainingIgnoreCaseAndRole(String firstName, UserRole role1, String lastName, UserRole role2, String email, UserRole role3, Pageable pageable);
}

