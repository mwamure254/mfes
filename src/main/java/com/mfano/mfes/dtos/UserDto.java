package com.mfano.mfes.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter 
@Getter 
@NoArgsConstructor 
public class UserDto {
  @Email 
  @NotBlank 
  private String email;
  @NotBlank
  private String password;
  private String fin;
  private String lan;
  private String gender;
  @NotNull 
  private Long branch;
  @NotNull 
  private Long role;

}
