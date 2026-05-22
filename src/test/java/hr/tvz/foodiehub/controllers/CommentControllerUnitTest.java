package hr.tvz.foodiehub.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import hr.tvz.foodiehub.model.dtos.CommentDTO;
import hr.tvz.foodiehub.model.requests.CreateCommentRequest;
import hr.tvz.foodiehub.services.interfaces.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CommentController - standalone unit tests")
class CommentControllerUnitTest {

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController controller;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    private CommentDTO sample() {
        return new CommentDTO(1L, "Great", 5, LocalDateTime.now(), 1L, 1L, "ana");
    }

    @Test
    @DisplayName("GET /api/recipes/{id}/comments returns list")
    void getAllComments_returnsOk() throws Exception {
        when(commentService.getAllRecipeComments(1L)).thenReturn(List.of(sample()));

        mockMvc.perform(get("/api/recipes/1/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].text").value("Great"));
    }

    @Test
    @DisplayName("GET /api/recipes/{rid}/comments/{cid} returns the comment")
    void getCommentById_returnsOk() throws Exception {
        when(commentService.getCommentById(1L, 1L)).thenReturn(sample());

        mockMvc.perform(get("/api/recipes/1/comments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value(5));
    }

    @Test
    @DisplayName("DELETE /api/recipes/{rid}/comments/{cid} returns 204")
    void deleteComment_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/recipes/1/comments/2"))
                .andExpect(status().isNoContent());

        verify(commentService).deleteCommentById(1L, 2L);
    }

    @Test
    @DisplayName("POST /api/recipes/{id}/comments returns 201 and Location")
    void createComment_returnsCreated() throws Exception {
        when(commentService.createNewComment(eq(1L), any())).thenReturn(sample());

        CreateCommentRequest request = new CreateCommentRequest("Great", 5, 1L);

        mockMvc.perform(post("/api/recipes/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/recipes/1/comments/1"));
    }

    @Test
    @DisplayName("PUT /api/recipes/{rid}/comments/{cid} returns 200 with updated body")
    void updateComment_returnsOk() throws Exception {
        when(commentService.updateComment(eq(1L), eq(2L), any())).thenReturn(sample());

        CreateCommentRequest request = new CreateCommentRequest("Updated", 3, 1L);

        mockMvc.perform(put("/api/recipes/1/comments/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
