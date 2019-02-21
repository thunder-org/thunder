package org.conqueror.drone.selenium;

import org.conqueror.drone.selenium.webdriver.WebBrowser;
import org.openqa.selenium.chrome.ChromeOptions;


public class SeleniumClient {

    public static void main(String[] args) throws InterruptedException {
        System.setProperty("webdriver.chrome.driver", args[0]);

        ChromeOptions options = new ChromeOptions();
        options.addArguments("disable-infobars"); // disabling infobars
        options.addArguments("--dns-prefetch-disable"); // disabling infobars
        options.addArguments("--always-authorize-plugins"); // disabling infobars
//        options.addArguments("--headless"; // disabling infobars
        options.addArguments("--disable-extensions"); // disabling extensions
        options.addArguments("--disable-dev-shm-usage"); // overcome limited resource problems
        options.addArguments("--no-sandbox"); // Bypass OS security model

        try (WebBrowser browser = WebBrowser.createWebBrowser(WebBrowser.WebDriverName.CHROME, options)) {
            browser.visit("https://www.naver.com");
            browser.waitForPageLoad();
        }
    }

}
