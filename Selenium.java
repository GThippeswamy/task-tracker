
    package project;

import java.time.Duration;
import java.util.List;
import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;

public class Selenium {
    WebDriver driver;
    WebDriverWait wait;

    @BeforeClass
    public void setup() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://rahulshettyacademy.com/seleniumPractise/#/");
    }

    @Test(priority = 1)
    public void searchAndAddToCartTest() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.findElement(By.cssSelector("input.search-keyword")).sendKeys("a");
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("div.products div.product")));
        List<WebElement> products = driver.findElements(By.cssSelector("div.products div.product"));
        if (products.size() > 0) {
            Random random = new Random();
            int index = random.nextInt(products.size());
            WebElement selectedProduct = driver.findElements(By.cssSelector("div.products div.product")).get(index);
            wait.until(ExpectedConditions.visibilityOf(selectedProduct));
            selectedProduct.findElement(By.xpath(".//div[@class='product-action']/button")).click();
        }
    }

    @Test(priority = 2)
    public void topDealsPageTest() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.findElement(By.linkText("Top Deals")).click();
        for (String handle : driver.getWindowHandles()) {
            driver.switchTo().window(handle);
        }
        WebElement search = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[type='search']")));
        search.sendKeys("Rice");
    }
    @Test(priority = 3)
    public void flightBookingTest() {
        driver.get("https://rahulshettyacademy.com/dropdownsPractise/");

        WebElement fromInput = driver.findElement(By.id("ctl00_mainContent_ddl_originStation1_CTXT"));
        fromInput.click();
        driver.findElement(By.xpath("//a[@value='BLR']")).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//a[@value='MAA'])[2]"))).click();

        driver.findElement(By.id("ctl00_mainContent_view_date1")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".ui-datepicker-calendar")));
        driver.findElement(By.xpath("//a[text()='15']")).click();

        driver.findElement(By.id("ctl00_mainContent_btn_FindFlights")).click();
        System.out.println("✈️ Flight booking search triggered.");
    }


    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}


