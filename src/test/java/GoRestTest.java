import POJOClassses.User2;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class GoRestTest {

    public String createRandomName() {
        return RandomStringUtils.randomAlphabetic(8);
    }

    public String createRandomEmail() {
        return RandomStringUtils.randomAlphabetic(10) + "@gmail.com";
    }

    RequestSpecification requestSpecification;
    ResponseSpecification responseSpecification;

    @BeforeClass
    public void setUp() {
        baseURI = "https://gorest.co.in/public/v2/users";

        requestSpecification = new RequestSpecBuilder()
                .addHeader("Authorization", "Bearer ccf0c2848c38bcaf49c85439f89670e5e829e55ec05119b5a0d405bfe841dc5c")
                .setContentType(ContentType.JSON)
                .build();

        responseSpecification = new ResponseSpecBuilder()
                .log(LogDetail.BODY)
                .expectContentType(ContentType.JSON)
                .build();
    }


    @Test
    void createNewUser() {
        given()
                .spec(requestSpecification)
                .body("{\"name\":\"" + createRandomName() + "\",\"gender\":\"male\",\"email\":\"" + createRandomEmail() + "\",\"status\":\"active\"}")
                .when()
                .post("")
                .then()
                .spec(responseSpecification)
                .statusCode(201);
    }

    @Test
    void createNewUserWithMaps() {
        Map<String, String> user = new HashMap<>();
        user.put("name", createRandomName());
        user.put("gender", "female");
        user.put("email", createRandomEmail());
        user.put("status", "active");

        given()
                .spec(requestSpecification)
                .body(user)
                .when()
                .post("")
                .then()
                .spec(responseSpecification)
                .statusCode(201)
                .body("email", equalTo(user.get("email")));
    }

    User2 user2;
    Response response;

    @Test
    void createNewUserWithObject() {
        user2 = new User2();
        user2.setName(createRandomName());
        user2.setEmail(createRandomEmail());
        user2.setGender("female");
        user2.setStatus("active");

        response = given()
                .spec(requestSpecification)
                .body(user2)
                .when()
                .post("")
                .then()
                .spec(responseSpecification)
                .statusCode(201)
                .body("email", equalTo(user2.getEmail()))
                .extract().response();
    }

    @Test(dependsOnMethods = "createNewUserWithObject")
    void createNewUserWithObject2() {
        user2.setName(createRandomName());
        user2.setGender("male");

        given()
                .spec(requestSpecification)
                .body(user2)
                .when()
                .post("")
                .then()
                .statusCode(422)
                .spec(responseSpecification)
                .body("[0].message", equalTo("has already been taken"));
    }

    @Test(dependsOnMethods = "createNewUserWithObject")
    void getUserById() {
        given()
                .spec(requestSpecification)
                .pathParam("userId", response.path("id"))
                .when()
                .get("/{userId}")
                .then()
                .statusCode(200)
                .spec(responseSpecification)
                .body("name", equalTo(user2.getName()))
                .body("email", equalTo(user2.getEmail()));
    }

    @Test(dependsOnMethods = "createNewUserWithObject")
    void updateUser() {
        user2.setName(createRandomName());
        user2.setEmail(createRandomEmail());

        given()
                .spec(requestSpecification)
                .body(user2)
                .pathParam("userId", response.path("id"))
                .when()
                .put("/{userId}")
                .then()
                .statusCode(200)
                .spec(responseSpecification)
                .body("id", equalTo(response.path("id")))
                .body("name", equalTo(user2.getName()));
    }

    @Test(dependsOnMethods = "createNewUserWithObject")
    void deleteUser() {
        given()
                .spec(requestSpecification)
                .pathParam("userId", response.path("id"))
                .when()
                .delete("/{userId}")
                .then()
                .statusCode(204);
    }

    @Test(dependsOnMethods = {"createNewUserWithObject","deleteUser"})
    void deleteUserNegative() {
        given()
                .spec(requestSpecification)
                .pathParam("userId", response.path("id"))
                .when()
                .delete("/{userId}")
                .then()
                .statusCode(404);
    }


}
