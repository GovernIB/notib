package es.caib.notib.selenium;
import com.google.common.base.Strings;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class EnviamentsSelenium extends  NotibSelenium {

    private static String path = "/enviament";
    private static String urlEnviaments = urlBase + path;

    public EnviamentsSelenium(WebDriver driver) {
        super(driver);
    }

    public void filtrar() {

        var nom = driver.findElement(By.name("titularNomLlinatge"));
        nom.sendKeys("test");
        driver.findElement(By.id("btnFiltrar")).click();
    }

    public void modificarColumnes() {

        esperar();
        var path = "/notib/enviament/visualitzar";
        var aTags = driver.findElements(By.tagName("a"));
        WebElement boto = null;
        String href;
        for (var a : aTags) {
            href = a.getAttribute("href");
            if (!Strings.isNullOrEmpty(href) && href.contains(path)) {
                boto = a;
                break;
            }
        }
        if (boto == null) {
            return;
        }
        boto.click();
        esperar();
        get(urlBase + "/modal/enviament/visualitzar");
        var check = driver.findElement(By.id("titularEmail"));
        check.click();
        driver.findElement(By.tagName("button")).click();
        get(urlBase + "/enviament");
    }

    public void test() throws Exception {

        try {
            navegarEnviaments();
            esperar();
            filtrar();
            modificarColumnes();
            netejarFiltres("btnNetejar");
        } catch (Exception ex) {
            System.out.println("Selenium error");
            System.out.println(ex);
        }
    }
}
