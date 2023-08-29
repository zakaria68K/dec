package com.decathlon.dec.conges;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.decathlon.dec.DecApplication;
import com.decathlon.dec.conges.dto.CreateCongeDto;
import com.decathlon.dec.documents.DocumentsRepository;
import com.decathlon.dec.documents.dto.CreateDocumentDto;
import com.decathlon.dec.documents.dto.EditDocumentDto;
import com.decathlon.dec.documents.models.Document;
import com.decathlon.dec.users.UserRepository;
import com.decathlon.dec.users.dto.PaginatedResponse;
import com.decathlon.dec.users.enumerations.UserRole;
import com.decathlon.dec.users.models.MyUserDetails;
import com.decathlon.dec.users.models.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import static org.hamcrest.MatcherAssert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;



@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = DecApplication.class)
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
public class CongesControllerTest {
        

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	CongeRepository congesRepository;


	@Autowired
	UserRepository userRepository;

	MyUserDetails testUser;
	MyUserDetails anotherTestUser;

    
	@BeforeEach
	@Transactional
	public void setup() {
		congesRepository.deleteAll();
		userRepository.deleteAll();

		// Create test users
		User user = new User();
		user.setFirstName("John");
		user.setLastName("Smith");
		user.setRole(UserRole.VENDEUR);
		user.setPassword("password");
		user.setEmail("john.smith@decathlon.com");

		User anotherUser = new User();
		anotherUser.setFirstName("Jane");
		anotherUser.setLastName("Doe");
		anotherUser.setEmail("jane.doe@decathlon.com");
		anotherUser.setPassword("password");
		anotherUser.setRole(UserRole.VENDEUR);
		anotherUser.setActive(true);

		// Save the test users
		User newUser = userRepository.save(user);
		testUser = new MyUserDetails(newUser);

		User anotherNewUser = userRepository.save(anotherUser);
		anotherTestUser = new MyUserDetails(anotherNewUser);


	}

    @AfterEach
    public void tearDown() {
        congesRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void testCreateConges() throws Exception {
        CreateCongeDto conges = CreateCongeDto.builder()
            .startDate(new Date())
            .endDate(new Date())
            .reason("reason")
            .build();

        mockMvc.perform(
            post("/conges")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(conges))
            .with(user(testUser))
        )
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", Matchers.notNullValue()))
        .andExpect(jsonPath("$.startDate", Matchers.notNullValue()))
        .andExpect(jsonPath("$.endDate", Matchers.notNullValue()))
        .andExpect(jsonPath("$.reason").value("reason"));
        
    }

    @Test //test to get all conges
    public void testGetAllConges() throws Exception {
        CreateCongeDto conges = CreateCongeDto.builder()
            .startDate(new Date())
            .endDate(new Date())
            .reason("reason")
            .build();

        mockMvc.perform(
            post("/conges")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(conges))
            .with(user(testUser))
        )
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", Matchers.notNullValue()))
        .andExpect(jsonPath("$.startDate", Matchers.notNullValue()))
        .andExpect(jsonPath("$.endDate", Matchers.notNullValue()))
        .andExpect(jsonPath("$.reason").value("reason"));

        mockMvc.perform(
            get("/conges")
            .with(user(testUser))
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.results", Matchers.hasSize(1)))
        .andExpect(jsonPath("$.results[0].id", Matchers.notNullValue()))
        .andExpect(jsonPath("$.results[0].startDate", Matchers.notNullValue()))
        .andExpect(jsonPath("$.results[0].endDate", Matchers.notNullValue()))
        .andExpect(jsonPath("$.results[0].reason").value("reason"));
    }

}
