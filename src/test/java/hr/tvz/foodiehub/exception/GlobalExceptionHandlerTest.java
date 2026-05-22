package hr.tvz.foodiehub.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("GlobalExceptionHandler - unit tests")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("handle returns 404 with the exception message")
    void handle_returnsNotFoundWithMessage() {
        NotFoundException ex = new NotFoundException("recipe missing");

        ResponseEntity<String> response = handler.handle(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualTo("recipe missing");
    }

    @Test
    @DisplayName("NotFoundException carries its constructor message")
    void notFoundException_carriesMessage() {
        NotFoundException ex = new NotFoundException("oops");

        assertThat(ex.getMessage()).isEqualTo("oops");
    }
}
