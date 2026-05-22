package hr.tvz.foodiehub.e2e;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DeploySmokeE2ETest extends BaseSeleniumTest {

    @Test
    @DisplayName("Deployani frontend odgovara")
    void testDeployedFrontendResponds() {
        driver.get(baseUrl);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        assertNotNull(
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body"))),
                "Body element nije ucitan"
        );

        assertFalse(driver.getTitle().isBlank(), "Title deployane stranice je prazan");
        assertFalse(driver.getCurrentUrl().startsWith("chrome-error://"), "Browser je otvorio error stranicu");
        assertFalse(driver.getPageSource().contains("ERR_CONNECTION_REFUSED"), "Deploy ne odgovara");
    }
}
