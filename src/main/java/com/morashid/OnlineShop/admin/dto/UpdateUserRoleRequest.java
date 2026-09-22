package com.morashid.OnlineShop.admin.dto;

import com.morashid.OnlineShop.user.entity.Role;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * DTO ya admin kubadilisha role ya user.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRoleRequest {

    @NotNull(message = "Role is required")
    private Role role;
}