package hr.tvz.foodiehub.e2e;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AuthE2ETest extends BaseSeleniumTest {

    @Test
    @DisplayName("Uspješan prikaz stranice za prijavu")
    void testLoginPageElements() {
        driver.get(baseUrl + "/login");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@data-testid='email-unos']")));

        WebElement emailInput = driver.findElement(By.xpath("//*[@data-testid='email-unos']"));
        WebElement passwordInput = driver.findElement(By.xpath("//*[@data-testid='lozinka-unos']"));
        WebElement loginButton = driver.findElement(By.xpath("//*[@data-testid='prijava-gumb']"));

        assertTrue(emailInput.isDisplayed(), "Email input should be displayed");
        assertTrue(passwordInput.isDisplayed(), "Password input should be displayed");
        assertTrue(loginButton.isDisplayed(), "Login button should be displayed");
        
        assertEquals("vas.email@primjer.com", emailInput.getAttribute("placeholder"), "Email placeholder should be correct");
    }

    @Test
    @DisplayName("Uspješna prijava")
    void testSuccessfulLogin() {
        driver.get(baseUrl + "/login");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@data-testid='email-unos']")));

        WebElement emailInput = driver.findElement(By.xpath("//*[@data-testid='email-unos']"));
        WebElement passwordInput = driver.findElement(By.xpath("//*[@data-testid='lozinka-unos']"));
        WebElement loginButton = driver.findElement(By.xpath("//*[@data-testid='prijava-gumb']"));

        emailInput.sendKeys("ivan@gmail.com");
        passwordInput.sendKeys("password123");
        loginButton.click();

        wait.until(ExpectedConditions.urlContains("/homepage"));

        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("/homepage"), "Should be redirected to homepage after login");

        WebElement welcomeText = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@data-testid='naslov-pocetna']")));
        assertTrue(welcomeText.getText().contains("Dijeli recepte"), "Početna stranica nije učitana");
    }

    @Test
    @DisplayName("Neuspješna prijava")
    void testUnsuccessfulLogin() {
        driver.get(baseUrl + "/login");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@data-testid='email-unos']")));

        WebElement emailInput = driver.findElement(By.xpath("//*[@data-testid='email-unos']"));
        WebElement passwordInput = driver.findElement(By.xpath("//*[@data-testid='lozinka-unos']"));
        WebElement loginButton = driver.findElement(By.xpath("//*[@data-testid='prijava-gumb']"));

        emailInput.sendKeys("krivi@email.com");
        passwordInput.sendKeys("tralalala");
        loginButton.click();

        WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@data-testid='poruka-pogreska']")));
        assertTrue(errorMessage.getText().contains("Neispravan email ili lozinka"), "Ne prikazuje se poruka o neuspjeloj prijavi");
    }

    @Test
    @DisplayName("Uspješna odjava")
    void testLogout() {
        driver.get(baseUrl + "/login");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@data-testid='email-unos']"))).sendKeys("ivan@gmail.com");
        driver.findElement(By.xpath("//*[@data-testid='lozinka-unos']")).sendKeys("password123");
        driver.findElement(By.xpath("//*[@data-testid='prijava-gumb']")).click();
        
        wait.until(ExpectedConditions.urlContains("/homepage"));

        WebElement logoutButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@data-testid='odjava-gumb']")));
        logoutButton.click();

        wait.until(ExpectedConditions.urlContains("/login"));
        assertTrue(driver.getCurrentUrl().contains("/login"), "Neuspješan redirect na stranicu za prijavu nakon odjave");
    }

    @Test
    @DisplayName("Preusmjeravanje bez prijave")
    void testProtectedRouteRedirect() {
        driver.get(baseUrl + "/add-recipe");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        
        wait.until(ExpectedConditions.urlContains("/login"));
        
        assertTrue(driver.getCurrentUrl().contains("/login"), "Neuspješan redirect na stranicu za prijavu nakon pokušaja neovlaštenog pristupa");
    }
}
