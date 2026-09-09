package com.proyecto.services;

import static io.restassured.RestAssured.given;

import com.proyecto.models.LoginRequest;
import io.restassured.http.ContentType;

public class ApiSecurity {

  private ApiSecurity() {
    throw new IllegalStateException("Utility class");
  }

  public static String getToken(String username, String password) {
    LoginRequest loginRequest = new LoginRequest(username, password);
    return given()
        .contentType(ContentType.JSON)
        .body(loginRequest)
        .post("https://demointranet.vitaly.es/seguridad-api2/auth/login/")
        .then()
        .statusCode(200)
        .extract()
        .path("token");
  }
}
