package com.saucelabs.tests;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DemoSwagLabsPerformanceTest {

    private static ThreadLocal<RemoteWebDriver> driver = new ThreadLocal<RemoteWebDriver>();
    private String SAUCE_EU_URL = "https://ondemand.eu-central-1.saucelabs.com/wd/hub";
    private String SAUCE_US_URL = "https://ondemand.us-west-1.saucelabs.com/wd/hub";

    // The expected metrics
    private String[] my_metrics = {
            "load",
            "speedIndex",
            "firstInteractive",
            "firstVisualChange",
            "lastVisualChange",
            "firstMeaningfulPaint",
            "firstCPUIdle",
            "timeToFirstByte",
            "firstPaint",
            "estimatedInputLatency",
            "firstContentfulPaint",
            "totalBlockingTime",
            "score",
            "domContentLoaded",
            "cumulativeLayoutShift",
            "serverResponseTime",
            "largestContentfulPaint",
    };

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

        caps.setCapability("browserName", "Chrome");
        caps.setCapability("browserVersion", "latest");
        caps.setCapability("platformName", "windows 10");

        sauceOptions.setCapability("name", methodName);
        sauceOptions.setCapability("build", "myBuild_Performance_1");
        List<String> tags = Arrays.asList("sauceDemo", "Performance", "W3C");
        sauceOptions.setCapability("tags", tags);
        sauceOptions.setCapability("screenResolution", "1600x1200");
        sauceOptions.setCapability("username", System.getenv("SAUCE_USERNAME"));
        sauceOptions.setCapability("accessKey", System.getenv("SAUCE_ACCESS_KEY"));

        // Performance
        sauceOptions.setCapability("extendedDebugging", true);
        sauceOptions.setCapability("capturePerformance", true);

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
    public void swagLabsLoginPerformance_noAssertion() {
        System.out.println("Sauce - Start checkSwagLabsTitle test");
        RemoteWebDriver driver = getDriver();
        // login
        driver.navigate().to("https://www.saucedemo.com");
        driver.findElement(By.id("user-name")).sendKeys("standard_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        driver.findElement(By.id("login-button")).click();

    }

    @Test
    public void swagLabsLoginNetwork() {
        System.out.println("Sauce - Start swagLabsLoginPerformance_assertion test");
        WebDriver driver = getDriver();

        // login
        driver.navigate().to("https://www.saucedemo.com");

        driver.findElement(By.id("user-name")).sendKeys("standard_user");
        //    driver.findElement(By.id("user-name")).sendKeys("performance_glitch_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        driver.findElement(By.id("login-button")).click();
    }

    @Test
    public void swagLabsLoginPerformance_assertion() {
        System.out.println("Sauce - Start swagLabsLoginPerformance_assertion test");
        WebDriver driver = getDriver();
        // https://docs.saucelabs.com/performance/transitions/#target-specific-urls-in-a-script
        ((RemoteWebDriver) driver).executeScript("sauce:performanceDisable");
        // login
        driver.navigate().to("https://www.saucedemo.com");
        ((RemoteWebDriver) driver).executeScript("sauce:performanceEnable");
        driver.findElement(By.id("user-name")).sendKeys("standard_user");
    //    driver.findElement(By.id("user-name")).sendKeys("performance_glitch_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        driver.findElement(By.id("login-button")).click();

        // load a second URL and we run performance on this page: https://www.saucedemo.com/inventory.html
        HashMap<String, Object> metrics = new HashMap<>();
        metrics.put("type", "sauce:performance");

        // Get the performance logs
        Map<String, Object> perfMetrics = (Map<String, Object>) ((RemoteWebDriver) driver).executeScript("sauce:log", metrics);
        // Verify that all logs have been captured
        for (Map.Entry<String, Object> entry : perfMetrics.entrySet()) {
            String k = entry.getKey();
            Object v = entry.getValue();
            System.out.println("key: " + k.toString() + " .val: " + v.toString());
        }


        float scoreVal = Float.parseFloat(perfMetrics.get("score").toString());
        int speedIndexVal = Integer.parseInt(perfMetrics.get("speedIndex").toString());

        Assert.assertTrue(scoreVal > 0.90, "score: " + scoreVal + "  - Performance Score is less than 0.9");
        Assert.assertTrue(speedIndexVal < 1000,  "speedIndex: " + speedIndexVal + "  is equal or bigger than 1000");

    }

}