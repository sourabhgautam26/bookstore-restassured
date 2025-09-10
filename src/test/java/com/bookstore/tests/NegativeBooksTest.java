package com.bookstore.tests;

import com.bookstore.base.BaseSetup;
import com.bookstore.constants.ApiEndPoints;
import com.bookstore.util.TestUtil;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import org.testng.Assert;
import org.testng.annotations.Test;

public class NegativeBooksTest extends BaseSetup {

    @Test(dependsOnMethods = { "com.bookstore.tests.AccountsTests.signUpUser",
            "com.bookstore.tests.AccountsTests.verifySignUpUserAndGenerateToken" }, description = "Add a book with invalid token")
    public void addBookWithInvalidTokenTest() {
        String jsonPayload = TestUtil.readJsonFromFile("createBook.json");
        Response response = given()
                .header("Authorization", "Bearer " + "invalid_token")
                .log().all() // Log the request details
                .body(jsonPayload)
                .when()
                .post(ApiEndPoints.ADD_NEW_BOOK)
                .then().log().all() // Log the response details
                .statusCode(403).extract().response();
        String message = response.jsonPath().getString("detail");
        Assert.assertEquals(message, "Invalid token or expired token", "Error message mismatch");
    }

    @Test(dependsOnMethods = { "com.bookstore.tests.AccountsTests.signUpUser",
            "com.bookstore.tests.AccountsTests.verifySignUpUserAndGenerateToken" }, description = "Update book with invalid token")
    public void updateBookWithInvalidTokenTest() {
        String updatePayload = TestUtil.readJsonFromFile("updateBook.json");
        Response response = given()
                .header("Authorization", "Bearer " + "invalid_token")
                .log().all() // Log the request details
                .body(updatePayload)
                .when()
                .put(String.format(ApiEndPoints.GET_BOOK_ID, BooksTest.bookId))
                .then().log().all() // Log the response details
                .statusCode(403).extract().response();
        String message = response.jsonPath().getString("detail");
        Assert.assertEquals(message, "Invalid token or expired token", "Error message mismatch");
    }

    @Test(dependsOnMethods = { "com.bookstore.tests.AccountsTests.signUpUser",
            "com.bookstore.tests.AccountsTests.verifySignUpUserAndGenerateToken" }, description = "Delete book with invalid token")
    public void deleteBookWithInvalidTokenTest() {
        Response response = given()
                .header("Authorization", "Bearer " + "invalid_token")
                .log().all() // Log the request details
                .when()
                .put(String.format(ApiEndPoints.GET_BOOK_ID, BooksTest.bookId))
                .then().log().all() // Log the response details
                .statusCode(403).extract().response();
        String message = response.jsonPath().getString("detail");
        Assert.assertEquals(message, "Invalid token or expired token", "Error message mismatch");
    }

    @Test(dependsOnMethods = "com.bookstore.tests.BooksTest.addBooksTest", description = "Get all books with invalid token")
    public void getAllBookWithInvalidTokenTest() {
        Response response = given()
                .header("Authorization", "Bearer " + "invalid_token")
                .log().all() // Log the request details
                .when()
                .get(ApiEndPoints.GET_ALL_BOOKS)
                .then()
                .log().all() // Log the response details
                .statusCode(403)
                .extract().response();

        String message = response.jsonPath().getString("detail");
        Assert.assertEquals(message, "Invalid token or expired token", "Error message mismatch");
    }

    @Test(dependsOnMethods = "com.bookstore.tests.BooksTest.addBooksTest", description = "Get book with non-existent ID")
    public void getBookWithNonExistantIdTest() {
        Response response = given()
                .header("Authorization", "Bearer " + AccountsTests.token)
                .log().all() // Log the request details
                .when()
                .get(String.format(ApiEndPoints.GET_BOOK_ID, "89999"))
                .then()
                .log().all() // Log the response details
                .statusCode(404)
                .extract().response();
        String message = response.jsonPath().getString("detail");
        Assert.assertEquals(message, "Book not found", "Error message mismatch");
    }

    @Test(priority = 2, dependsOnMethods = "com.bookstore.tests.BooksTest.addBooksTest", description = "Get book with invalid ID")
    public void getBookWithInvalidIdTest() {
        Response response = given()
                .header("Authorization", "Bearer " + AccountsTests.token)
                .log().all() // Log the request details
                .when()
                .get(String.format(ApiEndPoints.GET_BOOK_ID, "inalid_id"))
                .then()
                .log().all() // Log the response details
                .statusCode(422)
                .extract().response();
        String message = response.jsonPath().getString("detail[0].msg");
        Assert.assertEquals(message, "Input should be a valid integer, unable to parse string as an integer",
                "Error message mismatch");
    }

}
