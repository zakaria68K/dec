package com.decathlon.dec.commentaires;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.decathlon.dec.DecApplication;
import com.decathlon.dec.commentaires.models.Commentaire;
import com.decathlon.dec.conges.dto.CreateCongeDto;
import com.decathlon.dec.conges.enumerations.CongeStatus;
import com.decathlon.dec.conges.models.Conge;
import com.decathlon.dec.publications.PublicationRepository;
import com.decathlon.dec.publications.models.Publication;
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
public class CommentaireControllerTest {


        private static String PIC_PATH = "src/test/java/com/decathlon/dec/resources/test_image.png";


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PublicationRepository publicationRepository;

    @Autowired
    private CommentaireRepository commentaireRepository;

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
           commentaireRepository.deleteAll();
           commentaireRepository.deleteAll();
   }

   @Test // create a comment to a publication
   public void createCommentaire() throws Exception{
              // create a publication
              Publication publication = publicationRepository.save(Publication.builder()
              .description("This is a test publication")
              .user(testUser.getUser())
              .build());
              publication = publicationRepository.save(publication);

              // create a comment
              Commentaire commentaire = new Commentaire();
              commentaire.setContenu("This is a test comment");
                commentaire.setPublication(publication);
                commentaire.setUser(testUser.getUser()); 
                commentaire = commentaireRepository.save(commentaire);   
              // test
              MvcResult result = mockMvc.perform(post("/commentaires/"+ publication.getId())
                            .with(user(testUser))
                            //with publication  
                                      
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(commentaire)))
                            .andExpect(status().isCreated())
                            .andExpect(jsonPath("$.contenu", Matchers.is(commentaire.getContenu())))
                            .andReturn();

   }

    @Test // get all comments of a publication
    public void getAllCommentaires() throws Exception{
               // create a publication
               Publication publication = publicationRepository.save(Publication.builder()
               .description("This is a test publication")
               .user(testUser.getUser())
               .build());
               publication = publicationRepository.save(publication);

               // create a comment
               Commentaire commentaire = new Commentaire();
               commentaire.setContenu("This is a test comment");
               commentaire.setPublication(publication);
               commentaire.setUser(testUser.getUser()); 
              commentaire = commentaireRepository.save(commentaire);   
               // test
               MvcResult result = mockMvc.perform(get("/commentaires/"+ publication.getId())
                             .with(user(testUser))
                             //with publication  
                                       
                             .contentType(MediaType.APPLICATION_JSON)
                             .content(objectMapper.writeValueAsString(commentaire)))
                             .andExpect(status().isOk())
                             .andReturn();

    }
    // @Test // update a comment
    // public void updateCommentaire() throws Exception{
    //            // create a publication
    //            Publication publication = publicationRepository.save(Publication.builder()
    //            .description("This is a test publication")
    //            .user(testUser.getUser())
    //            .build());
    //            publication = publicationRepository.save(publication);

    //            // create a comment
    //            Commentaire commentaire = new Commentaire();
    //            commentaire.setContenu("This is a test comment");
    //              commentaire.setPublication(publication);
    //              commentaire.setUser(testUser.getUser()); 
    //              commentaire = commentaireRepository.save(commentaire);   
    //            // test
    //            MvcResult result = mockMvc.perform(patch("/commentaires/"+ commentaire.getId())
    //                          .with(user(testUser))
    //                          //with publication  
                                       
    //                          .contentType(MediaType.APPLICATION_JSON)
    //                          .content(objectMapper.writeValueAsString(commentaire)))
    //                          .andExpect(status().isOk())
    //                          .andExpect(jsonPath("$.contenu", Matchers.is(commentaire.getContenu())))
    //                          .andReturn();

    // }
    @Test // delete a comment
    public void deleteCommentaire() throws Exception{
               // create a publication
               Publication publication = publicationRepository.save(Publication.builder()
               .description("This is a test publication")
               .user(testUser.getUser())
               .build());
               publication = publicationRepository.save(publication);

               // create a comment
               Commentaire commentaire = new Commentaire();
               commentaire.setContenu("This is a test comment");
                 commentaire.setPublication(publication);
                 commentaire.setUser(testUser.getUser()); 
                 commentaire = commentaireRepository.save(commentaire);   
               // test
               MvcResult result = mockMvc.perform(delete("/commentaires/"+ publication.getId())
                             .with(user(testUser))
                             //with publication  
                                       
                             .contentType(MediaType.APPLICATION_JSON)
                             .content(objectMapper.writeValueAsString(commentaire)))
                             .andExpect(status().isOk())
                             .andReturn();

    }




}
