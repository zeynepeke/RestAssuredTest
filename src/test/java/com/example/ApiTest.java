package com.example;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ApiTest {

    private static final String BASE_URL = "https://jsonplaceholder.typicode.com";

    @BeforeAll
    public static void setup() {

        RestAssured.baseURI = BASE_URL;
    }

    @Test
    @DisplayName("GET /posts/1 - Status Code, Body ve Süre Kontrolü")
    public void testGetSinglePost() {
        long startTime = System.currentTimeMillis();

        Response response = RestAssured
                .given()
                .when()
                .get("/posts/1")
                ;

        long responseTime = System.currentTimeMillis() - startTime;


        assertThat(response.statusCode(), is(200));


        int id = response.jsonPath().getInt("id");
        int userId = response.jsonPath().getInt("userId");
        String title = response.jsonPath().getString("title");

        assertThat(id, is(1));
        assertThat(userId, greaterThan(0));
        assertThat(title, not(isEmptyOrNullString()));


        assertThat(responseTime, lessThan(1000L));
    }

    @Test
    @DisplayName("GET /posts - Çoklu Kayıt Kontrolü")
    public void testGetAllPosts() {
        Response response = RestAssured
                .given()
                .when()
                .get("/posts");


        assertThat(response.statusCode(), is(200));



        int postCount = response.jsonPath().getList("$").size();
        assertThat(postCount, greaterThanOrEqualTo(100));
    }

    @Test
    @DisplayName("GET /users/1 - Nested JSON Kontrolü")
    public void testGetUserWithAddress() {
        Response response = RestAssured
                .given()
                .when()
                .get("/users/1");

        // Status 200 ve adres bilgileri boş değil
        assertThat(response.statusCode(), is(200));

        String city = response.jsonPath().getString("address.city");
        String geoLat = response.jsonPath().getString("address.geo.lat");

        assertThat(city, notNullValue());
        assertThat(geoLat, not(isEmptyOrNullString()));
    }
}
