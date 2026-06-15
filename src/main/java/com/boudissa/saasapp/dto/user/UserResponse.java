package com.boudissa.saasapp.dto.user;

import com.boudissa.saasapp.entities.UserRole;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {

    private String username;
    private String email;
    private String password;
    private UserRole role;
    private String firstName;
    private String lastName;
}
