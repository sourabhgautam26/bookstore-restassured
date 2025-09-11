package com.bookstore.tests;

import com.bookstore.constants.APIRoutes;
import com.bookstore.util.TestUtil;
import com.bookstore.util.ResponseLogger;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import com.bookstore.base.BaseSetup;

import org.json.JSONObject;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

public class AccountsTests extends BaseSetup {
    static String token;
    String userPayload;
    Response response;

    @Test(priority = 1, dependsOnGroups = "healthCheck", description = "Create a new user account via sign-up")
    public void signUpUser() {
        userPayload = TestUtil.getSignUpPayload(null, null);

        Reporter.log("Request Payload (SignUp): " + userPayload, true);

        Response response = given()
                .body(userPayload)
                .log().all()
                .when()
                .post(APIRoutes.SIGN_UP)
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .response();

        ResponseLogger.attach(userPayload,response);

        String message = response.jsonPath().getString("message");
        Assert.assertEquals(message, "User created successfully", "Sign-up message mismatch");
    }

    @Test(priority = 2, dependsOnMethods = "signUpUser", description = "Login after sign-up and generate token")
    public void verifySignUpUserAndGenerateToken() {
        Reporter.log("Login Payload: " + userPayload, true);

        Response response = given()
                .body(userPayload)
                .log().all()
                .when()
                .post(APIRoutes.LOGIN)
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .response();

        ResponseLogger.attach(userPayload,response);

        token = response.jsonPath().getString("access_token");
        Assert.assertNotNull(token, "Token should not be null after login");
    }

    @Test(priority = 3, dependsOnMethods = "signUpUser", description = "Try signing up with an already registered email")
    public void signUpWithExistingEmail() {
        Reporter.log("Request Payload (Existing Email): " + userPayload, true);

        Response response = given()
                .body(userPayload)
                .log().all()
                .when()
                .post(APIRoutes.SIGN_UP)
                .then()
                .log().all()
                .statusCode(400)
                .extract()
                .response();

        ResponseLogger.attach(userPayload,response);

        String message = response.jsonPath().getString("detail");
        Assert.assertEquals(message, "Email already registered", "Expected duplicate email error message");
    }

    @Test(priority = 4, dependsOnMethods = "signUpUser", description = "Login with incorrect password attempt")
    public void loginWithIncorrectPassword() {
        JSONObject jsonObject = new JSONObject(userPayload);
        String email = jsonObject.getString("email");

        String incorrectPasswordPayload = TestUtil.getSignUpPayload(email, "wrongPassword");

        Reporter.log("Request Payload (Incorrect Password): " + incorrectPasswordPayload, true);

        Response response = given()
                .body(incorrectPasswordPayload)
                .log().all()
                .when()
                .post(APIRoutes.LOGIN)
                .then()
                .log().all()
                .statusCode(400)
                .extract()
                .response();

        ResponseLogger.attach(incorrectPasswordPayload,response);

        String message = response.jsonPath().getString("detail");
        Assert.assertEquals(message, "Incorrect email or password", "Expected error for wrong password");
    }

    @Test(priority = 5, dependsOnMethods = "signUpUser", description = "Login with incorrect email attempt")
    public void loginWithIncorrectEmail() {
        JSONObject jsonObject = new JSONObject(userPayload);
        String password = jsonObject.getString("password"); // fixed, should fetch password

        String incorrectEmailPayload = TestUtil.getSignUpPayload("invalid@email.com", password);

        Reporter.log("Request Payload (Incorrect Email): " + incorrectEmailPayload, true);

        Response response = given()
                .body(incorrectEmailPayload)
                .log().all()
                .when()
                .post(APIRoutes.LOGIN)
                .then()
                .log().all()
                .statusCode(400)
                .extract()
                .response();

        ResponseLogger.attach(incorrectEmailPayload,response);

        String message = response.jsonPath().getString("detail");
        Assert.assertEquals(message, "Incorrect email or password", "Expected error for wrong email");
    }
}
