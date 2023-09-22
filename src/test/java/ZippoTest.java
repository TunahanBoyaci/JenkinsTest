import POJOClassses.Location;
import POJOClassses.User;
import POJOClassses.User2;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import io.restassured.specification.RequestLogSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class ZippoTest {
    @Test
    void statusCodeTest() {
        given()
                .when()
                .get("http://api.zippopotam.us/us/90210")
                .then()
                .log().body();
    }

    @Test
    void contentTyp() {
        given()
                .when()
                .get("http://api.zippopotam.us/us/90210")
                .then()
                .contentType(ContentType.JSON);
    }

    @Test
    void checkCountryFromResponseBody() {
        given()
                .when()
                .get("http://api.zippopotam.us/us/90210")
                .then()
                .body("country", equalTo("United States"));
    }

    @Test
    void checkStateFromResponse() {
        given()
                .when()
                .get("http://api.zippopotam.us/us/90210")
                .then()
                .statusCode(200)
                .body("places[0].state", equalTo("California"));
    }

    @Test
    void checkStateAbbreviationFromResponse() {
        given()
                .when()
                .get("http://api.zippopotam.us/us/90210")
                .then()
                .statusCode(200)
                .body("places[0].'state abbreviation'", equalTo("CA"));
    }

    @Test
    void bodyArrayHasItem() {
        given()
                .when()
                .get("http://api.zippopotam.us/tr/01000")
                .then()
                .body("places.'longitude'", hasItem("37.4987"));

    }

    @Test
    void arraySize() {
        given()
                .when()
                .get("http://api.zippopotam.us/tr/01000")
                .then()
                .body("places.'longitude'", hasItem(1));

    }

    @Test
    void multipleTests() {
        given()
                .when()
                .get("http://api.zippopotam.us/tr/01000")
                .then()
                .statusCode(200)
                .body("places.'longitude'", hasItem("37.4987"))
                .body("places", hasSize(71));

    }

    @Test
    void pathParameterTest() {
        given()
                .pathParam("Country", "us")
                .pathParam("Zipcode", "90210")
                .log().uri()
                .when()
                .get("http://api.zippopotam.us/{Country}/{Zipcode}")
                .then()
                .log().body()
                .statusCode(200);
    }

    @Test
    void pathParameterTest2() {

        for (int i = 90210; i <= 90213; i++) {
            given()
                    .pathParam("Zipcode", i)
                    .when()
                    .get("http://api.zippopotam.us/us/{Zipcode}")
                    .then()
                    .log().body()
                    .statusCode(200);

        }
    }

    @Test
    void queryParameterTest() {

        given()
                .param("page", 3)
                .pathParam("apiname", "users")
                .log().uri()
                .when()
                .get("https://gorest.co.in/public/v1/{apiname}")
                .then()
                .log().body()
                .statusCode(200)
                .body("meta.pagination.page", equalTo(3));
    }

    @Test
    void queryParameterTest1() {

        for (int i = 1; i <= 10; i++) {
            given()
                    .param("page", i)
                    .pathParam("apiname", "users")
                    .log().uri()
                    .when()
                    .get("https://gorest.co.in/public/v1/{apiname}")
                    .then()
                    .log().body()
                    .statusCode(200)
                    .body("meta.pagination.page", equalTo(i));
        }
    }

    @Test(dataProvider = "pageNumbers")
    void queryParameterTestWithDataProvider(int page) {

        given()
                .param("page", page)
                .pathParam("apiname", "users")
                .log().uri()
                .when()
                .get("https://gorest.co.in/public/v1/{apiname}")
                .then()
                .log().body()
                .statusCode(200)
                .body("meta.pagination.page", equalTo(page));
    }

    @DataProvider
    public Object[][] pageNumbers() {
        Object[][] pageNumberList = {
                {1},
                {2},
                {3},
                {4},
                {5},
                {6},
                {7},
                {8},
                {9},
                {10}
        };

        return pageNumberList;
    }

    RequestSpecification requestSpecification;
    ResponseSpecification responseSpecification;

    @BeforeClass
    public void setUp() {
        baseURI = "https://gorest.co.in/public/v1";

        requestSpecification = new RequestSpecBuilder()
                .log(LogDetail.URI)
                .setContentType(ContentType.JSON)
                .addPathParam("apiname", "users")
                .addParam("page", 3)
                .build();

        responseSpecification = new ResponseSpecBuilder()
                .log(LogDetail.BODY)
                .expectStatusCode(200)
                .expectContentType(ContentType.JSON)
                .build();
    }

    @Test
    void requestAndResponseSpecTest() {
        given()
                .spec(requestSpecification)
                .when()
                .get("/{APIName}")
                .then()
                .body("meta.pagination.page", equalTo(3))
                .spec(responseSpecification);
    }

    @Test
    void baseURITest() {
        given()
                .param("page", 3)
                .pathParam("apiname", "users")
                .log().uri()
                .when()
                .get("/{apiname}")
                .then()
                .spec(responseSpecification)
                .body("meta.pagination.page", equalTo(3));
    }

    @Test
    void extractStringData() {

        String placeName = given()
                .pathParam("Country", "us")
                .pathParam("ZipCode", "90210")
                .when()
                .get("http://api.zippopotam.us/{Country}/{ZipCode}")
                .then()
                .statusCode(200)
                .extract().path("places[0].'place name'");

        System.out.println(placeName);

    }

    @Test
    void extractIntData() {
        int limit = given()
                .spec(requestSpecification)
                .when()
                .get("/{apiname}")// https://gorest.co.in/public/v1/users?page=2
                .then()
                .spec(responseSpecification)
                .extract().path("meta.pagination.limit");
        // We are not allowed to assign an int to a String(cannot assign a type to another type)

        System.out.println("limit = " + limit);
    }

    @Test
    void extractListData() {

        List<Integer> idList = given()
                .spec(requestSpecification)
                .when()
                .get("/{apiname}")// https://gorest.co.in/public/v1/users?page=2
                .then()
                .spec(responseSpecification)
                .extract().path("data.id");

        System.out.println("idList.size() = " + idList.size());
        System.out.println("idList.get(1) = " + idList.get(1));
        Assert.assertTrue(idList.contains(5148020));
    }

    @Test
    void extractListData1() {
        List<String> nameList = given()
                .pathParam("apiname", "users")
                .when()
                .get("/{apiname}")
                .then()
                .spec(responseSpecification)
                .extract().path("data.name");

        System.out.println("nameList.size() = " + nameList.size());
        System.out.println("nameList.get(5) = " + nameList.get(5));
    }

    @Test
    void extractResponse() {
        Response response = given()
                .pathParam("apiname", "users")
                .when()
                .get("/{apiname}")
                .then()
                .spec(responseSpecification)
                .extract().response();

        int limit = response.path("meta.pagination.limit");
        System.out.println("limit = " + limit);

        String current = response.path("meta.pagination.links.current");
        System.out.println("current = " + current);

        List<Integer> idList = response.path("data.id");
        System.out.println("idList.size() = " + idList.size());

        List<String> nameList = response.path("data.name");

        Assert.assertTrue(nameList.contains("Agastya Prajapat"));
    }

    @Test
    void extractJsonPOJO() {
        Location location = given()
                .pathParam("ZipCode", 90210)
                .when()
                .get("http://api.zippopotam.us/us/{ZipCode}")
                .then()
                .log().body()
                .extract().as(Location.class);

        // public class Location{                   public class Place{
        // String post code;                            String place name;
        // String country;                              String longitude;
        // String country abbreviation;                 String state;
        // List<Place> places;                          String state abbreviation;
        //                                              String latitude;
        // }                                           }

        System.out.println("location.getPostCode() " + location.getPostCode());
        System.out.println("location.getCountry() " + location.getCountry());
        System.out.println("location.getCountryAbbreviation() " + location.getCountryAbbreviation());
        System.out.println("location.getPlaces().get(0).getPlaceName() " + location.getPlaces().get(0).getPlaceName());
        System.out.println("location.getPlaces().get(0).getLongitude() " + location.getPlaces().get(0).getLongitude());
        System.out.println("location.getPlaces().get(0).getState() " + location.getPlaces().get(0).getState());
        System.out.println("location.getPlaces().get(0).getStateAbbreviation() " + location.getPlaces().get(0).getStateAbbreviation());
        System.out.println("location.getPlaces().get(0).getLatitude() " + location.getPlaces().get(0).getLatitude());
    }

    @Test
    void extractJsonPOJOTask() {
        User user = given()
                .when()
                .get("https://jsonplaceholder.typicode.com/todos/2")
                .then()
                .log().body()
                .extract().as(User.class);

        System.out.println(user.toString());
    }

    @Test
    void todoTest2() {
        given()
                .pathParam("status","203")
                .when()
                .get("https://httpstat.us/{status}")
                .then()
                .statusCode(203)
                .contentType(ContentType.TEXT)
                .log().body();
    }

    @Test
    void ToDoTest3(){
        given()
                .when()
                .get("https://jsonplaceholder.typicode.com/todos/2")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("title",equalTo("quis ut nam facilis et officia qui"))
                .log().body();
    }

    @Test
    void ToDoTest4(){
        Boolean completed = given()
                .when()
                .get("https://jsonplaceholder.typicode.com/todos/2")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().path("completed");

        Assert.assertFalse(completed);
    }

    // extract.path() ==> we can extract only one value or list of that values
    //                 String name = extract.path(data[0].name)
    //                 List<String> nameList extract.path(data.name)
    //
    // extract.as() ==> We can extract the entire response body.
    // It doesn't let us to extract one part of the body separately.
    //                  So we need to create classes for the entire body.
    //                  extract.as(Location.class)
    //                  extract.as(Place.class) cannot extract like this
    //
    // extract.jsonPath() ==> We can extract the entire body as well as any part of the body.
    // So if we need only one part of the
    //                        body we don't need to create classes for the entire body
    //                        extract.jsonPath().getObject(Location.class)
    //                        extract.jsonPath().getObject(Place.class)

    @Test
    void extractWihJSONPath(){
        User2 user2 = given()
                .when()
                .get("/users")
                .then()
                .extract().jsonPath().getObject("data[0]", User2.class);

        System.out.println(user2.getId());
        System.out.println(user2.getName());
    }

    @Test
    void extractWihJSONPath2(){
        List<User2> user2List = given()
                .when()
                .get("/users")
                .then()
                .extract().jsonPath().getList("data", User2.class);

        System.out.println(user2List.get(0).getId());
        System.out.println(user2List.get(0).getName());
    }

    @Test
    void extractWihJSONPath3(){
        Response response = given()
                .when()
                .get("/users")
                .then()
                .log().body()
                .extract().response();

        List<User2> user2List = response.jsonPath().getList("data",User2.class);
        System.out.println(user2List.get(5).getName());
    }

}
