package com.decathlon.dec.users;

import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.decathlon.dec.DecApplication;
import com.decathlon.dec.users.dto.CreateUserDto;
import com.decathlon.dec.users.dto.EditUserDto;
import com.decathlon.dec.users.enumerations.UserRole;
import com.decathlon.dec.users.models.MyUserDetails;
import com.decathlon.dec.users.models.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = DecApplication.class)
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
public class UserControllerTest {
    
    
    @Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

    @Autowired
	UserRepository userRepository;

    MyUserDetails testUser;

    // Store the created user ids to delete them after the tests
    ArrayList<Long> createdUsersIds = new ArrayList<>();
    
    @BeforeAll
    public void setUp() throws Exception {
        // Create a user
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john_doe@decathlon.com");
        user.setPassword("password");
        user.setActive(true);
        user.setRole(UserRole.VENDEUR);

        // Create another user
        User user2 = new User();
        user2.setFirstName("Jane");
        user2.setLastName("Doe");
        user2.setEmail("jane_doe@decathlon.com");
        user2.setPassword("password");
        user2.setActive(true);
        user2.setRole(UserRole.VENDEUR);


        User user3 = new User();
        user3.setFirstName("John");
        user3.setLastName("Smith");
        user3.setEmail("john_smith@decathlon.com");
        user3.setPassword("password");
        user3.setActive(true);
        user3.setRole(UserRole.DIRECTEUR);

          // Save the users
          userRepository.save(user);
          userRepository.save(user2);
          userRepository.save(user3);
  
          // Add the created users ids to the list
          createdUsersIds.add(user.getId());
          createdUsersIds.add(user2.getId());
          createdUsersIds.add(user3.getId());
  
          // Create a test user
          testUser = new MyUserDetails(user3);
      }

      
    @Test // create a user with a valid email
    public void testCreateUser_withValidEmail() throws Exception {
        CreateUserDto user = CreateUserDto.builder()
            .firstName("Mouad")
            .lastName("FIALI")
            .email("mouad_fiali@decathlon.com") // Valid email
            .password("P@ssw0rd")
            .confirmPassowrd("P@ssw0rd")
            .build();

            String response = mockMvc.perform(post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(user)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.email", Matchers.is(user.getEmail())))
            .andReturn().getResponse().getContentAsString();

            // Delete the user
            Long id = new JSONObject(response).getLong("id");
            userRepository.deleteById(id);
}

   @Test // create a user with an existing email
    public void testCreateUser_withExistingEmail() throws Exception {
        CreateUserDto user = CreateUserDto.builder()
            .firstName("Mouad")
            .lastName("FIALI")
            .email("john_doe@decathlon.com") // Existing email
            .password("P@ssw0rd")
            .confirmPassowrd("P@ssw0rd")
            .build();

        mockMvc.perform(post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(user)))
                    .andExpect(status().isBadRequest())
                    .andReturn();
        
        // no user should be created
    }

    
  
    @Test // Test search by first name
    public void testGetUsersBySearch_withFirstName() throws Exception {
        int pageSize = 2;
        int page = 0;


        mockMvc.perform(get("/users?search=John")
            .with(user(testUser))
            .param("page", String.valueOf(page))
            .param("size", String.valueOf(pageSize))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.results", Matchers.hasSize(2)))
            .andExpect(jsonPath("$.results[*].firstName", Matchers.everyItem(Matchers.is("John"))))
            .andReturn();
            
    }

    @Test // Test search by last name (case insensitive)
    public void testGetUsersBySearch_withLastName() throws Exception {
        int pageSize = 2;
        int page = 0;

        mockMvc.perform(get("/users?search=doe") // Search for "doe" in the last name (case insensitive)
            .with(user(testUser))
            .param("page", String.valueOf(page))
            .param("size", String.valueOf(pageSize))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.results", Matchers.hasSize(2)))
            .andExpect(jsonPath("$.results[*].lastName", Matchers.everyItem(Matchers.is("Doe"))))
            .andReturn();
    }

    @Test // Test search by email (case insensitive)
    public void testGetUsersBySearch3() throws Exception {

        // Create some users with an email containing "mouad"
        User user = new User();
        user.setFirstName("User"); // force the first name to be different from the search term
        user.setLastName("Boukhriss");
        user.setEmail("mouad_boukhriss@decathlon.com");
        user.setPassword("password");
        user.setRole(UserRole.VENDEUR);

        User user2 = new User();
        user2.setFirstName("User"); // force the first name to be different from the search term
        user2.setLastName("Samawi");
        user2.setEmail("hamdoun_mouadi@decathlon.com");
        user2.setPassword("password");
        user2.setRole(UserRole.VENDEUR);
        
        // Save the users
        userRepository.save(user);
        userRepository.save(user2);

        int pageSize = 2;
        int page = 0;

        mockMvc.perform(get("/users?search=mouad") // Search for "mouad" in the email (case insensitive)
            .with(user(testUser))
            .param("page", String.valueOf(page))
            .param("size", String.valueOf(pageSize))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.results", Matchers.hasSize(2)))
            .andExpect(jsonPath("$.results[*].email", Matchers.everyItem(Matchers.containsString("mouad"))))
            .andReturn();

        // Delete the users
        userRepository.delete(user);
        userRepository.delete(user2);
    }

    
    @Test // Test get user by id
    public void testGetUserById_shouldReturnUser() throws Exception {

        // Create a user
        User user = new User();
        user.setFirstName("mouad");
        user.setLastName("fiali");
        user.setEmail("mouad_fiali@decathlon.com");
        user.setPassword("password");
        user.setRole(UserRole.VENDEUR);

        // Save the user
        userRepository.save(user);

        // Get the user by id
        mockMvc.perform(get("/users/" + user.getId())
            .with(user(testUser))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName", Matchers.is("mouad")))
            .andExpect(jsonPath("$.lastName", Matchers.is("fiali")))
            .andExpect(jsonPath("$.email", Matchers.is("mouad_fiali@decathlon.com")))
            .andExpect(jsonPath("$.role", Matchers.is("VENDEUR")))
            .andReturn();

        // Delete the user
        userRepository.delete(user);
    }

    @Test // Test get user by id with an invalid id
    public void testGetUserById_shouldReturnNotFound() throws Exception {

        // Get the user by id
        mockMvc.perform(get("/users/0")
            .with(user(testUser))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andReturn();
    
    }

    @Test // test login with a valid user
    public void testLogin_shouldReturnStatusOK() throws Exception {

        // Create a json object with the email and password of the test user
        JSONObject json = new JSONObject();
        json.put("username", "john_doe@decathlon.com");
        json.put("password", "password");
        
        // login with the test user
        mockMvc.perform(post("/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message", Matchers.is("Logged in successfully")))
            .andReturn();

    }

    @Test // test edit user by id when it's not the principal
    public void testEditUserById_shouldReturnForbidden() throws Exception {

        EditUserDto editUserDto = EditUserDto.builder()
            .firstName("john le bon")
            .lastName("doe le bien")
            .build();

        // create a user
        User user = new User();
        user.setFirstName("Mouad");
        user.setLastName("Fiali");
        user.setEmail("mouad_fiali@decathlon.com");
        user.setPassword("password");
        user.setRole(UserRole.VENDEUR);

        // save the user
        userRepository.save(user);

        // edit the user
        mockMvc.perform(patch("/users/" + user.getId())
            .with(user(testUser))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(editUserDto)))
            .andExpect(status().isForbidden())
            .andReturn();
        
        // delete the user
        userRepository.delete(user);
    }

    @Test // test edit user by id when it's the principal
    public void testEditUserById_shouldReturnUser() throws Exception {

        EditUserDto editUserDto = EditUserDto.builder()
            .firstName("john le bon")
            .lastName("doe le bien")
            .build();

         // create a user
         User user = new User();
         user.setFirstName("mike");
         user.setLastName("ross");
         user.setEmail("mike_ross@decathlon.com");
         user.setPassword("password");
         user.setRole(UserRole.VENDEUR);
 
         // save the user
         user = userRepository.save(user);

         MyUserDetails userDetails = new MyUserDetails(user);

        // edit the user
        mockMvc.perform(patch("/users/" + userDetails.getUser().getId())
            .with(user(userDetails))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(editUserDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName", Matchers.is("john le bon")))
            .andExpect(jsonPath("$.lastName", Matchers.is("doe le bien")))
            .andReturn();
        
        // delete the user
        userRepository.delete(user);

    }

    @Test // test edit the user's role by Moderator
    public void testEditUserRole_shouldReturnUser() throws Exception {

        EditUserDto editUserRoleDto = EditUserDto.builder()
            .role(UserRole.DIRECTEUR)
            .build();

        // create a user
        User user = new User();
        user.setFirstName("Mouad");
        user.setLastName("Fiali");
        user.setEmail("mouad_fiali@decathlon.com");
        user.setPassword("password");
        user.setRole(UserRole.VENDEUR);

        // create a moderator
        User moderator = new User();
        moderator.setFirstName("moderator");
        moderator.setLastName("moderator");
        moderator.setEmail("moderator@decathlon.com");
        moderator.setPassword("password");
        moderator.setRole(UserRole.DIRECTEUR);

        // save the user
        userRepository.save(user);
        userRepository.save(moderator);

        MyUserDetails userDetails = new MyUserDetails(moderator);

        // edit the user's role
        mockMvc.perform(patch("/users/" + user.getId())
            .with(user(userDetails))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(editUserRoleDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.role", Matchers.is("DIRECTEUR")))
            .andReturn();

        // delete the user
        userRepository.delete(user);
        userRepository.delete(moderator);

    }

   

  
    @Test // test login with an invalid user
    public void testLogin_shouldReturnStatusBadRequest() throws Exception {

        // Create a json object with the email and password of the test user
        JSONObject json = new JSONObject();
        json.put("username", "john_doe@decathlon.com");
        json.put("password", "wrong_password");

        // login with the test user
        mockMvc.perform(post("/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json.toString()))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", Matchers.is("Incorrect username or password")))
            .andReturn();
    }

  

    @AfterAll
    public void tearDown() throws Exception {
        // Delete the created users from the list we created
        System.out.println("All users tests are done!");
        System.out.println("Deleting the created users...");
        for (Long userId : createdUsersIds) {
            userRepository.deleteById(userId);
            System.out.println("User with id " + userId + " deleted successfully");
        }
        System.out.println("Database is cleaned up!");
    }

}