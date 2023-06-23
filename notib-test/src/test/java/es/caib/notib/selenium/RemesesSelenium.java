
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
            netejarFiltres("btn-netejar-filtre");

        } catch (Exception ex) {
            System.out.println("Selenium error");
            System.out.println(ex);

        }
    }

    private void filtrar() {

        Select tipus = new Select(driver.findElement(By.id("enviamentTipus")));
        tipus.selectByIndex(2);
        WebElement concepte = driver.findElement(By.id("concepte"));
        concepte.sendKeys("test");
        Select estat = new Select(driver.findElement(By.id("estat")));
        estat.selectByIndex(9);
        Select organ = new Select(driver.findElement(By.id("organGestor")));
        organ.selectByValue("18445"); // organ amb codi A04035942
        WebElement button = driver.findElement(By.name("accio"));
        button.click();
    }

    private String detallTaula() {

        esperar();
        List<WebElement> files = driver.findElements(By.tagName("tr"));
        int size = files.size()- 1;
        Random r = new Random();
        Integer inici = 1;
        Integer fi = size;
        Integer resultat = r.nextInt(fi-inici) + inici;
        WebElement fila = files.get(resultat);
        fila.click();
        return fila.getAttribute("id").split("_")[1];
    }

    private void tancar(String id) {

        get(urlBase + "/modal/notificacio/" + id + "/info");
        List<WebElement> cancelar = driver.findElements(By.tagName("a"));
        WebElement a = cancelar.get(cancelar.size()-1);
        esperar();
        a.click();
    }

}
