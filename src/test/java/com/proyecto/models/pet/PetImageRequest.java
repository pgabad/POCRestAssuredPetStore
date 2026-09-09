package com.proyecto.models.pet;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PetImageRequest {
  private Long petId;
  private String additionalMetadata;
  private String fileControlName;
  private String fileName;
  private byte[] fileBytes;
  private String mimeType;
}
