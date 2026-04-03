import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class test_assessment {

    public static void main(String[] args) throws Exception {

        Properties prop = new Properties();
        InputStream fis = test_assessment.class.getClassLoader().getResourceAsStream("config.properties");

        if (fis == null) {
            throw new RuntimeException("config.properties not found in resources folder");
        }

        prop.load(fis);

        String baseUrl = prop.getProperty("baseUrl");
        String loginUsername = prop.getProperty("loginUsername");
        String loginPassword = prop.getProperty("loginPassword");
        String employeeName = prop.getProperty("employeeName");
        String newPassword = prop.getProperty("newUserPassword");
        String expectedDashboardText = prop.getProperty("expectedDashboardText");
        String userRole = prop.getProperty("userRole");
        String status = prop.getProperty("status");


        String newUsername = "user" + System.currentTimeMillis();

        // Start Edge browser
        System.setProperty("webdriver.edge.driver", "E:\\drivers\\msedgedriver.exe");
        WebDriver driver = new EdgeDriver();
        driver.manage().window().maximize();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        try {

            driver.get(baseUrl);

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username")))
                    .sendKeys(loginUsername);

            driver.findElement(By.name("password")).sendKeys(loginPassword);

            driver.findElement(By.tagName("button")).click();

            String actualDashboardText = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.tagName("h6"))
            ).getText();

            if (!actualDashboardText.equals(expectedDashboardText)) {
                System.out.println("Login failed");
                driver.quit();
                return;
            }

            System.out.println("Login successful");

            wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//span[normalize-space()='Admin']"))).click();

            int before = getRecordsCount(wait);
            System.out.println("Records before: " + before);

            wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[normalize-space()='Add']"))).click();

            selectDropdownByLabel(wait, "User Role", userRole);

            selectDropdownByLabel(wait, "Status", status);

            WebElement emp = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//label[normalize-space()='Employee Name']/ancestor::div[contains(@class,'oxd-input-group')]//input")
            ));


            emp.sendKeys("a");


            WebElement firstOption = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[@role='listbox']//span")
            ));

            firstOption.click();


            WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//label[normalize-space()='Username']/ancestor::div[contains(@class,'oxd-input-group')]//input")
            ));
            usernameField.sendKeys(newUsername);

            WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//label[normalize-space()='Password']/ancestor::div[contains(@class,'oxd-input-group')]//input")
            ));
            passwordField.sendKeys(newPassword);

            WebElement confirmPasswordField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//label[normalize-space()='Confirm Password']/ancestor::div[contains(@class,'oxd-input-group')]//input")
            ));
            confirmPasswordField.sendKeys(newPassword);

            wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[normalize-space()='Save']"))).click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//h5[normalize-space()='System Users']")));

            int afterAdd = getRecordsCount(wait);
            System.out.println("Records after add: " + afterAdd);

            if (afterAdd == before + 1) {
                System.out.println("PASS: Record count increased by 1");
            } else {
                System.out.println("FAIL: Record count did not increase correctly");
            }

            WebElement searchUsername = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("(//label[normalize-space()='Username']/ancestor::div[contains(@class,'oxd-input-group')]//input)[1]")
            ));
            searchUsername.sendKeys(newUsername);

            driver.findElement(By.xpath("//button[normalize-space()='Search']")).click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[@class='oxd-table-body']")));

            wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//i[contains(@class,'bi-trash')]"))).click();

            wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[normalize-space()='Yes, Delete']"))).click();

            resetAndSearch(wait, newUsername);

            int rows = getTableRows(driver);

            if (rows == 0) {
                System.out.println("PASS: User deleted successfully");
            } else {
                System.out.println("FAIL: User still exists");
            }

            System.out.println("Test completed");

        } finally {
            Thread.sleep(3000);
            driver.quit();
        }
    }

    public static int getRecordsCount(WebDriverWait wait) {
        WebElement recordsLabel = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//span[contains(.,'Records Found')]")
        ));

        String text = recordsLabel.getText().trim();
        System.out.println("Records label text: " + text);

        return Integer.parseInt(text.replaceAll("[^0-9]", ""));
    }

    public static void selectDropdownByLabel(WebDriverWait wait, String label, String value) {
        WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//label[normalize-space()='" + label + "']/ancestor::div[contains(@class,'oxd-input-group')]//div[contains(@class,'oxd-select-text')]")
        ));
        dropdown.click();

        WebElement option = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[@role='listbox']//span[normalize-space()='" + value + "']")
        ));
        option.click();
    }

    public static void resetAndSearch(WebDriverWait wait, String username) {
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[normalize-space()='Reset']"))).click();

        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("(//label[normalize-space()='Username']/ancestor::div[contains(@class,'oxd-input-group')]//input)[1]")
        ));
        input.sendKeys(username);

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[normalize-space()='Search']"))).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@class='oxd-table-body']")));
    }

    public static int getTableRows(WebDriver driver) {
        return driver.findElements(By.xpath("//div[@class='oxd-table-body']/div")).size();
    }
}