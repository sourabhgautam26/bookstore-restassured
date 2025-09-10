package com.bookstore.tests;

import com.bookstore.constants.ApiEndPoints;
import com.bookstore.util.TestUtil;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import com.bookstore.base.BaseSetup;

import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AccountsTests extends BaseSetup {
    static String token;
    String userPayload;
    Response response;

    @Test(priority = 1, dependsOnGroups = "healthCheck", description = "Sign up a new user")
    public void signUpUser() {
        userPayload = TestUtil.getSignUpPayload(null, null);
        Response response = given()
                .body(userPayload)
                .log().all() // Log the request details
                .when()
                .post(ApiEndPoints.SIGN_UP)
                .then()
                .log().all() // Log the response details
                .statusCode(200)
                .extract()
                .response();

        String message = response.jsonPath().getString("message");
        Assert.assertEquals(message, "User created successfully", "Sign-up message mismatch");
    }

    @Test(priority = 2, dependsOnMethods = "signUpUser", description = "Verify user sign up and generate token")
    public void verifySignUpUserAndGenerateToken() {
        Response response = given()
                .body(userPayload)
                .log().all() // Log the request details
                .when()
                .post(ApiEndPoints.LOGIN)
                .then()
                .log().all() // Log the response details
                .statusCode(200)
                .extract()
                .response();
        token = response.jsonPath().getString("access_token");
        Assert.assertNotNull(token, "Token should not be null");
    }

    @Test(priority = 3, dependsOnMethods = "signUpUser", description = "Sign up with existing email")
    public void signUpwithExistingEmail() {
        Response response = given()
                .body(userPayload)
                .log().all() // Log the request details
                .when()
                .post(ApiEndPoints.SIGN_UP)
                .then()
                .log().all() // Log the response details
                .statusCode(400)
                .extract()
                .response();

        String message = response.jsonPath().getString("detail");
        Assert.assertEquals(message, "Email already registered", "Sign-up message mismatch for existing email");
    }

    @Test(priority = 4, dependsOnMethods = "signUpUser", description = "Login with incorrect password")
    public void loginWithIncorrectPassword() {
        JSONObject jsonObject = new JSONObject(userPayload);
        String email = jsonObject.getString("email");

        String incorrectPasswordPayload = TestUtil.getSignUpPayload(email, "wrongPassword");
        Response response = given()
                .body(incorrectPasswordPayload)
                .log().all() // Log the request details
                .when()
                .post(ApiEndPoints.LOGIN)
                .then()
                .log().all() // Log the response details
                .statusCode(400)
                .extract().response();
        String message = response.jsonPath().getString("detail");
        Assert.assertEquals(message, "Incorrect email or password", "Login message mismatch for incorrect password");

    }

    @Test(priority = 5, dependsOnMethods = "signUpUser", description = "Login with incorrect email")
    public void loginWithIncorrectEmail() {
        JSONObject jsonObject = new JSONObject(userPayload);
        String password = jsonObject.getString("email");

        String incorrectEmailPayload = TestUtil.getSignUpPayload("email", password);
        Response response = given()
                .body(incorrectEmailPayload)
                .log().all() // Log the request details
                .when()
                .post(ApiEndPoints.LOGIN)
                .then()
                .log().all() // Log the response details
                .statusCode(400)
                .extract().response();
        String message = response.jsonPath().getString("detail");
        Assert.assertEquals(message, "Incorrect email or password", "Login message mismatch for incorrect password");

    }

}