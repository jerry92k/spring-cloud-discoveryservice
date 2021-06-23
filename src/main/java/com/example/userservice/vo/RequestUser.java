package com.example.userservice.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class RequestUser {
    @NotNull(message="email cannot be nulll")
    @Size(min=2, message="email not be less than two character")
    private String email;

    @NotNull(message="name cannot be nulll")
    @Size(min=2, message="name not be less than two character")
    private String name;

    @NotNull(message="password cannot be nulll")
    @Size(min=8, message="password not be less than eight character")
    private String password;


}
