package hr.tvz.foodiehub.security;

import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CookieService - unit tests")
class CookieServiceTest {

    private CookieService cookieService;
    private HttpServletResponse response;

    @BeforeEach
    void setUp() {
        cookieService = new CookieService();
        response = new MockHttpServletResponse();
    }

    @Test
    @DisplayName("setAccessTokenCookie writes an HttpOnly, Lax cookie with token value")
    void setAccessTokenCookie_writesHeader() {
        cookieService.setAccessTokenCookie(response, "tok-123");

        String setCookie = response.getHeader("Set-Cookie");
        assertThat(setCookie).contains("ACCESS_TOKEN=tok-123");
        assertThat(setCookie).contains("HttpOnly");
        assertThat(setCookie).contains("SameSite=Lax");
        assertThat(setCookie).contains("Path=/");
    }

    @Test
    @DisplayName("setRefreshTokenCookie writes a Strict, Secure cookie scoped to /api/auth")
    void setRefreshTokenCookie_writesHeader() {
        cookieService.setRefreshTokenCookie(response, "refresh-456");

        String setCookie = response.getHeader("Set-Cookie");
        assertThat(setCookie).contains("REFRESH_TOKEN=refresh-456");
        assertThat(setCookie).contains("HttpOnly");
        assertThat(setCookie).contains("Secure");
        assertThat(setCookie).contains("SameSite=Strict");
        assertThat(setCookie).contains("Path=/api/auth");
    }

    @Test
    @DisplayName("clearAccessToken writes a Max-Age=0 cookie")
    void clearAccessToken_writesExpiredCookie() {
        cookieService.clearAccessToken(response);

        String setCookie = response.getHeader("Set-Cookie");
        assertThat(setCookie).contains("ACCESS_TOKEN=");
        assertThat(setCookie).contains("Max-Age=0");
    }

    @Test
    @DisplayName("clearRefreshToken writes a Max-Age=0 cookie scoped to /api/auth")
    void clearRefreshToken_writesExpiredCookie() {
        cookieService.clearRefreshToken(response);

        String setCookie = response.getHeader("Set-Cookie");
        assertThat(setCookie).contains("REFRESH_TOKEN=");
        assertThat(setCookie).contains("Max-Age=0");
        assertThat(setCookie).contains("Path=/api/auth");
    }
}
