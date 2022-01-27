package com.saucelabs.pages;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class SwagOverviewPage extends BasePage {

    private final By swagItemsLocator  = By.className("inventory_item");
    private final String swagItemAdToCartBtn = ".btn_primary.btn_inventory";

    public SwagOverviewPage(WebDriver driver) {
        super(driver);
    }

    private WebElement getSwag(String needle){

        WebElement needleFirst = findElements(swagItemsLocator).stream().filter(elm -> elm.getText().contains(needle)).findFirst().orElse(null);;//.collect(Collectors.toList());
        return needleFirst;
    }

    public void addSwagToCart(String needle){
        getSwag(needle).findElement(By.cssSelector(swagItemAdToCartBtn)).click();
    }

}
