package com.decathlon.users.models;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import com.decathlon.users.enumerations.UserService;
import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.security.crypto.bcrypt.BCrypt;

import com.decathlon.users.enumerations.UserRole;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {
    
        @Id
        @Column(name = "user_id")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "first_name", nullable = false)
        private String firstName;

        @Column(name = "last_name", nullable = false)
        private String lastName;

        @Column(name = "email", nullable = false)
        private String email;

        @Column(name = "service", nullable = false)
        private UserService service;

        @Column(name = "role", nullable = false)
        private UserRole role;

        @Column(name = "password", nullable = false)
	@JsonIgnore
	private String password;

        @Column(name = "is_active", nullable = false)
	private boolean isActive;
        
        // Crypt password before saving
	public void setPassword(String password) {
		this.password = BCrypt.hashpw(password, BCrypt.gensalt());
	}

	// Get full name
	@JsonIgnore
	public String getFullName() {
		return this.firstName + " " + this.lastName;
	}
    
}