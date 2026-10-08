package com.odontogama.odontogama_auth_service.application.UseCase;

import com.odontogama.auth.application.usecase.FindUserByIdUseCase;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FindUserByIdUseCaseTest {
    @Mock
    UserRepository userRepository;

    @InjectMocks
    FindUserByIdUseCase findUserByIdUseCase;

    @Test
    void shouldReturnUserWhenIdExists(){
        // Arrange
        Long userId = 1L;

        User user = User.create(
                "eduardo@gmail.com",
                "Senha123!",
                UserRole.PATIENT
        );

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        //Act
        User result = findUserByIdUseCase.execute(userId);

        //Assert
        assertEquals(user,result);

        verify(userRepository).findById(userId);

    }
}
