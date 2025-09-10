package com.bookstore.util;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ExtentReporter {
    /**
     * Initializes and configures the ExtentReports instance.
     *
     * @return Configured ExtentReports instance.
     */
    public static ExtentReports initReport() {
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter("test-output/testReport.html");
        ExtentReports extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
        return extent;
    }
}
