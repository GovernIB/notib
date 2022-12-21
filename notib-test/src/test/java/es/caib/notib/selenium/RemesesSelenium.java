
package es.caib.notib.selenium;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;
import java.util.Random;

public class RemesesSelenium extends NotibSelenium {

    public RemesesSelenium(WebDriver driver) {
        super(driver);
    }
    public void test() throws Exception {

        try {
            navegarRemeses();
            filtrar();
            String id = detallTaula();
            tancar(id);
            natejarFiltres("btn-netejar-filtre");

        } catch (Exception ex) {
            System.out.println("Selenium error");
            System.out.println(ex);

        }
    }

    private void filtrar() {

        var tipus = new Select(driver.findElement(By.id("enviamentTipus")));
        tipus.selectByIndex(2);
        var concepte = driver.findElement(By.id("concepte"));
        concepte.sendKeys("test");
        var estat = new Select(driver.findElement(By.id("estat")));
        estat.selectByIndex(9);
        var organ = new Select(driver.findElement(By.id("organGestor")));
        organ.selectByValue("18445"); // organ amb codi A04035942
        var button = driver.findElement(By.name("accio"));
        button.click();
    }

    private String detallTaula() {

        esperar();
        var files = driver.findElements(By.tagName("tr"));
        int size = files.size()- 1;
        var r = new Random();
        var inici = 1;
        var fi = size;
        var resultat = r.nextInt(fi-inici) + inici;
        var fila = files.get(resultat);
        fila.click();
        return fila.getAttribute("id").split("_")[1];
    }

    private void tancar(String id) {

        get(urlBase + "/modal/notificacio/" + id + "/info");
        var cancelar = driver.findElements(By.tagName("a"));
        var a = cancelar.get(cancelar.size()-1);
        esperar();
        a.click();
    }

}
