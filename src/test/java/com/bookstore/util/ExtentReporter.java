package com.bookstore.util;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ExtentReporter {

    private static ExtentReports extent;

    public static ExtentReports initReport() {
        if (extent == null) {
            // Unique report name with timestamp
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String reportPath = System.getProperty("user.dir") + "/test-output/ExtentReport_" + timestamp + ".html";

            ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);
            spark.config().setTheme(Theme.DARK);
            spark.config().setReportName("Bookstore API Test Report");
            spark.config().setDocumentTitle("Bookstore API Automation Results");

            extent = new ExtentReports();
            extent.attachReporter(spark);

            // Add environment + metadata info
            extent.setSystemInfo("Project", "Bookstore API");
            extent.setSystemInfo("Author", "Sourabh Gautam");
            extent.setSystemInfo("Environment", System.getProperty("env", "QA"));
            extent.setSystemInfo("Java Version", System.getProperty("java.version"));
            extent.setSystemInfo("OS", System.getProperty("os.name"));
        }
        return extent;
    }
}
