package com.odontogama.odontogama_auth_service.infrastructure.persistence.mapper;

import com.odontogama.auth.domain.model.User;
import com.odontogama.auth.domain.model.UserRole;
import com.odontogama.auth.infrastructure.persistence.entity.UserEntity;
import com.odontogama.auth.infrastructure.persistence.mapper.UserPersistenceMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

public class UserPersistenceMapperTest {
    private static final Long USER_ID = 1L;
    private static final String EMAIL = "teste@email.com";
    private static final String PASSWORD = "Senha123!";
    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2026, 1, 15, 10, 30, 45);
    private static final LocalDateTime UPDATED_AT = LocalDateTime.of(2026, 2, 20, 14, 15, 30);

    private final UserPersistenceMapper mapper = Mappers.getMapper(UserPersistenceMapper.class);

    @Nested
    @DisplayName("toEntity")
    class ToEntity{

        @Test
        @DisplayName("should map all fields from User to UserEntity")
        void shouldMapUserToEntity(){
            //arrange
            User user = User.reconstruct(USER_ID,EMAIL,PASSWORD, UserRole.PATIENT,false,CREATED_AT,UPDATED_AT);

            //act
            UserEntity entity = mapper.toEntity(user);

            //assert
            assertThat(entity.getId()).isEqualTo(USER_ID);
            assertThat(user.getEmail()).isEqualTo(EMAIL);
            assertThat(user.getPassword()).isEqualTo(PASSWORD);
            assertThat(user.getRole()).isEqualTo(UserRole.PATIENT);
            assertThat(user.getActive()).isFalse();
            assertThat(user.getCreatedAt()).isEqualTo(CREATED_AT);
            assertThat(user.getUpdatedAt()).isEqualTo(UPDATED_AT);

        }

        @Test
        @DisplayName("should return null when user is null")
        void shouldRetutnNullWhenUserIsNull() {
            assertThat(mapper.toEntity(null)).isNull();
            }
    }

    @Nested
    @DisplayName("toDomain")
    class ToDomain{
        void shouldMapEntityToDomain(){
            // arrange
            UserEntity entity = UserEntity.builder()
                    .id(USER_ID)
                    .email(EMAIL)
                    .password(PASSWORD)
                    .role(UserRole.PATIENT)
                    .active(false)
                    .createdAt(CREATED_AT)
                    .updatedAt(UPDATED_AT)
                    .build();

            //act
            User user = mapper.toDomain(entity);

            //assert
            assertThat(user.getId()).isEqualTo(USER_ID);
            assertThat(user.getEmail()).isEqualTo(EMAIL);
            assertThat(user.getPassword()).isEqualTo(PASSWORD);
            assertThat(user.getRole()).isEqualTo(UserRole.PATIENT);
            assertThat(user.getActive()).isFalse();
            assertThat(user.getCreatedAt()).isEqualTo(CREATED_AT);
            assertThat(user.getUpdatedAt()).isEqualTo(UPDATED_AT);
        }

        @Test
        @DisplayName("should return null when entity is null")
        void shouldReturnNullWhenEntityIsNull(){
            assertThat(mapper.toDomain(null)).isNull();
        }

    }

}
