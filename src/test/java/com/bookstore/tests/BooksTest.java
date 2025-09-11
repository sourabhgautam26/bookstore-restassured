package com.bookstore.tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import com.bookstore.base.BaseSetup;
import com.bookstore.constants.APIRoutes;
import com.bookstore.util.TestUtil;
import com.bookstore.util.ResponseLogger;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class BooksTest extends BaseSetup {
    public static int bookId;

    String jsonPayload = TestUtil.readJsonFromFile("createBook.json");

    @Test(priority = 1, dependsOnMethods = { "com.bookstore.tests.AccountsTests.signUpUser",
            "com.bookstore.tests.AccountsTests.verifySignUpUserAndGenerateToken" },
            description = "Create a new book record in the system")
    public void createBookRecord() {
        // Extract response first
        Response response = given()
                .header("Authorization", "Bearer " + AccountsTests.token)
                .log().all()
                .body(jsonPayload)
                .when()
                .post(APIRoutes.ADD_NEW_BOOK)
                .andReturn();

        // Attach to reporter for listener
        ResponseLogger.attach(jsonPayload,response);

        // Assertions
        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 201");
        bookId = response.jsonPath().getInt("id");
        Assert.assertNotNull(bookId, "Book ID should not be null after creation");
    }

    @Test(priority = 2, dependsOnMethods = "createBookRecord",
            description = "Retrieve the created book by its ID")
    public void fetchBookDetailsById() {
        Response response = given()
                .header("Authorization", "Bearer " + AccountsTests.token)
                .log().all()
                .when()
                .get(String.format(APIRoutes.GET_BOOK_ID, bookId))
                .andReturn();

        ResponseLogger.attach("GET request to /books/" + bookId,response);

        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");
        Assert.assertTrue(response.jsonPath().getString("name").contains("The Silent Patient"),
                "Fetched book name should match the expected title");
    }

    @Test(priority = 3, dependsOnMethods = "createBookRecord",
            description = "Modify an existing book and verify the update")
    public void modifyBookInformation() {
        String updatePayload = TestUtil.readJsonFromFile("updateBook.json");

        Response response = given()
                .header("Authorization", "Bearer " + AccountsTests.token)
                .body(updatePayload)
                .log().all()
                .when()
                .put(String.format(APIRoutes.GET_BOOK_ID, bookId))
                .andReturn();

        ResponseLogger.attach(jsonPayload,response);

        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");

        JsonPath expectedJson = new JsonPath(updatePayload);
        JsonPath actualJson = new JsonPath(response.asString());
        Assert.assertEquals(actualJson.getString("name"), expectedJson.getString("name"),
                "Book title should reflect the updated value");
    }

    @Test(priority = 4, dependsOnMethods = "createBookRecord",
            description = "Fetch the list of all available books")
    public void listAllBooks() {
        Response response = given()
                .header("Authorization", "Bearer " + AccountsTests.token)
                .log().all()
                .when()
                .get(APIRoutes.GET_ALL_BOOKS)
                .andReturn();

        ResponseLogger.attach("GET request to /books/" + bookId,response);

        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");
        Assert.assertTrue(response.jsonPath().getList("books").size() > 0,
                "The collection of books should not be empty");
    }

    @Test(priority = 5, dependsOnMethods = "createBookRecord",
            description = "Remove the created book using its ID")
    public void removeBookById() {
        Response response = given()
                .header("Authorization", "Bearer " + AccountsTests.token)
                .log().all()
                .when()
                .delete(String.format(APIRoutes.GET_BOOK_ID, bookId))
                .andReturn();

        ResponseLogger.attach("Delete request ,to /books/" + bookId,response);

        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");
        Assert.assertTrue(response.jsonPath().getString("message").contains("Book deleted successfully"),
                "The response should confirm book deletion");
    }
}
