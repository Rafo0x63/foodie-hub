package hr.tvz.foodiehub.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JwtService - unit tests")
class JwtServiceTest {

    private static final String SECRET = "neki-super-dugi-secret-od-barem-32-znaka";

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", SECRET);
    }

    @Test
    @DisplayName("generateToken produces a valid token containing subject and roles")
    void generateToken_andExtract() {
        String token = jwtService.generateToken("ana@gmail.com", List.of("ROLE_USER"));

        assertThat(token).isNotBlank();
        assertThat(jwtService.isValid(token)).isTrue();
        assertThat(jwtService.extractUsername(token)).isEqualTo("ana@gmail.com");
        assertThat(jwtService.extractRoles(token)).containsExactly("ROLE_USER");
    }

    @Test
    @DisplayName("isValid returns false for malformed token")
    void isValid_returnsFalseForGarbage() {
        assertThat(jwtService.isValid("not-a-jwt")).isFalse();
    }

    @Test
    @DisplayName("isValid returns false for null token")
    void isValid_returnsFalseForNull() {
        assertThat(jwtService.isValid(null)).isFalse();
    }

    @Test
    @DisplayName("isValid returns false for token signed with a different secret")
    void isValid_returnsFalseForWrongSignature() {
        JwtService other = new JwtService();
        ReflectionTestUtils.setField(other, "secret", "a-totally-different-secret-string-32+chars");

        String foreignToken = other.generateToken("x@y.com", List.of("ROLE_USER"));

        assertThat(jwtService.isValid(foreignToken)).isFalse();
    }

    @Test
    @DisplayName("extractRoles returns multiple roles")
    void extractRoles_returnsAll() {
        String token = jwtService.generateToken("admin@gmail.com", List.of("ROLE_USER", "ROLE_ADMIN"));

        assertThat(jwtService.extractRoles(token))
                .containsExactly("ROLE_USER", "ROLE_ADMIN");
    }
}
