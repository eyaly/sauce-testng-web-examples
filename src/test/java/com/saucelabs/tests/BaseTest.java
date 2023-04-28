package com.saucelabs.tests;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;

import io.github.bonigarcia.wdm.WebDriverManager;

import static com.saucelabs.tests.Config.env;
import static com.saucelabs.tests.Config.region;

public class BaseTest {
    protected static final ThreadLocal<WebDriver> driver = new ThreadLocal<WebDriver>();
    private String SAUCE_EU_URL = "https://ondemand.eu-central-1.saucelabs.com/wd/hub";
    private String SAUCE_US_URL = "https://ondemand.us-west-1.saucelabs.com/wd/hub";
    private String SAUCE_VISUAL_URL = "https://hub.screener.io:443/wd/hub";
    private String SAUCE_CAP = "sauce_";
    private String VISUAL_CAP = "visual_";
    private String DOCKER_SELENIUM_URL = "http://localhost:4444/wd/hub";  // https://github.com/SeleniumHQ/docker-selenium

    @BeforeMethod
    public void setup(Method method) throws MalformedURLException {

        System.out.println("Sauce - BeforeMethod hook");

        String methodName = method.getName();
        URL url;


        if (env.equals("saucelabs")) {

            switch (region) {
                case "us":
                    url = new URL(SAUCE_US_URL);
                    break;
                case "visual-us":
                    url = new URL(SAUCE_VISUAL_URL);
                    break;
                case "eu":
                default:
                    url = new URL(SAUCE_EU_URL);
                    break;
            }

            boolean isBuildCap = false;
            MutableCapabilities caps = new MutableCapabilities();
            Map<String, Object> sauceOptions = new HashMap<>();
            Map<String, Object> sauceVisual = new HashMap<>();

            for (Map.Entry<String, String> entry : Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest().getAllParameters().entrySet()) {
                String k = entry.getKey();
                String v = entry.getValue();

                if (k.startsWith(SAUCE_CAP)) {
                     // Sauce capability
                     String sauceCap = k.replaceFirst(SAUCE_CAP, "");
                     if (sauceCap.equals("build")) {
                         isBuildCap = true;
                     }

                     if (v.contains(" ")) {
                         // handle a list such as in tags cap
                         List<String> capList = Arrays.asList(v.split(" "));
                         sauceOptions.put(sauceCap, capList);
                     } else {
                         sauceOptions.put(sauceCap, v);
                     }
                } else if (k.startsWith(VISUAL_CAP)) {
                    // visual capability
                    String visualCap = k.replaceFirst(VISUAL_CAP, "");
                    sauceVisual.put(visualCap, v);
                } else {
                    caps.setCapability(k, v);
                }
            }

            sauceOptions.put("username", System.getenv("SAUCE_USERNAME"));
            sauceOptions.put("accessKey", System.getenv("SAUCE_ACCESS_KEY"));
            sauceOptions.put("name", methodName);
            if (!isBuildCap){ //handle build cap
                String buildVal = System.getenv("BUILD_NAME");
                sauceOptions.put("build", buildVal == null ? String.valueOf(new Random(System.currentTimeMillis()).nextInt()).replace("-", "") : buildVal);
            }

            caps.setCapability("sauce:options", sauceOptions);

            if (region.equalsIgnoreCase("visual-us")) {
//                sauceVisual.put("apiKey", System.getenv("SCREENER_API_KEY"));
                sauceVisual.put("apiKey", "4f07f4cb-ab09-4b66-aa14-4947f5a7aaaf");

//                String projectName = methodName + "-" + caps.getPlatformName() + "-" + caps.getBrowserName() + "-" + caps.getBrowserVersion();
//                sauceVisual.put("projectName", projectName);
                caps.setCapability("sauce:visual", sauceVisual);
            }

            try {
//                new SeleniumLogger().setLevel(Level.FINE)
                driver.set(new RemoteWebDriver(url, caps));
            } catch (Exception e) {
                System.out.println("*** Problem to create the remote web driver " + e.getMessage());
                throw new RuntimeException(e);
            }
        } else if (env.equals("local")) {
            // run on local machine
            try {
                System.out.println("Run on local chrome");
                // Let WebDriverManager handle drivers
                WebDriverManager.chromedriver().setup();
                driver.set(new ChromeDriver());

            } catch (Exception e) {
                System.out.println("*** Problem to create the local driver " + e.getMessage());
                throw new RuntimeException(e);
            }
        } else if (env.equals("docker")) {
            // run on docker image
            try {
                System.out.println("Run on docker chrome");

                MutableCapabilities caps = new MutableCapabilities();
                caps.setCapability("browserName", "chrome");
                driver.set(new RemoteWebDriver(new URL(DOCKER_SELENIUM_URL), caps));

            } catch (Exception e) {
                System.out.println("*** Problem to create the docker driver " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    @AfterMethod
    public void teardown(ITestResult result) {
        System.out.println("Sauce - AfterMethod hook");
        try {
            boolean bSuccess = result.isSuccess();
            if (env.equals("saucelabs"))
            {
                if (bSuccess) {
                    ((JavascriptExecutor) getDriver()).executeScript("sauce:job-result=passed");
                } else {
                    ((JavascriptExecutor) getDriver()).executeScript("sauce:job-result=failed");
                    ((JavascriptExecutor) getDriver()).executeScript("sauce:context=" + result.getThrowable().getMessage());

                }
            }
        } finally {
            System.out.println("Sauce - release VM");
            getDriver().quit();
        }
    }

    public WebDriver getDriver() {
        return driver.get();
    }
}
