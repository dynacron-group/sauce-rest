package com.dynacrongroup.webtest.sauce;

import org.json.simple.JSONObject;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.BeanToJsonConverter;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.hasKey;
import static org.junit.Assert.assertNotNull;

public class JobStopTest {

    private static final Logger log = LoggerFactory
            .getLogger(JobStopTest.class);

    private static String USER = System.getenv("SAUCELABS_USER");
    private static String KEY = System.getenv("SAUCELABS_KEY");

    private static WebDriver driver = null;
    SauceREST sauceREST = new SauceREST(USER, KEY);


    @Before
    public void createConnection() throws Exception {

        if (driver == null) {

            DesiredCapabilities capabillities = new DesiredCapabilities("iexplore",
                    "7", Platform.WINDOWS);
            capabillities.setCapability("name", "InSauceRestTest");
            capabillities.setCapability("tags", "test-tag");
            capabillities.setCapability("build", "12345");

            log.info("capabilities: "
                    + new BeanToJsonConverter().convert(capabillities));
            driver = new RemoteWebDriver(getConnectionString(), capabillities);
        }
    }

    @Test
    public void stopJob() {
        sauceREST.stopJob(getJobID());
        JSONObject jobStatus = sauceREST.getJobStatus(getJobID());
        assertNotNull(jobStatus);

        assertThat((Map<String, Object>) jobStatus, hasKey("status"));
        String status = (String) jobStatus.get("status");
        assertThat(status, equalToIgnoringCase("complete"));
    }


    @AfterClass
    public static void shutDown() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    private String getJobID() {
        return ((RemoteWebDriver) driver).getSessionId().toString();
    }

    private URL getConnectionString() throws MalformedURLException {
        return new URL("http://" + USER + ":" + KEY + "@"
                + "ondemand.saucelabs.com/wd/hub");
    }
}
