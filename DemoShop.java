package practicePROBLEMS;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.List;

public class DemoShop {
    WebDriver driver;
    WebDriverWait wait;
    String email = "krishna" + System.currentTimeMillis() + "@gmail.com";
    String password = "Krishna123";

    @BeforeMethod
    public void setup() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.manage().window().maximize();
        driver.get("https://demowebshop.tricentis.com/");
    }

    @Test
    public void demoWebShopFlow() throws InterruptedException {
        register();
        logout();
        login();
        wishlist();
        searchAndAdd();
        addCategories();
        verifyFooter();
        checkCart();
        completeCheckout();
        logout();
    }

    public void register() {
        driver.findElement(By.linkText("Register")).click();
        driver.findElement(By.id("gender-male")).click();
        driver.findElement(By.id("FirstName")).sendKeys("Krishna");
        driver.findElement(By.id("LastName")).sendKeys("Yadav");
        driver.findElement(By.id("Email")).sendKeys(email);
        driver.findElement(By.id("Password")).sendKeys(password);
        driver.findElement(By.id("ConfirmPassword")).sendKeys(password);
        driver.findElement(By.id("register-button")).click();
        driver.findElement(By.className("register-continue-button")).click();
        System.out.println("Registered");
    }

    public void login() {
        driver.findElement(By.linkText("Log in")).click();
        driver.findElement(By.id("Email")).sendKeys(email);
        driver.findElement(By.id("Password")).sendKeys(password);
        driver.findElement(By.cssSelector("input.button-1.login-button")).click();
        System.out.println("Logged in");
    }

    public void logout() {
        try {
            driver.findElement(By.linkText("Log out")).click();
            System.out.println("Logged out");
        } catch (Exception e) {
            System.out.println("Logout not found");
        }
    }

    public void wishlist() {
        driver.findElement(By.linkText("Books")).click();
        List<WebElement> items = driver.findElements(By.cssSelector("input[value='Add to wishlist']"));
        if (!items.isEmpty()) items.get(0).click();
        driver.findElement(By.linkText("Wishlist")).click();
        int count = driver.findElements(By.cssSelector(".wishlist-content .product-name")).size();
        System.out.println("Wishlist items: " + count);
    }

    public void searchAndAdd() {
        driver.findElement(By.name("q")).sendKeys("computer");
        driver.findElement(By.cssSelector("input[value='Search']")).click();
        driver.findElement(By.linkText("Simple Computer")).click();
        driver.findElement(By.id("product_attribute_75_5_31_96")).click();
        driver.findElement(By.id("product_attribute_75_6_32_99")).click();
        driver.findElement(By.id("product_attribute_75_3_33_102")).click();
        driver.findElement(By.id("product_attribute_75_8_35_107")).click();
        WebElement qty = driver.findElement(By.id("addtocart_75_EnteredQuantity"));
        qty.clear();
        qty.sendKeys("2");
        driver.findElement(By.id("add-to-cart-button-75")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("bar-notification")));
        driver.get("https://demowebshop.tricentis.com/");
    }

    public void addCategories() {
    	 String[] res = {
    	            "Books", "Computers", "Electronics",
    	            "Apparel & Shoes", "Digital downloads", 
    	            "Jewelry", "Gift Cards"
    	        };

    	        for (String cat : res) {
    	            try {
    	                driver.get("https://demowebshop.tricentis.com/");
    	                driver.findElement(By.linkText(cat)).click();

    	                List<WebElement> Sublink = driver.findElements(By.xpath("//ul[@class='sublist']/li/a"));

    	                if (!Sublink.isEmpty()) {
    	                    for (int i = 0; i < Sublink.size(); i++) {
    	                        String subLinkUrl = Sublink.get(i).getAttribute("href");
    	                        driver.get(subLinkUrl);

    	                        List<WebElement> box = driver.findElements(By.xpath("//h2[@class='product-title']/a"));
    	                        if (box.size() > 1) {
    	                            box.get(1).click(); 
    	                            driver.findElement(By.xpath("//input[@class='button-2 product-box-add-to-cart-button']")).click();
    	                            System.out.println(" Product added from subcategory: " + subLinkUrl);
    	                        } else {
    	                            System.out.println(" No second product in subcategory: " + subLinkUrl);
    	                        }
    	                    }
    	                } else {
    	                    
    	                    List<WebElement> box = driver.findElements(By.xpath("//h2[@class='product-title']/a"));
    	                    if (box.size() > 1) {
    	                        box.get(1).click();
    	                        driver.findElement(By.xpath("//input[@class='button-2 product-box-add-to-cart-button']")).click();
    	                        System.out.println("Product added from main category: " + cat);
    	                    } else {
    	                        System.out.println(" No second product in main category: " + cat);
    	                    }
    	                }
    	            } catch (Exception e) {
    	                System.out.println("⚠️ Error in category: " + cat + " - " + e.getMessage());
    	            }
    	        }
    }

    public void verifyFooter() {
        System.out.println("Manufacturer present: " + driver.getPageSource().contains("Tricentis"));
        String[] links = {"Sitemap", "Shipping & Returns", "Privacy Notice", "Conditions of Use", "About us", "Contact us"};
        for (String link : links) {
            System.out.println(link + ": " + (driver.findElements(By.linkText(link)).size() > 0));
        }
        System.out.println("Logo displayed: " + driver.findElement(By.cssSelector(".header-logo a")).isDisplayed());
        System.out.println("Footer displayed: " + driver.findElement(By.className("footer")).isDisplayed());
    }

    public void checkCart() {
        driver.findElement(By.linkText("Shopping cart")).click();
        List<WebElement> cartItems = driver.findElements(By.cssSelector(".cart-item-row .product-name"));
        System.out.println("Cart items: " + cartItems.size());
        for (WebElement item : cartItems) System.out.println(" - " + item.getText());
    }

    public void completeCheckout() throws InterruptedException {
        driver.findElement(By.linkText("Shopping cart")).click();
        driver.findElement(By.id("termsofservice")).click();
        driver.findElement(By.id("checkout")).click();

        try {
            WebElement dropdown = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("billing-address-select")));
            new Select(dropdown).selectByIndex(0);
        } catch (Exception e) {
            driver.findElement(By.id("BillingNewAddress_FirstName")).clear();
            driver.findElement(By.id("BillingNewAddress_FirstName")).sendKeys("Krishna");
            driver.findElement(By.id("BillingNewAddress_LastName")).clear();
            driver.findElement(By.id("BillingNewAddress_LastName")).sendKeys("Yadav");
            driver.findElement(By.id("BillingNewAddress_Email")).clear();
            driver.findElement(By.id("BillingNewAddress_Email")).sendKeys(email);
            new Select(driver.findElement(By.id("BillingNewAddress_CountryId"))).selectByVisibleText("India");
            driver.findElement(By.id("BillingNewAddress_City")).sendKeys("Anantapur");
            driver.findElement(By.id("BillingNewAddress_Address1")).sendKeys("Main Street");
            driver.findElement(By.id("BillingNewAddress_ZipPostalCode")).sendKeys("515001");
            driver.findElement(By.id("BillingNewAddress_PhoneNumber")).sendKeys("9876543210");
        }

        driver.findElement(By.cssSelector("input.button-1.new-address-next-step-button")).click();
        Thread.sleep(2000);

        driver.findElement(By.id("PickUpInStore")).click();
        driver.findElement(By.xpath("(//input[@class='button-1 new-address-next-step-button'])[2]")).click();
        Thread.sleep(2000);

        driver.findElement(By.xpath("//ul[@class='method-list']/li//input[@id='shippingoption_2']")).click();
        driver.findElement(By.xpath("(//input[@class='button-1 new-address-next-step-button'])[3]")).click();
        Thread.sleep(2000);
        
    
        driver.findElement(By.xpath("//div[@id='paymentmethod_1']//input")).click();

;        driver.findElement(By.xpath("(//input[@class='button-1 new-address-next-step-button'])[4]")).click();
         Thread.sleep(2000);
        
        driver.findElement(By.xpath("(//input[@class='button-1 new-address-next-step-button'])[5]")).click();
        Thread.sleep(2000);
        
        
        driver.findElement(By.xpath("//input[@value='Confirm']")).click();
        Thread.sleep(2000);
        
       
    }


    @AfterMethod
    public void teardown() throws InterruptedException {
        Thread.sleep(2000);
        driver.quit();
    }
}
