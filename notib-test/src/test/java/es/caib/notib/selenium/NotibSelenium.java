package es.caib.notib.selenium;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class NotibSelenium {

	WebDriver driver;
	static String urlBase = "http://localhost:8280/notib";

	public NotibSelenium(WebDriver driver) {

		this.driver = driver != null ? driver : new ChromeDriver();
	}

	void esperar(WebElement ... elem) {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
		//ºwait.until(ExpectedConditions.stalenessOf(elem));
	}

	void navegarRemeses() {

		esperar();
		driver.findElement(By.id("ml_notificacio")).click();
	}

	void navegarEnviaments() {

		esperar();
		driver.findElement(By.id("ml_enviament")).click();
	}

	void natejarFiltres(String id) {

		var boto = driver.findElement(By.id(id));
		esperar();
		boto.click();
	}

	void get(String url) {
		driver.get(url);
	}
}
