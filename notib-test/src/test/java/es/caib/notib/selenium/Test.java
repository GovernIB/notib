package es.caib.notib.selenium;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.concurrent.TimeUnit;

public class Test {

    public static void main(String [] args) throws Exception {

        Thread t;
        for (int i = 0; i<1;i++) {

            t = new Thread(() -> {
                var driver = new ChromeDriver();
                driver.manage().timeouts().implicitlyWait(4, TimeUnit.SECONDS);
                try {
                    var ls = new LoginSelenium(driver);
                    ls.login();
                    while (true) {
                        var ns = new RemesesSelenium(driver);
                        ns.test();
                        var es = new EnviamentsSelenium(driver);
                        es.test();
                    }
                } catch (Exception ex) {
                    System.out.println("Exception: " + ex);
                } finally {
                    try {
                        Thread.sleep(1000000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    driver.quit();
                }
            });
            t.start();
        }
    }
}
