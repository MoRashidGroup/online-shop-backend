package com.morashid.OnlineShop.admin.dto;

import com.morashid.OnlineShop.user.entity.Role;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * DTO ya admin kuunda user mpya.
 * 
 * Tofauti na RegisterRequest:
 *   - Admin anaweza kuchagua ROLE
 *   - Admin anaweza ku-set enabled
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {

    @NotBlank(message = "Full name is required")
    @Size(min = 3, max = 100, message = "Full name must be between 3 and 100 characters")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Size(max = 150, message = "Email must not exceed 150 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Phone number must be 10-15 digits")
    private String phoneNo;

    @NotNull(message = "Role is required")
    private Role role;

    private Boolean enabled = true;
}