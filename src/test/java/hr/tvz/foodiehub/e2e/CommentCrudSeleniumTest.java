package hr.tvz.foodiehub.e2e;

import hr.tvz.foodiehub.e2e.pages.LoginPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CommentCrudSeleniumTest extends BaseSeleniumTest {

    private void loginAsUser() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open(baseUrl);
        String testEmail = "ivan@gmail.com";
        String testPassword = "password123";
        loginPage.login(testEmail, testPassword);
    }

    private void openRecipeDetail() {
        long recipeId = 1L;
        driver.get(baseUrl + "/recipe/" + recipeId);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='recipe-detail-title']")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='comments-section']")));
    }

    private void addComment(String text, int rating) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement textInput = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("[data-testid='comment-text-input']")));
        textInput.clear();
        textInput.sendKeys(text);

        Select ratingSelect = new Select(driver.findElement(By.cssSelector("[data-testid='comment-rating-select']")));
        ratingSelect.selectByVisibleText(rating + "/5");

        driver.findElement(By.cssSelector("[data-testid='comment-submit-button']")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(commentTextLocator(text)));
    }

    private WebElement findCommentByText(String text) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(commentContainerLocator(text)));
    }

    private By commentTextLocator(String text) {
        return By.xpath("//*[contains(normalize-space(.), " + xpathLiteral(text) + ")]");
    }

    private By commentContainerLocator(String text) {
        return By.xpath("//*[@data-testid and starts-with(@data-testid,'comment-item-')][.//*[contains(normalize-space(.), " + xpathLiteral(text) + ")]]");
    }

    private String xpathLiteral(String value) {
        if (!value.contains("'")) {
            return "'" + value + "'";
        }

        if (!value.contains("\"")) {
            return "\"" + value + "\"";
        }

        return "concat('" + value.replace("'", "',\"'\",'") + "')";
    }

    @Test
    @DisplayName("Prikaz sekcije komentara na detalju recepta")
    void testCommentsSectionIsDisplayed() {
        loginAsUser();
        openRecipeDetail();

        assertTrue(driver.findElement(By.cssSelector("[data-testid='comments-section']")).isDisplayed(),
                "Sekcija komentara nije prikazana");
        assertTrue(driver.findElement(By.cssSelector("[data-testid='comment-text-input']")).isDisplayed(),
                "Polje za unos komentara nije prikazano");
        assertTrue(driver.findElement(By.cssSelector("[data-testid='comment-rating-select']")).isDisplayed(),
                "Polje za ocjenu nije prikazano");
    }

    @Test
    @DisplayName("Dodavanje novog komentara")
    void testCreateCommentSuccessfully() {
        loginAsUser();
        openRecipeDetail();

        String commentText = "E2E komentar " + System.currentTimeMillis();
        addComment(commentText, 5);

        assertTrue(driver.findElement(commentTextLocator(commentText)).isDisplayed(),
                "Novi komentar nije vidljiv u listi");
    }

    @Test
    @DisplayName("Validacija forme komentara")
    void testCommentValidation() {
        loginAsUser();
        openRecipeDetail();

        driver.findElement(By.cssSelector("[data-testid='comment-submit-button']")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement validationError = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("[data-testid='comment-validation-error']")
        ));

        assertTrue(validationError.isDisplayed(), "Validacijska poruka za komentar nije prikazana");
    }

    @Test
    @DisplayName("Uredjivanje postojeceg komentara")
    void testEditCommentSuccessfully() {
        loginAsUser();
        openRecipeDetail();

        String originalText = "E2E komentar za edit " + System.currentTimeMillis();
        String updatedText = originalText + " updated";
        addComment(originalText, 4);

        WebElement comment = findCommentByText(originalText);
        comment.findElement(By.xpath(".//*[@data-testid and starts-with(@data-testid,'comment-edit-button-')]")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement editInput = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[@data-testid and starts-with(@data-testid,'comment-edit-text-input-')]")
        ));
        editInput.clear();
        editInput.sendKeys(updatedText);

        Select ratingSelect = new Select(driver.findElement(By.xpath("//*[@data-testid and starts-with(@data-testid,'comment-edit-rating-select-')]")));
        ratingSelect.selectByVisibleText("5/5");

        driver.findElement(By.xpath("//*[@data-testid and starts-with(@data-testid,'comment-save-button-')]")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(commentTextLocator(updatedText)));
        assertTrue(driver.findElement(commentTextLocator(updatedText)).isDisplayed(),
                "Azurirani komentar nije vidljiv");
    }

    @Test
    @DisplayName("Brisanje postojeceg komentara")
    void testDeleteCommentSuccessfully() {
        loginAsUser();
        openRecipeDetail();

        String commentText = "E2E komentar za brisanje " + System.currentTimeMillis();
        addComment(commentText, 3);

        WebElement comment = findCommentByText(commentText);
        comment.findElement(By.xpath(".//*[@data-testid and starts-with(@data-testid,'comment-delete-button-')]")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(commentTextLocator(commentText)));

        assertFalse(driver.findElements(commentTextLocator(commentText)).stream().anyMatch(WebElement::isDisplayed),
                "Komentar je i dalje vidljiv nakon brisanja");
    }
}
