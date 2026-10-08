package com.odontogama.odontogama_auth_service.application.UseCase;

import com.odontogama.auth.application.usecase.DeleteUserUseCase;
import com.odontogama.auth.domain.exception.UserErrorCode;
import com.odontogama.auth.domain.exception.UserException;
import com.odontogama.auth.domain.model.User;
import com.odontogama.auth.domain.model.UserRole;
import com.odontogama.auth.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class DeleteUserUseCaseTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DeleteUserUseCase deleteUserUseCase;

    @Test
    void shouldDeactivateUserWhenUserExists(){
        //arrange
        Long userId = 1L;

        User user = User.create(
                "eduardo@email.com",
                "Senha123!",
                UserRole.PATIENT
        );

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        //Act
        deleteUserUseCase.execute(userId);

        //Assert
        assertFalse(user.getActive());

        verify(userRepository).findById(userId);
        verify(userRepository).save(user);
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExist() {

        // Arrange
        Long userId = 1L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        // Act

        UserException exception = assertThrows(
                UserException.class,
                () -> deleteUserUseCase.execute(userId)
        );
        // Assert
        assertEquals(UserErrorCode.USER_NOT_FOUND, exception.getErrorCode());

        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any());
    }


}
