package hr.tvz.foodiehub.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;

import hr.tvz.foodiehub.model.requests.CreateCommentRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
class CommentControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    void getAllComments_withoutAuthentication_returns401() throws Exception {
        mockMvc.perform(get("/api/recipes/1/comments"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void getAllComments_withAuthentication_returns200() throws Exception {
        mockMvc.perform(get("/api/recipes/1/comments"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getCommentById_withoutAuthentication_returns401() throws Exception {
        mockMvc.perform(get("/api/recipes/1/comments/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteComment_withoutAuthentication_returns401() throws Exception {
        mockMvc.perform(delete("/api/recipes/1/comments/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createComment_withoutAuthentication_returns401() throws Exception {

        CreateCommentRequest request = new CreateCommentRequest(
                "Great recipe!", 5, 99L
        );

        mockMvc.perform(post("/api/recipes/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "ana@gmail.com")
    void createComment_withAuthentication_returns201() throws Exception {

        CreateCommentRequest request = new CreateCommentRequest(
                "Great recipe!", 5, 99L
        );

        mockMvc.perform(post("/api/recipes/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void updateComment_withoutAuthentication_returns401() throws Exception {

        CreateCommentRequest request = new CreateCommentRequest(
                "Updated comment", 5, 99L
        );

        mockMvc.perform(put("/api/recipes/1/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}