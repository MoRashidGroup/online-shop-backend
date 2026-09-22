package com.morashid.OnlineShop.user.dto;



import com.morashid.OnlineShop.user.entity.Role;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO ya kurudisha user info.
 * 
 * HATUNA password! Hii ni kwa security.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;
    private String fullName;
    private String email;
    private String phoneNo;
    private Role role;
    private Boolean enabled;
    private LocalDateTime createdAt;
}
