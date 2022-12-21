package es.caib.notib.selenium;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.concurrent.TimeUnit;

public class Test {

    public static void main(String [] args) throws Exception {

        var driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(4, TimeUnit.SECONDS);
        try {
            var ls = new LoginSelenium(driver);
            ls.login();
            while(true) {

                var ns = new RemesesSelenium(driver);
                ns.test();
                var es = new EnviamentsSelenium(driver);
                es.test();
            }
        }  finally {
            Thread.sleep(1000000);
            driver.quit();
        }
    }
}
