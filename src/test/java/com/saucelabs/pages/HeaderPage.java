package com.saucelabs.pages;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class HeaderPage extends BasePage {

    private final By shoppingCartLocator = By.className("shopping_cart_link");

    public HeaderPage(WebDriver driver) {
        super(driver);
    }

    public String getCartAmount(){
        return getText(shoppingCartLocator);
    }

}
