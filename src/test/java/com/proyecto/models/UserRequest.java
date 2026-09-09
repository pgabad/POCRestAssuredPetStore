package com.proyecto.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRequest {
  private String firstName;
  private String lastName;
  private String email;
  private String password;
  private String password2;
  private String captcha;
}
