package com.devotion.capture.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistredUser implements User {

    private String uniqueId;
    private String userName;
    private String userEmail;
    private String fullName;
    private String gender;
    private Date dateOfBirth;
    private String address;

}
