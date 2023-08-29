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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.decathlon.dec.DecApplication;
import com.decathlon.dec.conges.dto.CreateCongeDto;
import com.decathlon.dec.conges.enumerations.CongeStatus;
import com.decathlon.dec.conges.models.Conge;
import com.decathlon.dec.users.UserRepository;
import com.decathlon.dec.users.dto.PaginatedResponse;
import com.decathlon.dec.users.enumerations.UserRole;
import com.decathlon.dec.users.models.MyUserDetails;
import com.decathlon.dec.users.models.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;



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

            int page = 1;
            int pageSize = 0;
            String response = mockMvc.perform( get("/conges")
            .param("page", String.valueOf(page))
            .param("size", String.valueOf(pageSize))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(conges))
                .with(user(testUser)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                 .getContentAsString();
            
            PaginatedResponse<Conge> paginatedResponse = (PaginatedResponse<Conge>) objectMapper.readValue(response,
            PaginatedResponse.class);
assertEquals(paginatedResponse.getResults().size(), paginatedResponse.getCount());
assertEquals(paginatedResponse.getPage(), page);
assertEquals(paginatedResponse.getCount(), pageSize);
assertTrue(paginatedResponse.isLast());
       
    }
    @Test //get conge by id
    public void testGetAlertById_shouldReturnConge()  throws Exception {
        Conge conge = new Conge();
        String dateString = "2021-05-05";
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = dateFormat.parse(dateString);
        conge.setStartDate(startDate);

        String dateeString = "2021-05-05";
        DateFormat dateeFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        Date endDate = dateeFormat.parse(dateeString);

        conge.setEndDate(endDate);
        conge.setReason("reason");
        conge.setUser(testUser.getUser());
        Conge savedConge = congesRepository.save(conge);
     

        long startDateTimestamp = savedConge.getStartDate().getTime();
        long endDateTimestamp = savedConge.getEndDate().getTime();
        
        DateFormat expectedDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String expectedStartDate = expectedDateFormat.format(new Date(startDateTimestamp));
        String expectedEndDate = expectedDateFormat.format(new Date(endDateTimestamp));
        
        mockMvc.perform(get("/conges/{id}", savedConge.getId())
            .with(user(testUser))
            .contentType("application/json"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(savedConge.getId()))
            .andExpect(jsonPath("$.startDate").value(expectedStartDate+ "T00:00:00.000+00:00"))
            .andExpect(jsonPath("$.endDate").value(expectedEndDate+ "T00:00:00.000+00:00"))
            .andExpect(jsonPath("$.reason").value(savedConge.getReason()))
            .andReturn();
    }

    @Test //Test to edit conge (confirmed conge)
    // confirm the status
    public void testEditConge() throws Exception {
        Conge conge = new Conge();
        String dateString = "2021-05-05";
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = dateFormat.parse(dateString);
        conge.setStartDate(startDate);

        String dateeString = "2021-05-05";
        DateFormat dateeFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        Date endDate = dateeFormat.parse(dateeString);

        conge.setEndDate(endDate);
        conge.setReason("reason");
        conge.setUser(testUser.getUser());
        conge.setStatus(CongeStatus.CONFIRMED);
        Conge savedConge = congesRepository.save(conge);
     

        long startDateTimestamp = savedConge.getStartDate().getTime();
        long endDateTimestamp = savedConge.getEndDate().getTime();
        
        DateFormat expectedDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String expectedStartDate = expectedDateFormat.format(new Date(startDateTimestamp));
        String expectedEndDate = expectedDateFormat.format(new Date(endDateTimestamp));


        
        mockMvc.perform(patch("/conges/{id}", savedConge.getId())
            .with(user(testUser))
            .contentType("application/json"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(savedConge.getId()))
            .andExpect(jsonPath("$.startDate").value(expectedStartDate+ "T00:00:00.000+00:00"))
            .andExpect(jsonPath("$.endDate").value(expectedEndDate+ "T00:00:00.000+00:00"))
            .andExpect(jsonPath("$.reason").value(savedConge.getReason()))
            .andExpect(jsonPath("$.status").value(CongeStatus.CONFIRMED.toString()))
            .andReturn();
    }

    @Test //test delete a conge
    public void testDeleteConge() throws Exception {
        Conge conge = new Conge();
        String dateString = "2021-05-05";
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = dateFormat.parse(dateString);
        conge.setStartDate(startDate);

        String dateeString = "2021-05-05";
        DateFormat dateeFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        Date endDate = dateeFormat.parse(dateeString);

        conge.setEndDate(endDate);
        conge.setReason("reason");
        conge.setUser(testUser.getUser());
        Conge savedConge = congesRepository.save(conge);
     

        long startDateTimestamp = savedConge.getStartDate().getTime();
        long endDateTimestamp = savedConge.getEndDate().getTime();
        
        DateFormat expectedDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String expectedStartDate = expectedDateFormat.format(new Date(startDateTimestamp));
        String expectedEndDate = expectedDateFormat.format(new Date(endDateTimestamp));
        
        mockMvc.perform(delete("/conges/{id}", savedConge.getId())
            .with(user(testUser)))
            
            .andReturn();
            // check if the conge is deleted
            mockMvc.perform(get("/conges/" + savedConge.getId())
            .with(user(testUser)))
            .andExpect(status().isNotFound())
            .andReturn();
    }



}
