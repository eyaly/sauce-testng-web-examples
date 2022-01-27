package com.saucelabs.tests;

public class Config {
    public static final String env = System.getProperty("env", "saucelabs");
    public static final String region = System.getProperty("region", "eu");

}
