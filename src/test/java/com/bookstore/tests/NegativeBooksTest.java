package com.bookstore.tests;

import com.bookstore.base.BaseSetup;
import com.bookstore.constants.APIRoutes;
import com.bookstore.util.TestUtil;
import com.bookstore.util.ResponseLogger;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import org.testng.Assert;
import org.testng.annotations.Test;

public class NegativeBooksTest extends BaseSetup {

    @Test(dependsOnMethods = {
            "com.bookstore.tests.AccountsTests.signUpUser",
            "com.bookstore.tests.AccountsTests.verifySignUpUserAndGenerateToken" },
            description = "Attempt to add a book with an invalid token")
    public void addBookWithInvalidTokenTest() {
        String jsonPayload = TestUtil.readJsonFromFile("createBook.json");
        Response response = given()
                .header("Authorization", "Bearer " + "invalid_token")
                .log().all()
                .body(jsonPayload)
                .when()
                .post(APIRoutes.ADD_NEW_BOOK)
                .then().log().all()
                .statusCode(403).extract().response();

        ResponseLogger.attach(jsonPayload,response);

        String message = response.jsonPath().getString("detail");
        Assert.assertEquals(message, "Invalid token or expired token", "Error message mismatch");
    }

    @Test(dependsOnMethods = {
            "com.bookstore.tests.AccountsTests.signUpUser",
            "com.bookstore.tests.AccountsTests.verifySignUpUserAndGenerateToken" },
            description = "Attempt to update a book with an invalid token")
    public void updateBookWithInvalidTokenTest() {
        String updatePayload = TestUtil.readJsonFromFile("updateBook.json");
        Response response = given()
                .header("Authorization", "Bearer " + "invalid_token")
                .log().all()
                .body(updatePayload)
                .when()
                .put(String.format(APIRoutes.GET_BOOK_ID, BooksTest.bookId))
                .then().log().all()
                .statusCode(403).extract().response();

        ResponseLogger.attach(updatePayload,response);

        String message = response.jsonPath().getString("detail");
        Assert.assertEquals(message, "Invalid token or expired token", "Error message mismatch");
    }

    @Test(dependsOnMethods = {
            "com.bookstore.tests.AccountsTests.signUpUser",
            "com.bookstore.tests.AccountsTests.verifySignUpUserAndGenerateToken" },
            description = "Attempt to delete a book with an invalid token")
    public void deleteBookWithInvalidTokenTest() {
        Response response = given()
                .header("Authorization", "Bearer " + "invalid_token")
                .log().all()
                .when()
                .delete(String.format(APIRoutes.GET_BOOK_ID, BooksTest.bookId))
                .then().log().all()
                .statusCode(403).extract().response();

        ResponseLogger.attach("Delete Request",response);

        String message = response.jsonPath().getString("detail");
        Assert.assertEquals(message, "Invalid token or expired token", "Error message mismatch");
    }

    @Test(dependsOnMethods = "com.bookstore.tests.BooksTest.createBookRecord",
            description = "Fetch all books with an invalid token")
    public void getAllBookWithInvalidTokenTest() {
        Response response = given()
                .header("Authorization", "Bearer " + "invalid_token")
                .log().all()
                .when()
                .get(APIRoutes.GET_ALL_BOOKS)
                .then()
                .log().all()
                .statusCode(403)
                .extract().response();

        ResponseLogger.attach("Get Request",response);

        String message = response.jsonPath().getString("detail");
        Assert.assertEquals(message, "Invalid token or expired token", "Error message mismatch");
    }

    @Test(dependsOnMethods = "com.bookstore.tests.BooksTest.createBookRecord",
            description = "Try fetching a book with a non-existent ID")
    public void getBookWithNonExistantIdTest() {
        Response response = given()
                .header("Authorization", "Bearer " + AccountsTests.token)
                .log().all()
                .when()
                .get(String.format(APIRoutes.GET_BOOK_ID, "89999"))
                .then()
                .log().all()
                .statusCode(404)
                .extract().response();

        ResponseLogger.attach("Get Request",response);

        String message = response.jsonPath().getString("detail");
        Assert.assertEquals(message, "Book not found", "Error message mismatch");
    }

    @Test(priority = 2, dependsOnMethods = "com.bookstore.tests.BooksTest.createBookRecord",
            description = "Try fetching a book with an invalid ID format")
    public void getBookWithInvalidIdTest() {
        Response response = given()
                .header("Authorization", "Bearer " + AccountsTests.token)
                .log().all()
                .when()
                .get(String.format(APIRoutes.GET_BOOK_ID, "inalid_id"))
                .then()
                .log().all()
                .statusCode(422)
                .extract().response();

        ResponseLogger.attach("Delete request",response);

        String message = response.jsonPath().getString("detail[0].msg");
        Assert.assertEquals(message,
                "Input should be a valid integer, unable to parse string as an integer",
                "Error message mismatch");
    }
}
