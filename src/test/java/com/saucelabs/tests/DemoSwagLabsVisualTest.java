package com.saucelabs.tests;

import com.saucelabs.pages.HeaderPage;
import com.saucelabs.pages.LoginPage;
import com.saucelabs.pages.SwagOverviewPage;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class DemoSwagLabsVisualTest extends BaseTest {


    public void loadingLoginPage() {
        System.out.println("Sauce - Start loadingLoginPage test");
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.navigate("https://www.saucedemo.com");
        Assert.assertEquals(loginPage.getCurrentTitle(), "Swag Labs");
    }


    public void loginSuccess() {
        System.out.println("Sauce - Start loginSuccess test");
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.navigate("https://www.saucedemo.com");
        SwagOverviewPage swagOverviewPage = loginPage.login("standard_user", "secret_sauce");
        Assert.assertEquals(swagOverviewPage.getCurrentURL(), "https://www.saucedemo.com/inventory.html");

    }

    @Test
    public void addProductToCart(){
        System.out.println("Sauce - Start addProductToCart test");
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        js.executeScript("/*@visual.init*/", "Sauce Demo");

        LoginPage loginPage = new LoginPage(getDriver());
        HeaderPage HeaderPage = new HeaderPage(getDriver());
        loginPage.navigate("https://www.saucedemo.com");
        js.executeScript("/*@visual.snapshot*/", "Login Page");

        SwagOverviewPage swagOverviewPage = loginPage.login("standard_user", "secret_sauce");
        js.executeScript("/*@visual.snapshot*/", "Swag Overview Page");

        swagOverviewPage.addSwagToCart("Sauce Labs Backpack");
        // After
        js.executeScript("/*@visual.snapshot*/", "Verify shopping cart after adding a Swag");

        Map response = (Map)js.executeScript("/*@visual.end*/");
        Assert.assertTrue((Boolean)response.get("passed"), (String)response.get("message"));
    }

}
