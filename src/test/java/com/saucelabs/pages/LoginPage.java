package com.saucelabs.pages;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {

    private final By usernameLocator = By.id("user-name");
    private final By passwordLocator = By.id("password");
    private final By submitButtonLocator     = By.id("login-button");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public SwagOverviewPage login(String username, String password) {
        type(username, usernameLocator);
        type(password, passwordLocator);
        click(submitButtonLocator);
        return new SwagOverviewPage(driver);
    }

}
