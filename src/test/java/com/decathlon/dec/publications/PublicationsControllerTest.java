package com.decathlon.dec.publications;

import com.decathlon.dec.DecApplication;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
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
import com.decathlon.dec.documents.dto.CreateDocumentDto;
import com.decathlon.dec.documents.dto.EditDocumentDto;
import com.decathlon.dec.documents.models.Document;
import com.decathlon.dec.publications.dto.CreatePublicationDto;
import com.decathlon.dec.publications.dto.UpdatePublicatioDto;
import com.decathlon.dec.publications.models.Publication;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = DecApplication.class)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PublicationsControllerTest {

    private static String PIC_PATH = "src/test/java/com/decathlon/dec/resources/test_image.png";


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PublicationRepository publicationRepository;

    @Autowired
    private UserRepository userRepository;

    Publication publication;

    MyUserDetails testUser;
    MyUserDetails anotherTestUser;

@BeforeAll
@Transactional
   public void setup() {
		publicationRepository.deleteAll();
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
   @Transactional
   public void postTest() {
           publicationRepository.deleteAll();
           publicationRepository.deleteAll();
   }

    @Test
    public void createPublication() throws Exception {

        // Load image file
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "test_image.png",
                MediaType.IMAGE_PNG_VALUE,
                Files.readAllBytes(Paths.get(PIC_PATH)));

        // Create DTO for data
        CreatePublicationDto data = CreatePublicationDto.builder()
                .description("This is a test publication")
                .build();

        // Convert data DTO to JSON
        MockMultipartFile jsonData = new MockMultipartFile(
                "data",
                null,
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(data));

        // Perform the POST request
        MvcResult result = mockMvc
                .perform(
                        multipart("/publications")
                                .file(image)
                                .file(jsonData)
                                
                                .with(user(testUser)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.description").value(data.getDescription()))
                .andReturn();

        
        
    }
    // testing the get method
    @Test
    @Transactional
    public void getPublication() throws Exception {
        // Create a publication
        Publication publication = publicationRepository.save(Publication.builder()
                .description("This is a test publication")
                .user(testUser.getUser())
                .build());

        // Save the publication
        publication = publicationRepository.save(publication);

        // Perform the GET request
        mockMvc.perform(get("/publications/" + publication.getId())
                .with(user(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("This is a test publication"))
               .andReturn();
    }
    // testing the patch method
    @Test
    @Transactional
    public void editPublication() throws Exception {
        // Create a publication
        Publication publication = new Publication();
        publication.setDescription("This is a test publication");
        publication.setUser(testUser.getUser());

        // Save the publication
        publication = publicationRepository.save(publication);

        // Create the edit DTO
        UpdatePublicatioDto editPublicationDto = new UpdatePublicatioDto();
        editPublicationDto.setDescription("This is an edited publication");

        // Convert the DTO to JSON
        String json = objectMapper.writeValueAsString(editPublicationDto);

        // Perform the PATCH request
        mockMvc.perform(patch("/publications/" + publication.getId())
                .with(user(testUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("This is an edited publication"))
                .andReturn();
    }
   

    @Test
    @Transactional
    public void deletePublication() throws Exception {
        // Create a publication
        Publication publication = new Publication();
        publication.setDescription("This is a test publication");
        publication.setUser(testUser.getUser());

        // Save the publication
        publication = publicationRepository.save(publication);

        // Perform the DELETE request
        mockMvc.perform(delete("/publications/" + publication.getId())
                .with(user(testUser)))
                .andExpect(status().isOk());

        // Check that the publication was deleted
        assertFalse(publicationRepository.findById(publication.getId()).isPresent());
    }

	}