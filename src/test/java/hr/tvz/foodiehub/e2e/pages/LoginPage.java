package hr.tvz.foodiehub.e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By emailInput = By.cssSelector("[data-testid='email-unos']");
    private final By passwordInput = By.cssSelector("[data-testid='lozinka-unos']");
    private final By loginButton = By.cssSelector("[data-testid='prijava-gumb']");
    private final By errorMessage = By.cssSelector("[data-testid='poruka-pogreska']");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void open(String baseUrl) {
        driver.get(baseUrl + "/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(emailInput));
    }

    public void login(String email, String password) {
        clearAndType(emailInput, email);
        clearAndType(passwordInput, password);
        clickLogin();
        wait.until(ExpectedConditions.urlContains("/homepage"));
    }

    public void clickLogin() {
        WebElement button = wait.until(ExpectedConditions.presenceOfElementLocated(loginButton));
        findClickableButton(button).click();
    }

    public boolean isLoaded() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(emailInput)).isDisplayed()
                && driver.findElement(passwordInput).isDisplayed()
                && driver.findElement(loginButton).isDisplayed();
    }

    public boolean isErrorDisplayed() {
        return !driver.findElements(errorMessage).isEmpty()
                && driver.findElement(errorMessage).isDisplayed();
    }

    private void clearAndType(By locator, String value) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        element.clear();
        element.sendKeys(value);
    }

    private WebElement findClickableButton(WebElement element) {
        if ("button".equalsIgnoreCase(element.getTagName())) {
            return wait.until(ExpectedConditions.elementToBeClickable(element));
        }

        WebElement nestedButton = element.findElement(By.cssSelector("button"));
        return wait.until(ExpectedConditions.elementToBeClickable(nestedButton));
    }
}
