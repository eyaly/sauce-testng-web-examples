package com.saucelabs.tests;
import com.saucelabs.pages.HeaderPage;
import com.saucelabs.pages.SwagOverviewPage;
import com.saucelabs.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class DemoSwagLabsTest extends BaseTest {

    @Test
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


    public void addProductToCart(){
        System.out.println("Sauce - Start addProductToCart test");
        LoginPage loginPage = new LoginPage(getDriver());
        HeaderPage HeaderPage = new HeaderPage(getDriver());
        loginPage.navigate("https://www.saucedemo.com");
        SwagOverviewPage swagOverviewPage = loginPage.login("standard_user", "secret_sauce");

        // Before
        assertThat(HeaderPage.getCartAmount()).as("Verify shopping cart before adding a Swag").isEqualTo("");
        swagOverviewPage.addSwagToCart("Sauce Labs Backpack");
        // After
        assertThat(HeaderPage.getCartAmount()).as("Verify shopping cart after adding a Swag").isEqualTo("1");
    }

}
