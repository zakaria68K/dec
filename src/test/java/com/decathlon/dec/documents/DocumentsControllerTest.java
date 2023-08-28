package com.decathlon.dec.documents;
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
@TestInstance(Lifecycle.PER_CLASS)
public class DocumentsControllerTest {

    
	private static String DUMMY_PDF_PATH = "src/test/java/com/decathlon/dec/resources/dummy.pdf";

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	DocumentsRepository documentsRepository;


	@Autowired
	UserRepository userRepository;

	MyUserDetails testUser;
	MyUserDetails anotherTestUser;

    
	@BeforeEach
	@Transactional
	public void setup() {
		documentsRepository.deleteAll();
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
		documentsRepository.deleteAll();
		userRepository.deleteAll();
	}

	@Test
	public void testFileUpload_shouldReturnDocumentDetails() throws Exception {
		MockMultipartFile file = new MockMultipartFile(
				"file",
				"dummy.pdf",
				MediaType.APPLICATION_PDF_VALUE,
				Files.readAllBytes(Paths.get(DUMMY_PDF_PATH)));

		CreateDocumentDto data = CreateDocumentDto
				.builder()
				.name("Résumé TG")
				.build();

		MockMultipartFile jsonData = new MockMultipartFile(
				"data",
				null,
				MediaType.APPLICATION_JSON_VALUE,
				objectMapper.writeValueAsBytes(data));

		MvcResult result = mockMvc
				.perform(
						multipart("/documents")
								.file(file)
								.file(jsonData)
								.with(user(testUser)))
				.andExpect(status().isCreated())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(
						jsonPath("$.id").value(Matchers.anyOf(Matchers.instanceOf(Integer.class), Matchers.instanceOf(long.class))))
				.andExpect(jsonPath("$.name").value(data.getName()))
				.andReturn();

		long id = ((Number) JsonPath.parse(result.getResponse().getContentAsString()).read("$.id")).longValue();
		Optional<Document> newDoc = documentsRepository.findById(id);
		assertTrue(newDoc.isPresent());
		assertNotNull(newDoc.get().getUser());
		assertEquals(newDoc.get().getUser().getId(), testUser.getUser().getId());
		assertEquals(newDoc.get().getName(), data.getName());
	
	}


	@Test
	public void testFileListing_shoudReturnListOfUploadedDocuments() throws Exception {
		documentsRepository.saveAll(
				List.of(
						Document
								.builder()
								.name("Résumé TG")
								.file(Files.readAllBytes(Paths.get(DUMMY_PDF_PATH)))
								.user(testUser.getUser())
								.build(),
						Document
								.builder()
								.name("Java Cheatsheet (LOL)")
								.file(Files.readAllBytes(Paths.get(DUMMY_PDF_PATH)))
								.user(testUser.getUser())
								.build()));

		String response = mockMvc
				.perform(get("/documents")
						.with(user(testUser)))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.results").value(Matchers.hasSize(2)))
				.andReturn()
				.getResponse()
				.getContentAsString();

		objectMapper.readValue(response, PaginatedResponse.class);
	}

	@Test
	public void testFileListing_withLotsOfItems_shoudReturnListOfUploadedDocuments() throws Exception {
		List<Document> listOfDocs = new ArrayList<>();

		int pageSize = 30;
		int page = 2;

		for (int index = 0; index < 100; index++) {
			listOfDocs.add(
					Document
							.builder()
							.name(String.format("Resource Number %d", index + 1))
							.file(Files.readAllBytes(Paths.get(DUMMY_PDF_PATH)))
							.user(testUser.getUser())
							.build());
		}

		documentsRepository.saveAll(listOfDocs);

		String response = mockMvc
				.perform(get("/documents")
						.param("page", String.valueOf(page))
						.param("size", String.valueOf(pageSize))
						.with(user(testUser)))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.results").value(Matchers.hasSize(pageSize)))
				.andReturn()
				.getResponse()
				.getContentAsString();

		@SuppressWarnings("unchecked")
		PaginatedResponse<Document> paginatedResponse = (PaginatedResponse<Document>) objectMapper.readValue(response,
				PaginatedResponse.class);

		assertEquals(paginatedResponse.getResults().size(), paginatedResponse.getCount());
		assertEquals(paginatedResponse.getPage(), page);
		assertEquals(paginatedResponse.getTotalItems(), listOfDocs.size());
		assertEquals(paginatedResponse.getCount(), pageSize);
		assertFalse(paginatedResponse.isLast());
	}



	@Test
	public void testGetDocumentDetails_shoudReturnDocumentDetails() throws Exception {
		Document doc = documentsRepository.save(
				Document
						.builder()
						.name("Résumé TG")
						.file(Files.readAllBytes(Paths.get(DUMMY_PDF_PATH)))
						.user(testUser.getUser())
						.build());

		mockMvc
				.perform(
						get(String.format("/documents/%d", doc.getId()))
								.with(user(testUser)))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.name").value(doc.getName()));
				
	}

	@Test
	public void testGetDocumentDetails_givenWrongId_shoudReturnNotFound() throws Exception {
		Document doc = documentsRepository.save(
				Document
						.builder()
						.name("Résumé TG")
						.user(testUser.getUser())
						.file(Files.readAllBytes(Paths.get(DUMMY_PDF_PATH)))
						.build());

		mockMvc
				.perform(
						get(String.format("/documents/%d", doc.getId() + 1))
								.with(user(testUser)))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testDocumentEditDetails_shouldReturnNewDocumentDetails() throws Exception {
		Document doc = documentsRepository.save(
				Document
						.builder()
						.name("Résumé TG")
						.user(testUser.getUser())
						.file(Files.readAllBytes(Paths.get(DUMMY_PDF_PATH)))
						.build());


		EditDocumentDto editData = EditDocumentDto
				.builder()
				.name("Résumé Théorie des Graphes")
				.build();

		mockMvc
				.perform(
						patch(String.format("/documents/%d", doc.getId()))
								.contentType(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(editData))
								.with(user(testUser)))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.name").value(editData.getName()));
				
				

		Document newDoc = documentsRepository.findById(doc.getId()).orElseThrow();
		assertEquals(editData.getName(), newDoc.getName());
		
	}

	@Test
	public void testDeleteDocument_shoudReturnSuccessMessage() throws Exception {
		Document doc = documentsRepository.save(
				Document
						.builder()
						.name("PostgreSQL Cheatsheet")
                        .file(Files.readAllBytes(Paths.get(DUMMY_PDF_PATH)))
						.build());

		mockMvc
				.perform(
						delete(String.format("/documents/%d", doc.getId()))
								.with(user(testUser)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message").isString());

		Optional<Document> document = documentsRepository.findById(doc.getId());
		assertTrue(document.isEmpty());
	}

	@Test
	public void testDocumentDownload_shoudReturnFileBytes() throws Exception {
		Document doc = documentsRepository.save(
				Document
						.builder()
						.name("PostgreSQL Cheatsheet")
                        .file(Files.readAllBytes(Paths.get(DUMMY_PDF_PATH)))
						.user(testUser.getUser())
						.build());

		mockMvc
				.perform(
						get(String.format("/documents/%d/file", doc.getId()))
								.with(user(testUser)))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_OCTET_STREAM))
				.andExpect(content().bytes(Files.readAllBytes(Paths.get(DUMMY_PDF_PATH))));
	}

	

	@Test
	public void testDocumentDownload_givenAnotherUserUnsharedDocumentId_shouldReturnNotFound() throws Exception {
		Document doc = documentsRepository.save(
				Document
						.builder()
						.name("PostgreSQL Cheatsheet")
                        .file(Files.readAllBytes(Paths.get(DUMMY_PDF_PATH)))
						.user(testUser.getUser())
						.build());

		mockMvc
				.perform(
						get(String.format("/documents/%d/file", doc.getId()))
								.with(user(anotherTestUser)))
				.andExpect(status().isNotFound());
	}


}
