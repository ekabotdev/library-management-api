package com.ekabotdev.library.integration;

import com.ekabotdev.library.auth.dto.LoginRequest;
import com.ekabotdev.library.auth.dto.LoginResponse;
import com.ekabotdev.library.auth.dto.RegisterRequest;
import com.ekabotdev.library.auth.service.AuthService;
import com.ekabotdev.library.loan.dto.LoanResponse;
import com.ekabotdev.library.member.entity.Member;
import com.ekabotdev.library.member.repository.MemberRepository;
import com.ekabotdev.library.user.entity.User;
import com.ekabotdev.library.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AuthService authService;


    @Test
    void shouldRegisterUserSuccessfully() throws Exception{
        String email = "john-" + UUID.randomUUID() + "@test.com";
        RegisterRequest request = new RegisterRequest(
                "John Doe",
                email,
                "12345678",
                "+256770546843"
        );

        String requestBody = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated());


        Optional<User> savedUser = userRepository.findByEmail(request.email());
        assertThat(savedUser).isPresent();

        Optional<Member> savedMember = memberRepository.findByUserId(savedUser.get().getId());
        assertThat(savedMember).isPresent();
    }

    @Test
    void shouldRejectDuplicateRegistration() throws Exception {

        String email = "john-" + UUID.randomUUID() + "@test.com";
        RegisterRequest request = new RegisterRequest(
                "John Doe",
                email,
                "12345678",
                "+256770546843"
        );

        String requestBody = objectMapper.writeValueAsString(request);
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isConflict());

        assertThat(userRepository.existsByEmail(request.email())).isTrue();
        assertThat(userRepository.countByEmail(email)).isEqualTo(1);
    }

    @Test
    void shouldStorePasswordHashWhenUserRegisters() throws Exception {
        String email = "john-" + UUID.randomUUID() + "@test.com";
        RegisterRequest request = new RegisterRequest(
                "John Doe",
                email,
                "12345678",
                "+256770546843"
        );

        String requestBody = objectMapper.writeValueAsString(request);
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated());

        Optional<User> savedUser = userRepository.findByEmail(email);
        assertThat(savedUser).isPresent();
        assertThat(savedUser.get().getPasswordHash()).isNotEqualTo("12345678");
        BCryptPasswordEncoder encodedPassword = new BCryptPasswordEncoder();
        assertThat(encodedPassword.matches
                ("12345678", savedUser.get().getPasswordHash())).isTrue();

    }

    @Test
    void shouldLoginSuccessfullyWithValidCredentials() throws Exception {
        String email = "john-" + UUID.randomUUID() + "@test.com";

        RegisterRequest registerRequest = new RegisterRequest(
                "John Doe",
                email,
                "12345678",
                "0770546841"
        );

        String requestBody = objectMapper.writeValueAsString(registerRequest);
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated());

        Optional<User> savedUser =  userRepository.findByEmail(email);
        assertThat(savedUser).isPresent();
        LoginRequest loginRequest = new LoginRequest(
               email,
                "12345678"
         );


        MvcResult mvcResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        LoginResponse loginResponse = objectMapper.readValue(jsonResponse, LoginResponse.class);

        assertThat(loginResponse.accessToken()).isNotBlank();
        assertThat(loginResponse.tokenType()).isEqualTo("Bearer");

     }

     @Test
    void shouldRejectLoginWithAnIncorrectPassword() throws Exception {
        String email = "john-" + UUID.randomUUID() + "@test.com";

        RegisterRequest request = new RegisterRequest(
                "John Doe",
                email,
                "12345678",
                "0704037206"
        );

        String requestBody = objectMapper.writeValueAsString(request);
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated());

        Optional<User> savedUser = userRepository.findByEmail(email);
        assertThat(savedUser).isPresent();

        LoginRequest loginRequest = new LoginRequest(
                email,
                "11111111"
        );

        MvcResult mvcResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isForbidden())
                .andReturn();
     }
 }
