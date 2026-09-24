package com.odontogama.auth.presentation.mapper;

import com.odontogama.auth.domain.model.User;
import com.odontogama.auth.presentation.dto.response.UserResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserPresentationMapper {

    UserResponse toResponse(User user);
}
