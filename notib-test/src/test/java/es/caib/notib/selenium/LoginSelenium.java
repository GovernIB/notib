package es.caib.notib.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class LoginSelenium  extends NotibSelenium{

    public LoginSelenium(WebDriver driver) {
        super(driver);
    }

    void login() {

        this.driver.get(urlBase);
        var user = driver.findElement(By.id("j_username"));
        user.sendKeys("admin");
        var pass = driver.findElement(By.id("j_password"));
        pass.sendKeys("admin");
        var button = driver.findElement(By.name("formUCboton"));
        button.click();
    }
}
