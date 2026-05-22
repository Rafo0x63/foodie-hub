package hr.tvz.foodiehub.controllers;

import hr.tvz.foodiehub.model.dtos.CommentDTO;
import hr.tvz.foodiehub.services.interfaces.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserCommentController - standalone unit tests")
class UserCommentControllerUnitTest {

    @Mock
    private CommentService commentService;

    @InjectMocks
    private UserCommentController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("GET /api/users/{id}/comments returns list of user comments")
    void getAllUserComments_returnsOk() throws Exception {
        CommentDTO dto = new CommentDTO(1L, "Nice", 4, LocalDateTime.now(), 1L, 1L, "ana");
        when(commentService.getAllUserComments(1L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/users/1/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].text").value("Nice"));
    }
}
