package com.odontogama.odontogama_auth_service.application.UseCase;

import com.odontogama.auth.application.usecase.FindAllUsersUseCase;
import com.odontogama.auth.domain.model.User;
import com.odontogama.auth.domain.model.UserRole;
import com.odontogama.auth.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class FindAllUsersUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FindAllUsersUseCase findAllUsersUseCase;

    @Test
    void shouldReturnAllUsers(){

        //Arrange
        Pageable pageable = PageRequest.of(0,10);

        User user = User.create(
                "Eduardo@gmail.com",
                "123456789",
                UserRole.PATIENT
        );

        Page<User> page = new PageImpl<>(List.of(user));

        when(userRepository.findAll(pageable))
                .thenReturn(page);

        //Act
        Page<User> result = findAllUsersUseCase.execute(pageable);

        //Assert
        assertEquals(page,result);

        verify(userRepository).findAll(pageable);
    }

    @Test
    void shouldReturnEmptyPageWhenNoUsersExist(){

        //Arrange
        Pageable pageable = PageRequest.of(0,10);

        Page<User> emptyPage = Page.empty(pageable);

        when(userRepository.findAll(pageable))
                .thenReturn(emptyPage);

        //Act
        Page<User> result = findAllUsersUseCase.execute(pageable);

        //Assert
        assertEquals(0,result.getTotalElements());
        assertEquals(0,result.getContent().size());

        verify(userRepository).findAll(pageable);


    }
}
