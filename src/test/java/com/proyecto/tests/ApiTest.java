package com.proyecto.tests;

import static com.proyecto.services.utils.ApiUtils.generatePetImageRequest;
import static org.assertj.core.api.Assertions.assertThat;

import com.proyecto.models.pet.PetDto;
import com.proyecto.models.pet.PetImageRequest;
import com.proyecto.services.ApiServices;
import com.proyecto.services.utils.ApiUtils;
import io.restassured.response.Response;
import java.util.Map;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ApiTest extends BaseApiTest {

  private static final String PET_ENDPOINT_PREFIX = "/pet/";

  @Test
  @DisplayName("Flujo de alta de mascota")
  public void PostPetFlowReturn200OK() {

    PetDto requestPayload = ApiUtils.generateRandomPet();
    String endpoint = "/pet";

    Response response = ApiServices.post(requestPayload, endpoint);
    response.then().statusCode(HttpStatus.SC_OK);

    PetDto petResponseBody = response.as(PetDto.class);

    assertThat(petResponseBody)
        .as("El response debe coincidir exactamente con el payload enviado")
        .usingRecursiveComparison()
        .isEqualTo(requestPayload);

    PetImageRequest requestImage = generatePetImageRequest(petResponseBody.getId());

    String imageEndpoint = PET_ENDPOINT_PREFIX + requestImage.getPetId() + "/uploadImage";

    Map<String, Object> formParams =
        Map.of("additionalMetadata", requestImage.getAdditionalMetadata());
    Response imageResponse =
        ApiServices.postMultipart(
            imageEndpoint,
            formParams,
            requestImage.getFileControlName(),
            requestImage.getFileName(),
            requestImage.getFileBytes(),
            requestImage.getMimeType());
    imageResponse.then().statusCode(HttpStatus.SC_OK);

    String responseMessage = imageResponse.jsonPath().getString("message");

    assertThat(responseMessage)
        .as("El servidor debe confirmar la subida devolviendo el nombre del archivo")
        .contains(requestImage.getFileName());

    Response getResponse = ApiServices.getDetails(PET_ENDPOINT_PREFIX, petResponseBody.getId());
    getResponse.then().statusCode(HttpStatus.SC_OK);

    PetDto getResponseBody = getResponse.as(PetDto.class);
    assertThat(getResponseBody)
        .as("La mascota recuperada del servidor debe coincidir con la creada")
        .usingRecursiveComparison()
        .isEqualTo(petResponseBody);

    Response deleteResponse = ApiServices.delete(PET_ENDPOINT_PREFIX, petResponseBody.getId());
    deleteResponse.then().statusCode(HttpStatus.SC_OK);
  }
}
