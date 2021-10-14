package es.caib.notib.util;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class DriverManager {

    private static WebDriver driver;
    private static String driver_name;

    public static WebDriver createDriver() {
        if (driver_name == null) {
            driver_name = "geckodriver";
            String os = System.getProperty("os.name");
            if (os != null && os.isBlank() && os.toLowerCase().startsWith("windows")) {
                driver_name += ".exe";
            }
        }
        System.setProperty("webdriver.gecko.driver", resolveTestResourcePath(driver_name));
        driver = new FirefoxDriver();
        driver.manage()
                .timeouts()
                .implicitlyWait(10, TimeUnit.SECONDS);
        return driver;
    }

    public static WebDriver getDriver() {
        return driver;
    }

    private static String resolveTestResourcePath(String filename) {
        File file = new File("src/test/resources/" + filename);
        return file.getAbsolutePath();
    }
}
