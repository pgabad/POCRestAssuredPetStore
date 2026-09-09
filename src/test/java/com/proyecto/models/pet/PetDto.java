package com.proyecto.models.pet;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetDto {
  private Long id;
  private CategoryDto category;
  private String name;
  private List<String> photoUrls;
  private List<TagDto> tags;
  private String status;
}
