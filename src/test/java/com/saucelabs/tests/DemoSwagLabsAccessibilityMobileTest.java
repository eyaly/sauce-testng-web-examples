package com.saucelabs.tests;

import com.deque.html.axecore.results.Results;
import com.deque.html.axecore.results.Rule;
import com.deque.html.axecore.selenium.AxeBuilder;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;


public class DemoSwagLabsAccessibilityMobileTest {

    private static ThreadLocal<RemoteWebDriver> driver = new ThreadLocal<RemoteWebDriver>();
    private String SAUCE_EU_URL = "https://ondemand.eu-central-1.saucelabs.com/wd/hub";
    private String SAUCE_US_URL = "https://ondemand.us-west-1.saucelabs.com/wd/hub";

    @BeforeMethod
    public void setup(Method method) throws MalformedURLException {
        System.out.println("Sauce - BeforeMethod hook");
        String methodName = method.getName();

        URL url;
        switch (Config.region) {
            case "us":
                url = new URL(SAUCE_US_URL);
                break;
            case "eu":
            default:
                url = new URL(SAUCE_EU_URL);
                break;
        }

        MutableCapabilities caps = new MutableCapabilities();
        MutableCapabilities sauceOptions = new MutableCapabilities();

        caps.setCapability("browserName", "chrome");
        caps.setCapability("platformName", "android");
        caps.setCapability("automationName", "UiAutomator2");
        caps.setCapability("appium:deviceName", "samsung.*");
        caps.setCapability("appium:platformVersion", "12");

        sauceOptions.setCapability("name", methodName);
        sauceOptions.setCapability("build", "myBuild_Accessibility_2");
        List<String> tags = Arrays.asList("sauceDemo", "accessibility", "W3C");
        sauceOptions.setCapability("tags", tags);
        sauceOptions.setCapability("username", System.getenv("SAUCE_USERNAME"));
        sauceOptions.setCapability("accessKey", System.getenv("SAUCE_ACCESS_KEY"));

        caps.setCapability("sauce:options", sauceOptions);

        driver.set(new RemoteWebDriver(url, caps));

    }

    @AfterMethod
    public void teardown(ITestResult result) {
        System.out.println("Sauce - AfterMethod hook");
        try {
            boolean bSuccess = result.isSuccess();
            if (bSuccess) {
                ((JavascriptExecutor) getDriver()).executeScript("sauce:job-result=passed");
            }
            else {
                ((JavascriptExecutor) getDriver()).executeScript("sauce:job-result=failed");
                ((JavascriptExecutor) getDriver()).executeScript("sauce:context=" +result.getThrowable().getMessage());

            }
        } finally {
            System.out.println("Sauce - release driver");
            getDriver().quit();
        }
    }

    public RemoteWebDriver getDriver() {
        return driver.get();
    }

    @Test
    public void swagLabsLoginAccessibilityTest() {
        System.out.println("Sauce - Start swagLabsLoginAccessibility test");
        RemoteWebDriver driver = getDriver();
        // login
        driver.navigate().to("https://www.saucedemo.com");

        AxeBuilder axeBuilder = new AxeBuilder();
        Results accessibilityResults = axeBuilder.analyze(driver);
        Assert.assertEquals(3, accessibilityResults.getViolations().size());
        ((JavascriptExecutor) getDriver()).executeScript("sauce:context=" +" -----------");
        for (Rule violation : accessibilityResults.getViolations()) {
            System.out.println(violation);
            ((JavascriptExecutor) getDriver()).executeScript("sauce:context=" +"Impact: " + violation.getImpact());
            ((JavascriptExecutor) getDriver()).executeScript("sauce:context=" +"Description: " + violation.getHelp());
            ((JavascriptExecutor) getDriver()).executeScript("sauce:context=" +"Help: " + violation.getHelpUrl());
            ((JavascriptExecutor) getDriver()).executeScript("sauce:context=" +" -----------");
        }


//        driver.findElement(By.id("user-name")).sendKeys("standard_user");
//        driver.findElement(By.id("password")).sendKeys("secret_sauce");
//        driver.findElement(By.id("login-button")).click();
//
//        AxeBuilder axeBuilder2 = new AxeBuilder();
//        Results accessibilityResults2 = axeBuilder2.analyze(driver);
//
//        ((JavascriptExecutor) getDriver()).executeScript("sauce:context=" +" -----------");
//        for (Rule violation : accessibilityResults2.getViolations()) {
//            System.out.println(violation);
//            ((JavascriptExecutor) getDriver()).executeScript("sauce:context=" +"Impact: " + violation.getImpact());
//            ((JavascriptExecutor) getDriver()).executeScript("sauce:context=" +"Description: " + violation.getHelp());
//            ((JavascriptExecutor) getDriver()).executeScript("sauce:context=" +"Help: " + violation.getHelpUrl());
//            ((JavascriptExecutor) getDriver()).executeScript("sauce:context=" +" -----------");
//        }

    }
}