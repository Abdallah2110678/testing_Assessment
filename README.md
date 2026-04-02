# Testing Assessment

Java Selenium automation project for the OrangeHRM demo application.

## Overview

This project automates the following flow:

1. Open the OrangeHRM demo site.
2. Log in with admin credentials.
3. Navigate to the `Admin` module.
4. Capture the current number of system user records.
5. Add a new user with a generated username.
6. Verify the record count increases.
7. Search for the created user.
8. Delete the user.
9. Verify the user no longer exists.

The main automation logic is implemented in [studying.java](/d:/projects/testing%20assessmnet/testing_Assessment/src/test/java/studying.java), and the test data is stored in [config.properties](/d:/projects/testing%20assessmnet/testing_Assessment/src/main/resources/config.properties).

## Tech Stack

- Java 11
- Maven
- Selenium Java `4.27.0`
- TestNG `7.10.2`
- Microsoft Edge WebDriver

## Project Structure

```text
testing_Assessment/
|-- pom.xml
|-- README.md
|-- src/
|   |-- main/
|   |   |-- java/org/example/Main.java
|   |   `-- resources/config.properties
|   `-- test/
|       `-- java/studying.java
```

## Configuration

The test uses values from [config.properties](/d:/projects/testing%20assessmnet/testing_Assessment/src/main/resources/config.properties):

```properties
baseUrl=https://opensource-demo.orangehrmlive.com/web/index.php/auth/login
loginUsername=Admin
loginPassword=admin123
employeeName=Naruto Uzumaki
newUserPassword=Test@12345
expectedDashboardText=Dashboard
userRole=ESS
status=Enabled
```

## Important Note

The EdgeDriver path is currently hardcoded in [studying.java](/d:/projects/testing%20assessmnet/testing_Assessment/src/test/java/studying.java):

```java
System.setProperty("webdriver.edge.driver", "E:\\drivers\\msedgedriver.exe");
```

Update this path before running the test so it matches your local machine.

## How To Run

1. Install Java 11 or later.
2. Install Maven.
3. Download a version of `msedgedriver` that matches your Edge browser version.
4. Update the driver path in `studying.java`.
5. From the project root, run:

```bash
mvn test
```

If your IDE is configured for Maven, you can also run `studying.java` directly.

## Notes

- The username for the created user is generated dynamically using the current timestamp.
- The script uses explicit waits to handle page loading and element interaction.
- The project currently contains the automation in a `main` method rather than a formal TestNG test class.

## Possible Improvements

- Move the WebDriver path into `config.properties` or an environment variable.
- Convert the script into proper TestNG test methods with assertions.
- Add setup and teardown methods.
- Introduce Page Object Model structure for maintainability.
