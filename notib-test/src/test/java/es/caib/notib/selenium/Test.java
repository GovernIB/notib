package es.caib.notib.selenium;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.concurrent.TimeUnit;

public class Test {

    public static void main(String [] args) throws Exception {

        WebDriver driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(4, TimeUnit.SECONDS);
        try {
            LoginSelenium ls = new LoginSelenium(driver);
            ls.login();
            while(true) {

                RemesesSelenium ns = new RemesesSelenium(driver);
                ns.test();
                EnviamentsSelenium es = new EnviamentsSelenium(driver);
                es.test();
            }
        }  finally {
            Thread.sleep(1000000);
            driver.quit();
        }
    }
}
