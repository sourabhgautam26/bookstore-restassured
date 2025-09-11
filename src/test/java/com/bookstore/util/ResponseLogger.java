package com.bookstore.util;

import io.restassured.response.Response;
import org.testng.Reporter;

public class ResponseLogger {

    public static void attach(String requestDetails, Response response) {
        Reporter.getCurrentTestResult().setAttribute("request", requestDetails);
        Reporter.getCurrentTestResult().setAttribute("response", response);
    }
}
