import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;

public class studying {

    public static void main(String[] args) throws Exception {

        // Load properties file
        Properties prop = new Properties();
        InputStream fis = studying.class.getClassLoader().getResourceAsStream("config.properties");

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

        // Generate dynamic username
        String newUsername = "user" + System.currentTimeMillis();

        // Start Edge browser
        System.setProperty("webdriver.edge.driver", "E:\\drivers\\msedgedriver.exe");
        WebDriver driver = new EdgeDriver();
        driver.manage().window().maximize();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        try {
            // 1) Navigate
            driver.get(baseUrl);

            // 2) Enter username
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username")))
                    .sendKeys(loginUsername);

            // 3) Enter password
            driver.findElement(By.name("password")).sendKeys(loginPassword);

            // 4) Click login
            driver.findElement(By.tagName("button")).click();

            // Verify dashboard
            String actualDashboardText = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.tagName("h6"))
            ).getText();

            if (!actualDashboardText.equals(expectedDashboardText)) {
                System.out.println("Login failed");
                driver.quit();
                return;
            }

            System.out.println("Login successful");

            // 5) Click Admin tab
            wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//span[normalize-space()='Admin']"))).click();

            // 6) Get number of records found
            int before = getRecordsCount(wait);
            System.out.println("Records before: " + before);

            // 7) Click Add button
            wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[normalize-space()='Add']"))).click();

            // 8) Fill the required data

            // User Role
            selectDropdownByLabel(wait, "User Role", userRole);

            // Status
            selectDropdownByLabel(wait, "Status", status);

            // Employee Name (type letter + select from dropdown)
            WebElement emp = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//label[normalize-space()='Employee Name']/ancestor::div[contains(@class,'oxd-input-group')]//input")
            ));

// Type only one letter (like real user)
            emp.sendKeys("a");

// Wait for suggestions list to appear
            WebElement firstOption = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[@role='listbox']//span")
            ));

// Click the FIRST suggestion
            firstOption.click();

            // Username
            WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//label[normalize-space()='Username']/ancestor::div[contains(@class,'oxd-input-group')]//input")
            ));
            usernameField.sendKeys(newUsername);

            // Password
            WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//label[normalize-space()='Password']/ancestor::div[contains(@class,'oxd-input-group')]//input")
            ));
            passwordField.sendKeys(newPassword);

            // Confirm Password
            WebElement confirmPasswordField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//label[normalize-space()='Confirm Password']/ancestor::div[contains(@class,'oxd-input-group')]//input")
            ));
            confirmPasswordField.sendKeys(newPassword);

            // 9) Click Save button
            wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[normalize-space()='Save']"))).click();

            // Wait until back to System Users page
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//h5[normalize-space()='System Users']")));

            // 10) Verify that the number of records increased by 1
            int afterAdd = getRecordsCount(wait);
            System.out.println("Records after add: " + afterAdd);

            if (afterAdd == before + 1) {
                System.out.println("PASS: Record count increased by 1");
            } else {
                System.out.println("FAIL: Record count did not increase correctly");
            }

            // 11) Search with the username for the new user
            WebElement searchUsername = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("(//label[normalize-space()='Username']/ancestor::div[contains(@class,'oxd-input-group')]//input)[1]")
            ));
            searchUsername.sendKeys(newUsername);

            driver.findElement(By.xpath("//button[normalize-space()='Search']")).click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[@class='oxd-table-body']")));

            // 12) Delete the new user
            wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//i[contains(@class,'bi-trash')]"))).click();

            wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[normalize-space()='Yes, Delete']"))).click();

            // 13) Verify that the number of records decreased by 1
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

    // Get records count from "(x) Records Found"
    public static int getRecordsCount(WebDriverWait wait) {
        WebElement recordsLabel = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//span[contains(.,'Records Found')]")
        ));

        String text = recordsLabel.getText().trim();
        System.out.println("Records label text: " + text);

        return Integer.parseInt(text.replaceAll("[^0-9]", ""));
    }

    // Select dropdown value by label
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

    // Reset and search by username after delete
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

    // Count table rows
    public static int getTableRows(WebDriver driver) {
        return driver.findElements(By.xpath("//div[@class='oxd-table-body']/div")).size();
    }
}