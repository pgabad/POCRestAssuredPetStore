package com.proyecto.services.utils;

import com.proyecto.models.pet.CategoryDto;
import com.proyecto.models.pet.PetDto;
import com.proyecto.models.pet.PetImageRequest;
import com.proyecto.models.pet.TagDto;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import javax.imageio.ImageIO;
import net.datafaker.Faker;

public class ApiUtils {

  private static final Faker faker = new Faker();

  private ApiUtils() {
    throw new IllegalStateException("Utility class");
  }

  public static PetDto generateRandomPet() {
    return PetDto.builder()
        .id(faker.number().numberBetween(100000L, 9999999999L))
        .name(faker.dog().name())
        .status(faker.options().option("available", "pending", "sold"))
        .category(
            CategoryDto.builder()
                .id(faker.number().numberBetween(1000L, 999999L))
                .name(faker.dog().breed())
                .build())
        .photoUrls(Collections.singletonList("http://example.com/photo.jpg"))
        .tags(Collections.singletonList(TagDto.builder().id(1L).name("cute").build()))
        .build();
  }

  public static PetImageRequest generatePetImageRequest(Long petId) {
    return PetImageRequest.builder()
        .petId(petId)
        .additionalMetadata("Metadata autogenerada: " + faker.lorem().sentence())
        .fileControlName("file")
        .fileName("generated-test-image.png")
        .fileBytes(createDynamicImageBytes())
        .mimeType("image/png")
        .build();
  }

  private static byte[] createDynamicImageBytes() {
    try {
      int width = 100;
      int height = 100;

      BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
      Graphics2D g2d = bufferedImage.createGraphics();

      g2d.setColor(Color.BLUE);
      g2d.fillRect(0, 0, width, height);
      g2d.setColor(Color.WHITE);
      g2d.drawString("API Test", 25, 50);
      g2d.dispose();

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ImageIO.write(bufferedImage, "png", baos);
      return baos.toByteArray();

    } catch (IOException e) {
      throw new IllegalStateException("Fallo crítico al generar la imagen dinámica en memoria", e);
    }
  }
}
