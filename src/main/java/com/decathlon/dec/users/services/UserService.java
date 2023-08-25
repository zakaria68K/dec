package com.decathlon.dec.users.services;

import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.decathlon.dec.mappers.UserDtoMapper;
import com.decathlon.dec.users.UserRepository;
import com.decathlon.dec.users.dto.CreateUserDto;
import com.decathlon.dec.users.dto.EditPasswordDto;
import com.decathlon.dec.users.dto.EditUserDto;
import com.decathlon.dec.users.enumerations.UserRole;
import com.decathlon.dec.users.models.MyUserDetails;
import com.decathlon.dec.users.models.User;

import lombok.Data;


@Data
@Service
public class UserService {


    @Autowired
	private UserRepository userRepository;
    
    @Autowired
    private UserDtoMapper userDtoMapper;

    private static Supplier<ResponseStatusException> NOT_FOUND_HANDLER = () -> {
		return new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
	  };


    public User getUser(final Long id) {
        return userRepository.findById(id).orElseThrow(NOT_FOUND_HANDLER);
    }

	public User getUserByEmail(final String email) {
		return userRepository.findByEmail(email).orElseThrow(NOT_FOUND_HANDLER);
	}

	public Iterable<User> getAllUsers() {
		return userRepository.findAll();
	}

	// Get all users paginated
	public Page<User> getAllUsers(Pageable pageable) {
		return userRepository.findAll(pageable);
	}

	// Get users by search
	public Page<User> searchUsers(String search, Pageable pageable) {
		return userRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(search, search, search, pageable);
	}

	// Get users by search and role
	public Page<User> searchUsers(String search, UserRole role, Pageable pageable) {
		// The JPA query will need all the search parameters (for all fields) 
		// and also the role 3 times because each time it will need to compare it to a different field
		// But as we want to search by the same search term for all fields, we can just pass it 3 times
		// Same thing for the role
		return userRepository.findByFirstNameContainingIgnoreCaseAndRoleOrLastNameContainingIgnoreCaseAndRoleOrEmailContainingIgnoreCaseAndRole(search, role, search, role, search, role, pageable);
	}

	// Get users by role
	public Page<User> getUsersByRole(UserRole role, Pageable pageable) {
		return userRepository.findAllByRole(role, pageable);
	}


	public User addUser(CreateUserDto createUserDto){
		// Check if email already exists (to avoid an error from the unique constraint)
		User check = userRepository.findByEmail(createUserDto.getEmail()).orElse(null);
		if(check != null){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
		}
		User user = userDtoMapper.createUserDtoToUser(createUserDto);
		user.setActive(true);
		user.setRole(UserRole.VENDEUR);
		user.setTotal(100);
		return userRepository.save(user);
	}

	public User editUser(Long id, EditUserDto editUserDto) {
		User user = userRepository.findById(id).orElseThrow(NOT_FOUND_HANDLER);
		userDtoMapper.updateUserFromDto(editUserDto, user);
		return userRepository.save(user);
	}

	public User editPassword(EditPasswordDto editPasswordDto){
		MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		User user = userDetails.getUser();
		userDtoMapper.updatePasswordFromDto(editPasswordDto, user);
		return userRepository.save(user);
	}

	public void deleteUser(Long id){
		userRepository.delete(userRepository.findById(id).orElseThrow(NOT_FOUND_HANDLER));
	}
    
}
