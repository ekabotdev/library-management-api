package com.ekabotdev.library.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record RegisterRequest (

    @NotBlank
    @Size( max = 100)
     String name,

    @NotBlank
            @Size(max = 255)
     String email,

    @NotBlank
            @Size(min = 8, max = 72)
     String password,

    @NotBlank
            @Size(max = 50)
     String phone)
{

}
