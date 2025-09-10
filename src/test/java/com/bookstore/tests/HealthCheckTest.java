package com.bookstore.tests;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;

import com.bookstore.base.BaseSetup;
import com.bookstore.constants.ApiEndPoints;
import org.testng.Assert;
import org.testng.annotations.Test;

public class HealthCheckTest extends BaseSetup {

    @Test(priority = 1, groups = "healthCheck", description = "Health check to check if server is up")
    public void healthCheck() {
        Response response = given()
                .log().all() // Log the request details
                .when()
                .get(ApiEndPoints.HEALTH_CHECK)
                .then()
                .log().all() // Log the response details
                .statusCode(200)
                .extract()
                .response();

        String status = response.jsonPath().getString("status");
        Assert.assertEquals(status, "up", "Health check status mismatch");
    }

}
