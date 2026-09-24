package com.odontogama.auth.presentation.dto.response;

import com.odontogama.auth.domain.model.UserRole;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String email,
        UserRole role,
        Boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
