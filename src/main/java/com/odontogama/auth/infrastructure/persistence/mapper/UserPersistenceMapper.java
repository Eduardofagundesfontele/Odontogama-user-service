package com.odontogama.auth.infrastructure.persistence.mapper;

import com.odontogama.auth.domain.model.User;
import com.odontogama.auth.infrastructure.persistence.entity.UserEntity;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface UserPersistenceMapper {

    UserEntity toEntity(User user);

    default User toDomain(UserEntity entity){
        if (entity == null) return null;
        return User.reconstruct(entity.getId(),entity.getEmail(),entity.getPassword(),entity.getRole(),entity.getActive(),entity.getCreatedAt(),entity.getUpdatedAt());
    }
}
