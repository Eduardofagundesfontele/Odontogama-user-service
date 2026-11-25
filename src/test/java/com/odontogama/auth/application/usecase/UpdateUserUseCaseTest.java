package com.odontogama.auth.application.usecase;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UpdateUserUseCaseTest {
    @Mock
    UserRepository userRepository;

    @InjectMocks
    UpdateUserUseCase updateUserUseCase;

    @Test
    void shouldUpdateUserWhenUserExists(){
        //Arrange
        Long userId = 1L;
        String newEmail = "novoemail@gmail.com";
        String newPassword = "NovaSenha123!";

        User user = User.create(
                "eduardo@gmail.com",
                "Senha123!",
                UserRole.PATIENT
        );

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(userRepository.save(user))
                .thenReturn(user);

        // Act
        User result = updateUserUseCase.execute(
                userId,
                newEmail,
                newPassword
        );

        // Assert
        assertEquals(newEmail, result.getEmail());

        verify(userRepository).findById(userId);
        verify(userRepository).save(user);
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExist() {
        // Arrange
        Long userId = 1L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        // Act + Assert
        UserException exception = assertThrows(
                UserException.class,
                () -> updateUserUseCase.execute(
                        userId,
                        "novoemail@gmail.com",
                        "NovaSenha123!"
                )
        );

        assertEquals(
                UserErrorCode.USER_NOT_FOUND,
                exception.getErrorCode()
        );

        verify(userRepository).findById(userId);
    }

}
