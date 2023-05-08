package es.caib.notib.front;

import es.caib.notib.util.DriverManager;
import es.caib.notib.util.ScreenShotOnFailureExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(ScreenShotOnFailureExtension.class)
public class EntitatTest {

    private final static String URL = "https://dev.caib.es/notib";
    private final static String USER = "e18225486x";
    private final static String PASS = "XXXXXXX";

    private static WebDriver driver;

    @BeforeAll
    public static void setUp() {
        driver = DriverManager.createDriver();
    }

    @Test
    void test() {
        loginCaib(USER, PASS);

        // Seleccionar el rol Superusuari
        WebElement menu_rols = driver.findElement(By.xpath("//span[contains(@class, 'fa-bookmark')]/ancestor::a"));
        if (!menu_rols.getText().contains("Superusuari")) {
            System.out.println("Superusuari no seleccionat");
            menu_rols.click();
            driver.findElement(By.linkText("Superusuari")).click();
        } else {
            System.out.println("Superusuari seleccionat");
        }

        // Anar al llistat d'entitats
        driver.findElement(By.xpath("//button[contains(text(),'Configuració')]")).click();
        driver.findElement(By.xpath("//a[contains(text(),'Entitats')]")).click();

        // Click a la primera columna (Codi) per a ordenar
        driver.findElement(By.xpath("//tr/th[1]")).click();

        // Obtenim tots els codis
        List<WebElement> elementsCodis = driver.findElements(By.xpath("//tr/td[1]"));
        // Eliminam la última fila, que és la fila de paginació
        elementsCodis.remove(elementsCodis.size() - 1);
        List<String> codis = elementsCodis.stream().map(e -> e.getText()).collect(Collectors.toList());

        // Ordenam la llista
        List<String> sortedCodis = codis.stream().sorted().collect(Collectors.toList());

        // Comparam la llista original amb la ordenada
        assertTrue(codis.equals(sortedCodis));

    }

    protected void loginCaib(String user, String pass) {
        driver.get(URL);

        driver.findElement(By.xpath("//*[@id='j_username']")).sendKeys(user);
        driver.findElement(By.xpath("//*[@id='j_password']")).sendKeys(pass);
        driver.findElement(By.xpath("//*[@id='usuariclau']/form/p[3]/input")).click();
    }
}
