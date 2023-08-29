package com.decathlon.dec.commentaires;
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
public class CommentaireControllerTest {
    
}
