package com.morashid.OnlineShop.admin.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * DTO ya admin ku-enable/disable user.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserStatusRequest {

    @NotNull(message = "Enabled status is required")
    private Boolean enabled;
}