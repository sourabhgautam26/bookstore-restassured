package com.bookstore.util;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import io.restassured.response.Response;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.Reporter;

public class ApiTestReportListener implements ITestListener {
    private static ExtentReports extent = ExtentReporter.initReport();
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    @Override
    public void onTestStart(ITestResult result) {
        String description = result.getMethod().getDescription();
        String methodName = result.getMethod().getMethodName();

        // Combine description + method name for better clarity
        String testTitle;
        if (description != null && !description.isEmpty()) {
            testTitle = description + " (" + methodName + ")";
        } else {
            testTitle = methodName;
        }

        ExtentTest extentTest = extent.createTest(testTitle)
                .assignCategory(result.getTestClass().getName());

        test.set(extentTest);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        test.get().log(Status.PASS, "Test Passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentTest extentTest = test.get();
        extentTest.log(Status.FAIL, "❌ Test Failed: " + result.getThrowable());

        // Request details
        Object requestObj = result.getAttribute("request");
        if (requestObj instanceof String) {
            extentTest.log(Status.INFO, "📤 Request:\n" + requestObj);
        }

        // Response details
        Object responseObj = result.getAttribute("response");
        if (responseObj instanceof Response) {
            Response response = (Response) responseObj;
            extentTest.log(Status.INFO, "📥 Response Body:\n" + response.asPrettyString());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        test.get().log(Status.SKIP, "Test Skipped: " + result.getThrowable());
    }

    @Override
    public void onFinish(ITestContext context) {
        extent.flush();
    }
}
