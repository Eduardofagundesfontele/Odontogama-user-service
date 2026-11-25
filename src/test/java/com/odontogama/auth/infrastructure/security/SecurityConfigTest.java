package com.odontogama.auth.infrastructure.security;

import com.odontogama.auth.application.usecase.*;
import com.odontogama.auth.presentation.controller.UserController;
import com.odontogama.auth.presentation.mapper.UserPresentationMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
public class SecurityConfigTest {

    @MockitoBean
    private CreateUserUseCase createUserUseCase;
    @MockitoBean
    private FindUserByIdUseCase findUserByIdUseCase;
    @MockitoBean
    private FindUserByEmailUseCase findUserByEmailUseCase;
    @MockitoBean
    private FindAllUsersUseCase findAllUsersUseCase;
    @MockitoBean
    private DeleteUserUseCase deleteUserUseCase;
    @MockitoBean
    private UpdateUserUseCase updateUserUseCase;
    @MockitoBean
    private UserPresentationMapper mapper;
    @MockitoBean
    private JwtService jwtService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("should return 401 when request has no token")
    void shouldReturn401WhenRequestHasNoToken() throws Exception{

        //Act
        ResultActions resultActions = mockMvc.perform(get("/users"));

        //Assert
        resultActions.andExpect(status().isUnauthorized());
        verifyNoInteractions(findAllUsersUseCase);
    }
}
