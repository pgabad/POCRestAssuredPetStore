package com.proyecto.services;

import static io.restassured.RestAssured.given;

import com.proyecto.tests.BaseApiTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.util.Collections;
import java.util.Map;

public class ApiServices extends BaseApiTest {
  static {
    if (requestSpec == null) {
      BaseApiTest.setup();
    }
  }

  public static <T> Response post(T body, String pathPost) {

    return given().spec(requestSpec).contentType(ContentType.JSON).body(body).when().post(pathPost);
  }

  public static <T> Response post(T body, String pathPost, Map<String, Object> queryParams) {

    return given()
        .spec(requestSpec)
        .contentType(ContentType.JSON)
        .queryParams(queryParams)
        .body(body)
        .when()
        .post(pathPost);
  }

  public static Response postMultipart(
      String path,
      Map<String, Object> formParams,
      String fileControlName,
      String fileName,
      byte[] fileBytes,
      String mimeType) {

    return given()
        .spec(requestSpec)
        .formParams(formParams)
        .multiPart(fileControlName, fileName, fileBytes, mimeType)
        .when()
        .post(path);
  }

  public static <T, O> Response put(T body, String pathPost, O id) {

    return given()
        .spec(requestSpec)
        .contentType(ContentType.JSON)
        .body(body)
        .when()
        .put(pathPost + id);
  }

  public static <T> Response getDetails(String pathPost, T id) {

    return given().spec(requestSpec).when().get(pathPost + id);
  }

  public static <T> Response getDetails(String pathPost, T id, Map<String, Object> queryParams) {

    return given().spec(requestSpec).queryParams(queryParams).when().get(pathPost + id);
  }

  public static Response getList(String pathGet) {

    return given().spec(requestSpec).when().get(pathGet);
  }

  public static Response getList(String pathGet, Map<String, Object> queryParams) {

    return given()
        .spec(requestSpec)
        .queryParams(queryParams != null ? queryParams : Collections.emptyMap())
        .when()
        .get(pathGet);
  }

  public static <T> Response delete(String path, T id) {

    return given().spec(requestSpec).when().delete(path + id);
  }

  public static <T> Response patch(T body, String path, Map<String, Object> queryParams) {
    return given()
        .spec(requestSpec)
        .contentType(ContentType.JSON)
        .queryParams(queryParams)
        .body(body)
        .when()
        .patch(path);
  }
}
