package com.decathlon.dec.absences;
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
import com.decathlon.dec.absences.dto.CreateAbsenceDto;
import com.decathlon.dec.absences.enumerations.AbsenceStatus;
import com.decathlon.dec.absences.models.Absence;
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
public class AbsenceControllerTest {
    
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AbsenceRepository absenceRepository;

    MyUserDetails testUser;
    MyUserDetails anotherTestUser;   
    
    @BeforeEach
    @Transactional
    public void setup() {
       
        absenceRepository.deleteAll();
        userRepository.deleteAll();
        //create test users

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
    absenceRepository.deleteAll();
    userRepository.deleteAll();
}
@Test //test to create an absence
public void testCreateAbsence() throws Exception {
    CreateAbsenceDto absences = CreateAbsenceDto.builder()
    .startDate(new Date())
    .endDate(new Date())
    .reason("reason")
    .build();
    mockMvc.perform(
        post("/absences")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(absences))
        .with(user(testUser))
    )
    .andExpect(status().isCreated())
    .andExpect(jsonPath("$.id", Matchers.notNullValue()))
    .andExpect(jsonPath("$.startDate", Matchers.notNullValue()))
    .andExpect(jsonPath("$.endDate", Matchers.notNullValue()))
    .andExpect(jsonPath("$.reason").value("reason"));
}
@Test //test to get all absences
public void testGetAllAbsences() throws Exception {

    CreateAbsenceDto absences =  CreateAbsenceDto.builder()
    .startDate(new Date())
    .endDate(new Date())
    .reason("reason")
    .build();
    
    int page = 1;
    int pageSize = 0;
    String response = mockMvc.perform(get("/absences")
    .param("page", String.valueOf(page))
    .param("pageSize", String.valueOf(pageSize))
    .contentType(MediaType.APPLICATION_JSON)
    .content(objectMapper.writeValueAsString(absences))
    .with(user(testUser)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                 .getContentAsString();

    PaginatedResponse<Absence> paginatedResponse = objectMapper.readValue(response, PaginatedResponse.class);
    assertEquals(paginatedResponse.getResults().size(), paginatedResponse.getCount());
assertEquals(paginatedResponse.getPage(), page);
assertEquals(paginatedResponse.getCount(), pageSize);
assertTrue(paginatedResponse.isLast());
}

@Test //get absence by id
public void testGetAbsenceById_shouldReturnAbsence()  throws Exception {
    Absence absence = new Absence();
    String dateString = "2021-05-05";
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Date startDate = dateFormat.parse(dateString);
    absence.setStartDate(startDate);

    String dateeString = "2021-05-05";
    DateFormat dateeFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    Date endDate = dateeFormat.parse(dateeString);

    absence.setEndDate(endDate);
    absence.setReason("reason");
    absence.setUser(testUser.getUser());
    absence.setStatus(AbsenceStatus.APPROVED);
    Absence savedAbsence  = absenceRepository.save(absence);
 

    long startDateTimestamp = savedAbsence.getStartDate().getTime();
    long endDateTimestamp = savedAbsence.getEndDate().getTime();
    
    DateFormat expectedDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String expectedStartDate = expectedDateFormat.format(new Date(startDateTimestamp));
    String expectedEndDate = expectedDateFormat.format(new Date(endDateTimestamp));
    
    mockMvc.perform(get("/absences/{id}", savedAbsence.getId())
        .with(user(testUser))
        .contentType("application/json"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(savedAbsence.getId()))
        .andExpect(jsonPath("$.startDate").value(expectedStartDate+ "T00:00:00.000+00:00"))
        .andExpect(jsonPath("$.endDate").value(expectedEndDate+ "T00:00:00.000+00:00"))
        .andExpect(jsonPath("$.reason").value(savedAbsence.getReason()))
        .andReturn();
}

@Test //Test to edit absence (confirmed absence)
// confirm the status
public void testEditAbsence() throws Exception {
   
  Absence absence = new Absence();
    String dateString = "2021-05-05";
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Date startDate = dateFormat.parse(dateString);
    absence.setStartDate(startDate);

    String dateeString = "2021-05-05";
    DateFormat dateeFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    Date endDate = dateeFormat.parse(dateeString);

    absence.setEndDate(endDate);
    absence.setReason("reason");
    absence.setUser(testUser.getUser());
    absence.setStatus(AbsenceStatus.APPROVED);
    absenceRepository.save(absence);
 

    long startDateTimestamp = absence.getStartDate().getTime();
    long endDateTimestamp = absence.getEndDate().getTime();
    
    DateFormat expectedDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String expectedStartDate = expectedDateFormat.format(new Date(startDateTimestamp));
    String expectedEndDate = expectedDateFormat.format(new Date(endDateTimestamp));
    //print the status in the terminal
   // System.out.println(absence.getStatus());

    //test if the absence is null
  
    mockMvc.perform(get("/absences/" + absence.getId())
        .with(user(testUser))
        .contentType("application/json"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(absence.getId()))
        .andExpect(jsonPath("$.startDate").value(expectedStartDate+ "T00:00:00.000+00:00"))
        .andExpect(jsonPath("$.endDate").value(expectedEndDate+ "T00:00:00.000+00:00"))
        .andExpect(jsonPath("$.reason").value(absence.getReason()))
        .andExpect(jsonPath("$.status").value(AbsenceStatus.APPROVED.toString()))
        .andReturn();
}

@Test //test delete a absence
public void testDeleteAbsence() throws Exception {
    Absence absence = new Absence();
    String dateString = "2021-05-05";
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Date startDate = dateFormat.parse(dateString);
    absence.setStartDate(startDate);

    String dateeString = "2021-05-05";
    DateFormat dateeFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    Date endDate = dateeFormat.parse(dateeString);

    absence.setEndDate(endDate);
    absence.setReason("reason");
    absence.setUser(testUser.getUser());
    Absence savedAbsence= absenceRepository.save(absence);
 

    long startDateTimestamp = savedAbsence.getStartDate().getTime();
    long endDateTimestamp = savedAbsence.getEndDate().getTime();
    
    DateFormat expectedDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String expectedStartDate = expectedDateFormat.format(new Date(startDateTimestamp));
    String expectedEndDate = expectedDateFormat.format(new Date(endDateTimestamp));
    
    mockMvc.perform(delete("/absences/{id}", savedAbsence.getId())
        .with(user(testUser)))
        
        .andReturn();
        // check if the conge is deleted
        mockMvc.perform(get("/absences/" + savedAbsence.getId())
        .with(user(testUser)))
        .andExpect(status().isNotFound())
        .andReturn();
}


}