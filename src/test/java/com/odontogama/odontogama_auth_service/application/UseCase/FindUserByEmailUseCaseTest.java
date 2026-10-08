package com.odontogama.odontogama_auth_service.application.UseCase;

import com.odontogama.auth.application.usecase.FindUserByEmailUseCase;
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
public class FindUserByEmailUseCaseTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    FindUserByEmailUseCase findUserByEmailUseCase;

    @Test
    void shouldReturnUserWhenEmailExists(){
        //Arrange
        String email = "eduardo@gmail.com";

        User user = User.create(
                email,
                "Senha123!",
                UserRole.PATIENT
        );

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));


        //Act
        User result = findUserByEmailUseCase.execute(email);

        //Assert
        assertEquals(user,result);
        verify(userRepository).findByEmail(email);


    }

    @Test
    void shouldThrowExceptionWhenEmailDoesNotExist(){

        //Arrange
        String email = "Test@gmail.com";

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        //Act
        UserException exception = assertThrows(
                UserException.class,
                ()->findUserByEmailUseCase.execute(email));
        //Assert
        assertEquals(
                UserErrorCode.USER_NOT_FOUND,
                exception.getErrorCode()
        );

        verify(userRepository).findByEmail(email);
    }






}
