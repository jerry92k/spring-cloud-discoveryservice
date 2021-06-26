package com.example.userservice.vo;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class RequestLogin {
    @NotNull(message="email cannot be null")
    @Size(min=2,message="email not be less than 2")
    @Email
    private String email;

    @NotNull(message="paasword cannot be null")
    @Size(min=8,message="password not be less than 8")
    private String password;
}
