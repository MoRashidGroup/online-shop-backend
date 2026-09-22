package com.morashid.OnlineShop.auth.dto;



import com.morashid.OnlineShop.user.entity.Role;
import lombok.*;

/**
 * Response ya login/register.
 * 
 * Kwa Phase 5: tunarudisha user info tu (bila token).
 * Kwa Phase 6: tutaongeza "token" field (JWT).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private Long id;
    private String fullName;
    private String email;
    private String phoneNo;
    private Role role;
    private String message;
    private String token;
}
