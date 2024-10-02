import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PetstoreTest {
    private long petId;

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "https://petstore.swagger.io/v2";
    }

    @Test
    @Order(1)
    public void testCreatePet() {
        String requestBody = """
                {
                  "id": 0,
                  "name": "doggie",
                  "status": "available"
                }""";

        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/pet")
                .then()
                .statusCode(200)
                .body("name", equalTo("doggie"))
                .extract()
                .response();

        petId = response.path("id");
    }

    @Test
    @Order(2)
    void testUpdatePet() {
        String updatedRequestBody = """
                {
                  "id": 0,
                  "category": {
                    "id": 0,
                    "name": "string"
                  },
                  "name": "EuroDog",
                  "photoUrls": [
                    "string"
                  ],
                  "tags": [
                    {
                      "id": 0,
                      "name": "string"
                    }
                  ],
                  "status": "pending"
                }""";


        given()
                .contentType(ContentType.JSON)
                .body(updatedRequestBody)
                .when()
                .put("/pet")
                .then()
                .statusCode(200)
                .body("name", equalTo("EuroDog"))
                .body("status", equalTo("pending"));
    }


    @Test
    @Order(3)
    void testFindPetsByStatus() {
        String status = "pending";


        Response response = given()
                .queryParam("status", status)
                .when()
                .get("/pet/findByStatus")
                .then()
                .statusCode(200)
                .extract()
                .response();


        List<String> statuses = response.jsonPath().getList("status");
        assertFalse(statuses.isEmpty());
        assertTrue(statuses.stream().allMatch(s -> s.equals("pending")));
        System.out.println(statuses);
    }

    @Test
    @Order(4)
    public void testCreateUserWithList() {
        String requestBody = """
                [
                  {
                    "id": 0,
                    "username": "Brud",
                    "firstName": "Rudolf",
                    "lastName": "Brayninger",
                    "email": "brayninger@example.com",
                    "password": "password123",
                    "phone": "1234567890",
                    "userStatus": 0
                  },\s
                  {
                    "id": 1,
                    "username": "Michael",
                    "firstName": "Hm",
                    "lastName": "Mm",
                    "email": "gg@example.com",
                    "password": "password",
                    "phone": "123",
                    "userStatus": 0
                  }
                ]""";

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/user/createWithList")
                .then()
                .statusCode(200)
                .body("code", equalTo(200))
                .body("message", equalTo("ok"));
    }

    @Test
    @Order(5)
    public void testLogin() {
        given()
                .queryParam("username", "pass")
                .queryParam("password", "aa")
                .when()
                .get("/user/login")
                .then()
                .statusCode(200)
                .body("code", equalTo(200))
                .body("message", containsString("logged in user session"));
    }

    @Test
    @Order(6)
    public void testDeleteUserSuccess() {
        given()
                .when()
                .delete("/user/Brud")
                .then()
                .statusCode(200);
    }

    @Order(7)
    @Test
    public void testUpdateUser() {
        String updatedUserJson = """
                {
                  "id": 1,
                  "username": "updatedUser",
                  "firstName": "Updated",
                  "lastName": "User",
                  "email": "updateduser@example.com",
                  "password": "newpassword123",
                  "phone": "9876543210",
                  "userStatus": 1
                }""";

        given()
                .contentType(ContentType.JSON)
                .body(updatedUserJson)
                .when()
                .put("/user/{username}", "testUser")
                .then()
                .statusCode(200)
                .body("code", equalTo(200));
    }

}


