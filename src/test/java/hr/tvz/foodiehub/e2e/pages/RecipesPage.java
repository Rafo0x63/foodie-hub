package hr.tvz.foodiehub.e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class RecipesPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public RecipesPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void open(String baseUrl) {
        driver.get(baseUrl + "/recipe-list");
    }

    public boolean isLoaded() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='naslov-recepti']")));
        return !driver.findElements(By.cssSelector("[data-testid='tablica-recepata']")).isEmpty();
    }

    public void clickNewRecipe() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("[data-testid='novi-recept-gumb']")));
        btn.click();
    }

    public void fillRecipeForm(String title, String description, Integer prepTime) {
        fillRecipeForm(title, description, prepTime, "Pizza");
    }

    public void fillRecipeForm(String title, String description, Integer prepTime, String category) {
        clearAndType(By.cssSelector("[data-testid='recept-naziv-unos']"), title);
        clearAndType(By.cssSelector("[data-testid='recept-opis-unos']"), description);
        clearAndType(By.cssSelector("[data-testid='recept-kategorija-unos']"), category);

        if (prepTime != null) {
            clearAndType(By.cssSelector("[data-testid='recept-vrijeme-unos']"), String.valueOf(prepTime));
        }
    }

    public void saveRecipe() {
        WebElement saveBtn = findClickableButton(By.cssSelector("[data-testid='spremi-recept-gumb']"));
        saveBtn.click();
    }

    public boolean isRecipeVisible(String recipeName) {
        try {
            String xpath = "//*[@data-testid='recept-naziv' and normalize-space(.)=" + xpathLiteral(recipeName) + "]";
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public void clickEditRecipeByName(String recipeName) {
        String xpathName = "//*[@data-testid='recept-naziv' and normalize-space(.)=" + xpathLiteral(recipeName) + "]";
        WebElement nameEl = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpathName)));
        WebElement container = nameEl.findElement(By.xpath("ancestor::*[contains(@data-testid,'recept-red-')]"));
        WebElement editBtn = findClickableButton(container, By.xpath(".//*[@data-testid and starts-with(@data-testid,'uredi-recept-')]"));
        editBtn.click();
    }

    public void clickDeleteRecipeByName(String recipeName) {
        String xpathName = "//*[@data-testid='recept-naziv' and normalize-space(.)=" + xpathLiteral(recipeName) + "]";
        WebElement nameEl = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpathName)));
        WebElement container = nameEl.findElement(By.xpath("ancestor::*[contains(@data-testid,'recept-red-')]"));
        WebElement deleteBtn = findClickableButton(container, By.xpath(".//*[@data-testid and starts-with(@data-testid,'obrisi-recept-')]"));
        deleteBtn.click();
    }

    public boolean isValidationErrorDisplayed() {
        return !driver.findElements(By.cssSelector("[data-testid='recept-validacija-greska']")).isEmpty();
    }

    public void waitForRecipeVisible(String recipeName) {
        String xpath = "//*[@data-testid='recept-naziv' and normalize-space(.)=" + xpathLiteral(recipeName) + "]";
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
    }

    public void waitForRecipeNotVisible(String recipeName) {
        String xpath = "//*[@data-testid='recept-naziv' and normalize-space(.)=" + xpathLiteral(recipeName) + "]";
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(xpath)));
    }

    private void clearAndType(By locator, String value) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        element.clear();
        element.sendKeys(value);
    }

    private WebElement findClickableButton(By locator) {
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        return findClickableButton(element);
    }

    private WebElement findClickableButton(WebElement parent, By locator) {
        WebElement element = parent.findElement(locator);
        return findClickableButton(element);
    }

    private WebElement findClickableButton(WebElement element) {
        if ("button".equalsIgnoreCase(element.getTagName())) {
            return wait.until(ExpectedConditions.elementToBeClickable(element));
        }

        WebElement nestedButton = element.findElement(By.cssSelector("button"));
        return wait.until(ExpectedConditions.elementToBeClickable(nestedButton));
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
}
