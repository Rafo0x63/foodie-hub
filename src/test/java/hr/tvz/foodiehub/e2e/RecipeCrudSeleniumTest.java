package hr.tvz.foodiehub.e2e;

import hr.tvz.foodiehub.e2e.pages.LoginPage;
import hr.tvz.foodiehub.e2e.pages.RecipesPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RecipeCrudSeleniumTest extends BaseSeleniumTest {

    private final String testEmail = "ivan@gmail.com";
    private final String testPassword = "password123";

    private void loginAsUser() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open(baseUrl);
        loginPage.login(testEmail, testPassword);
    }

    private void createRecipeThroughUi(String recipeName) {
        RecipesPage recipesPage = new RecipesPage(driver);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        recipesPage.open(baseUrl);
        wait.until(ExpectedConditions.urlContains("/recipe-list"));
        recipesPage.clickNewRecipe();
        recipesPage.fillRecipeForm(recipeName, "Opis za " + recipeName, 15, "Pizza");
        recipesPage.saveRecipe();

        wait.until(ExpectedConditions.urlContains("/recipe-list"));
        recipesPage.waitForRecipeVisible(recipeName);
    }

    @Test
    @DisplayName("Prikaz stranice recepata nakon prijave")
    void testRecipesPageIsDisplayedAfterLogin() {
        loginAsUser();

        RecipesPage recipesPage = new RecipesPage(driver);
        recipesPage.open(baseUrl);

        assertTrue(recipesPage.isLoaded(), "Stranica recepata se nije ucitala pravilno");
    }

    @Test
    @DisplayName("Kreiranje novog recepta kroz UI")
    void testCreateNewRecipeSuccessfully() {
        loginAsUser();

        String recipeName = "E2E Recept " + System.currentTimeMillis();
        createRecipeThroughUi(recipeName);

        RecipesPage recipesPage = new RecipesPage(driver);
        assertTrue(recipesPage.isRecipeVisible(recipeName), "Novi recept nije vidljiv u listi");
    }

    @Test
    @DisplayName("Validacija forme recepta - neuspjesno kreiranje praznog recepta")
    void testRecipeFormValidation() {
        loginAsUser();

        RecipesPage recipesPage = new RecipesPage(driver);
        recipesPage.open(baseUrl);
        recipesPage.clickNewRecipe();
        recipesPage.saveRecipe();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(driver -> recipesPage.isValidationErrorDisplayed());

        assertTrue(recipesPage.isValidationErrorDisplayed(), "Validacijska poruka za prazan recept nije prikazana");
    }

    @Test
    @DisplayName("Uredjivanje postojeceg recepta")
    void testEditExistingRecipeSuccessfully() {
        loginAsUser();

        String recipeName = "E2E Recept " + System.currentTimeMillis();
        createRecipeThroughUi(recipeName);

        RecipesPage recipesPage = new RecipesPage(driver);
        recipesPage.open(baseUrl);
        recipesPage.clickEditRecipeByName(recipeName);

        String updatedName = recipeName + " - Edited";
        recipesPage.fillRecipeForm(updatedName, "Azurirani opis", 20, "Pizza");
        recipesPage.saveRecipe();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("/recipe-list"));
        recipesPage.waitForRecipeVisible(updatedName);

        assertTrue(recipesPage.isRecipeVisible(updatedName), "Azurirani naziv recepta nije vidljiv u listi");
    }

    @Test
    @DisplayName("Brisanje postojeceg recepta")
    void testDeleteExistingRecipeSuccessfully() {
        loginAsUser();

        String recipeName = "E2E Recept " + System.currentTimeMillis();
        createRecipeThroughUi(recipeName);

        RecipesPage recipesPage = new RecipesPage(driver);
        recipesPage.open(baseUrl);
        recipesPage.clickDeleteRecipeByName(recipeName);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.alertIsPresent());
        Alert alert = driver.switchTo().alert();
        alert.accept();

        recipesPage.waitForRecipeNotVisible(recipeName);

        assertFalse(recipesPage.isRecipeVisible(recipeName), "Recept i dalje postoji u listi nakon brisanja");
    }
}
