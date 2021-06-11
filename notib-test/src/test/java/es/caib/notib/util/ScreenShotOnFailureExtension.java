package es.caib.notib.util;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.File;
import java.util.Optional;

public class ScreenShotOnFailureExtension implements TestWatcher {

    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {
        DriverManager.getDriver().quit();
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        DriverManager.getDriver().quit();
    }

    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        DriverManager.getDriver().quit();
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        takeScreenshot(
                context.getRequiredTestClass().getSimpleName(),
                context.getRequiredTestMethod().getName());
        DriverManager.getDriver().quit();
    }

    public void takeScreenshot(String className, String methodName) {
        try {
            File src = ((TakesScreenshot) DriverManager.getDriver()).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(src, new File("screenshots/" + className + "/" + methodName + ".png"));
        } catch (Exception e) {
            System.err.println("Error guardant la captura:" + e.getMessage());
            e.printStackTrace();
        }
    }

}
