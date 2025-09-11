package com.bookstore.tests;

import io.restassured.response.Response;
import static io.restassured.RestAssured.given;

import com.bookstore.base.BaseSetup;
import com.bookstore.constants.APIRoutes;
import com.bookstore.util.ResponseLogger;

import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

public class HealthCheckTest extends BaseSetup {

    @Test(priority = 1, groups = "healthCheck", description = "Verify if the server health check endpoint is returning status UP")
    public void validateHealthCheckEndpoint() {
        Reporter.log("Executing Health Check API: " + APIRoutes.HEALTH_CHECK, true);

        Response response = given()
                .log().all() // Log request
                .when()
                .get(APIRoutes.HEALTH_CHECK)
                .then()
                .log().all() // Log response
                .statusCode(200)
                .extract()
                .response();

        // Attach response to report
        ResponseLogger.attach("Heathcheck",response);

        String status = response.jsonPath().getString("status");
        Assert.assertEquals(status, "up", "Expected API health status to be 'up'");
    }
}
