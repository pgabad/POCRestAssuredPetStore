package com.proyecto.tests;

import io.github.cdimascio.dotenv.Dotenv;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import java.io.FileOutputStream;
import java.util.Properties;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

@SuppressWarnings("java:S1118")
public abstract class BaseApiTest {
  protected static RequestSpecification requestSpec;
  protected static ResponseSpecification responseSpec;

  @BeforeAll
  protected static void setup() {
    if (requestSpec == null) {
      Dotenv dotenv = Dotenv.load();
      String baseUrl = dotenv.get("BASE_URL");

      System.getProperty("base.url", "baseUrl");
      RestAssured.baseURI = baseUrl;

      requestSpec =
          new RequestSpecBuilder()
              .setBaseUri(baseUrl)
              .addHeader("accept", "application/json")
              .addFilter(new AllureRestAssured())
              .build();

      RestAssured.requestSpecification = requestSpec;
    }
    if (responseSpec == null) {
      responseSpec =
          new ResponseSpecBuilder()
              .expectContentType(ContentType.JSON) // Validar que siempre responda JSON
              .expectResponseTime(org.hamcrest.Matchers.lessThan(5000L)) // Validar rendimiento
              .build();
      RestAssured.responseSpecification = responseSpec;
    }
  }

  @AfterAll // Se ejecuta al final de todos los tests
  static void tearDownEnv() {
    try (FileOutputStream fos =
        new FileOutputStream("target/allure-results/environment.properties")) {
      Properties props = new Properties();
      props.setProperty("Environment", "Localhost / Docker");
      props.setProperty("Java Version", System.getProperty("java.version"));
      props.setProperty("User", System.getProperty("user.name"));
      props.store(fos, "Allure Environment Properties");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
