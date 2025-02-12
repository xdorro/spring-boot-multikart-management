package com.example.multikart.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
public class UserProfileRequestDTO {
    @NotBlank
    private String name;
    @Email
    private String email;
    private String phone;

    private String password;

    private String provinceId;
    private String districtId;
    private String wardId;
    private String address;
}
