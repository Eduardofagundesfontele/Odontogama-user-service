package com.odontogama.auth.presentation.controller;

import com.odontogama.auth.application.usecase.*;
import com.odontogama.auth.domain.exception.UserErrorCode;
import com.odontogama.auth.domain.exception.UserException;
import com.odontogama.auth.domain.model.User;
import com.odontogama.auth.domain.model.UserRole;
import com.odontogama.auth.presentation.dto.response.UserResponse;
import com.odontogama.auth.presentation.exception.GlobalExceptionHandler;
import com.odontogama.auth.presentation.mapper.UserPresentationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private CreateUserUseCase createUserUseCase;
    @Mock
    private FindUserByIdUseCase findUserByIdUseCase;
    @Mock
    private FindUserByEmailUseCase findUserByEmailUseCase;
    @Mock
    private FindAllUsersUseCase findAllUsersUseCase;
    @Mock
    private DeleteUserUseCase deleteUserUseCase;
    @Mock
    private UpdateUserUseCase updateUserUseCase;
    @Mock
    private UserPresentationMapper mapper;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    private Authentication authAs(String email, String role) {
        return new UsernamePasswordAuthenticationToken(
                email,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + role))
        );
    }

    private static final Long USER_ID = 1L;
    private static final Long NON_EXISTENT_ID = 999L;
    private static final String EMAIL = "teste@email.com";
    private static final String NEW_EMAIL = "novo@email.com";
    private static final String PASSWORD = "Senha123!";
    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2026, 1, 15, 10, 30, 45);
    private static final LocalDateTime UPDATED_AT = LocalDateTime.of(2026, 2, 20, 14, 15, 30);

    private static final String VALID_BODY = """
            {
                "email": "teste@email.com",
                "password": "Senha123!"
            }
            """;

    private static final String INVALID_EMAIL_BODY = """
            {
                "email": "invalid-email",
                "password": "Senha123!"
            }
            """;

    private static final String BLANK_PASSWORD_BODY = """
            {
                "email": "teste@email.com",
                "password": ""
            }
            """;

    private static final String UPDATE_BODY = """
            {
                "email": "novo@email.com",
                "password": "Senha123!"
            }
            """;

    private User user;
    private UserResponse response;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();


        user = mock(User.class);
        response = new UserResponse(
                USER_ID,
                EMAIL,
                UserRole.PATIENT,
                true,
                CREATED_AT,
                UPDATED_AT);

    }

    @Nested
    @DisplayName("POST /users")
    class Create {

        @Test
        @DisplayName("should return 201 and the created user")
        void shouldCreateUser() throws Exception {
            // arrange
            when(createUserUseCase.execute(EMAIL, PASSWORD))
                    .thenReturn(user);

            when(mapper.toResponse(user))
                    .thenReturn(response);
            //act
            mockMvc.perform(
                            post("/users")
                                    .contentType(APPLICATION_JSON)
                                    .content(VALID_BODY)
                    )

                    //assert
                    .andExpect(status().isCreated())
                    .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(USER_ID))
                    .andExpect(jsonPath("$.email").value(EMAIL))
                    .andExpect(jsonPath("$.role").value("PATIENT"))
                    .andExpect(jsonPath("$.active").value(true));

            verify(createUserUseCase).execute(EMAIL, PASSWORD);


        }

        @Test
        @DisplayName("should return 400 when email is invalid")
        void shouldReturn400WhenEmailIsInvalid() throws Exception {
            //act
            ResultActions result = mockMvc.perform(post("/users")
                    .contentType(APPLICATION_JSON)
                    .content(INVALID_EMAIL_BODY)
            );
            //assert
            result.andExpect(status().isBadRequest());

            verify(createUserUseCase, never()).execute(any(), any());
        }

        @Test
        @DisplayName("should return 400 when password is blank")
        void shouldReturn400WhenPasswordIsBlank() throws Exception {
            // act
            ResultActions result = mockMvc.perform(post("/users")
                    .contentType(APPLICATION_JSON)
                    .content(BLANK_PASSWORD_BODY));

            // assert
            result.andExpect(status().isBadRequest());

            verify(createUserUseCase, never()).execute(any(), any());
        }
    }

    @Test
    @DisplayName("should return 400 when body is missing")
    void shouldReturn400WhenBodyIsMissing() throws Exception {
        // act
        ResultActions result = mockMvc.perform(post("/users")
                .contentType(APPLICATION_JSON));

        // assert
        result.andExpect(status().isBadRequest());

        verify(createUserUseCase, never()).execute(any(), any());
    }

    @Nested
    @DisplayName("GET /users/{id}")
    class FindById {

        @Test
        @DisplayName("should return 200 and the user")
        void shouldFindUserById() throws Exception {
            // arrange
            when(findUserByIdUseCase.execute(UserControllerTest.USER_ID)).thenReturn(user);
            when(mapper.toResponse(user)).thenReturn(response);
            when(user.getEmail()).thenReturn(EMAIL);

            // act
            ResultActions result = mockMvc.perform(get("/users/{id}", USER_ID)
                    .principal(authAs(EMAIL, "PATIENT")));

            // assert
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value
                            (UserControllerTest.USER_ID))
                    .andExpect(jsonPath("$.email").value(EMAIL))
                    .andExpect(jsonPath("$.role").value("PATIENT"));
        }

        @Test
        @DisplayName("should return 404 when user does not exist")
        void shouldReturn404WhenUserNotFound() throws Exception {

            //arrange
            when(findUserByIdUseCase.execute(NON_EXISTENT_ID)).thenThrow(new UserException(UserErrorCode.USER_NOT_FOUND));

            //act
            ResultActions result = mockMvc.perform(get("/users/{id}", NON_EXISTENT_ID));

            //assert
            result.andExpect(status().isNotFound());

        }

        @Test
        @DisplayName("should return 403 when user is not owner nor admin")
        void shouldReturn403WhenUserIsNotOwnerNorAdmin() throws Exception {

            //arrange
            when(findUserByIdUseCase.execute(USER_ID)).thenReturn(user);
            when(user.getEmail()).thenReturn(EMAIL);

            //act
            ResultActions result = mockMvc.perform(get("/users/{id}", USER_ID)
                    .principal(authAs("outro@gmail.com", "PATIENT")));

            //assert
            result.andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("should return 200 when admin accesses another user")
        void shouldReturn200WhenAdminAccessesAnotherUser() throws Exception {
            //Arrange
            when(findUserByIdUseCase.execute(UserControllerTest.USER_ID)).thenReturn(user);
            when(mapper.toResponse(user)).thenReturn(response);
            when(user.getEmail()).thenReturn(EMAIL);

            //Act
            ResultActions result = mockMvc.perform(get("/users/{id}", USER_ID)
                    .principal(authAs("admin@gmail.om", "ADMIN")));

            //Assert
            result.andExpect(status().isOk());
        }


        @Test
        @DisplayName("should return 400 when id is not a number")
        void shouldReturn400WhenIdIsNotANumber() throws Exception {
            //act
            ResultActions resultActions = mockMvc.perform(get("/users/{id}", "abc"));

            //assert
            resultActions.andExpect(status().isBadRequest());

            verify(findUserByIdUseCase, never()).execute(any());
        }
    }

    @Nested
    @DisplayName("GET /users/email")
    class FindByEmail {

        @Test
        @DisplayName("should return 200 and the user")
        void shouldFindUserByEmail() throws Exception {
            //arrange
            when(findUserByEmailUseCase.execute(EMAIL)).thenReturn(user);
            when(mapper.toResponse(user)).thenReturn(response);

            //act
            ResultActions resultActions = mockMvc.perform(get("/users/email").param("email", EMAIL));

            //assert
            resultActions.andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(USER_ID))
                    .andExpect(jsonPath("$.email").value(EMAIL));
        }

        @Test
        @DisplayName("should return 404 when email does not exist")
        void shouldReturn404WhenEmailNotFound() throws Exception {
            //arrange
            when(findUserByEmailUseCase.execute(EMAIL))
                    .thenThrow(new UserException(UserErrorCode.USER_NOT_FOUND));

            //act
            ResultActions resultActions = mockMvc.perform(get("/users/email").param("email", EMAIL));

            //assert
            resultActions.andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("should return 400 when email param is missing")
        void shouldReturn400WhenEmailParamIsMissing() throws Exception {

            //act
            ResultActions result = mockMvc.perform(get("/users/email"));

            //assert
            result.andExpect(status().isBadRequest());

            verify(findUserByEmailUseCase, never()).execute(any());
        }

    }

    @Nested
    @DisplayName("GET /users")
    class FindAll {

        @Test
        @DisplayName("should return 200 and a page of users")
        void shouldReturnPagedUsers() throws Exception {
            //arrange
            Page<User> page = new PageImpl<>(List.of(user), PageRequest.of(0, 10), 1);

            when(findAllUsersUseCase.execute(any(Pageable.class))).thenReturn(page);
            when(mapper.toResponse(user)).thenReturn(response);

            //act
            ResultActions resultActions = mockMvc.perform(get("/users")
                    .param("page", "0")
                    .param("size", "10"));

            //assert
            resultActions.andExpect(status().isOk())
                    .andExpect(jsonPath("$.content.length()").value(1))
                    .andExpect(jsonPath("$.content[0].id").value(USER_ID))
                    .andExpect(jsonPath("$.content[0].email").value(EMAIL));
        }

        @Test
        @DisplayName("should use default pagination: size 10, sorted by id")
        void shouldUseDefaultPagination() throws Exception {

            //arrange
            ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);

            when(findAllUsersUseCase.execute(any(Pageable.class))).thenReturn(Page.empty((PageRequest.of(0, 10))));

            //act
            ResultActions resultActions = mockMvc.perform(get("/users"));

            //assert
            verify(findAllUsersUseCase).execute(captor.capture());
            Pageable pageable = captor.getValue();
            assertThat(pageable.getPageNumber()).isZero();
            assertThat(pageable.getPageSize()).isEqualTo(10);
            assertThat(pageable.getSort()).isEqualTo(Sort.by("id"));

        }

        @Test
        @DisplayName("should return 200 and an empty page when there are no users")
        void shouldReturnEmptyPage() throws Exception {
            //arrange

            when(findAllUsersUseCase.execute(any(Pageable.class))).thenReturn(Page.empty(PageRequest.of(0, 10)));

            //act
            ResultActions resultActions = mockMvc.perform(get("/users"));

            //assert
            resultActions.andExpect(status().isOk()).andExpect(jsonPath("$.content.length()").value(0));

        }
    }

    @Nested
    @DisplayName("PUT /users/{id}")
    class Update {

        @Test
        @DisplayName("should return 200 and the updated user")
        void shouldUpdateUser() throws Exception {
            UserResponse userResponse = new UserResponse(
                    USER_ID,
                    NEW_EMAIL,
                    UserRole.PATIENT,
                    true,
                    CREATED_AT,
                    UPDATED_AT);

            when(findUserByIdUseCase.execute(USER_ID)).thenReturn(user);
            when(updateUserUseCase.execute(USER_ID, NEW_EMAIL, PASSWORD)).thenReturn(user);
            when(mapper.toResponse(user)).thenReturn(userResponse);
            when(user.getEmail()).thenReturn(EMAIL);

            //act
            ResultActions resultActions = mockMvc.perform(put("/users/{id}", USER_ID)
                    .contentType(APPLICATION_JSON)
                    .content(UPDATE_BODY)
                    .principal(authAs(EMAIL, "PATIENT")));

            //assert
            resultActions.andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(USER_ID))
                    .andExpect(jsonPath("$.email").value(NEW_EMAIL));

            verify(updateUserUseCase).execute(USER_ID, NEW_EMAIL, PASSWORD);
        }

        @Test
        @DisplayName("Should return 400 when body is invalid")
        void shouldReturn400WhenUpdateBodyIsInvalid() throws Exception {
            //ac
            ResultActions resultActions = mockMvc.perform(put("/users/{id}", USER_ID)
                    .contentType(APPLICATION_JSON)
                    .content(INVALID_EMAIL_BODY));
            //assert
            resultActions.andExpect(status().isBadRequest());
            verify(updateUserUseCase, never()).execute(any(), any(), any());
        }

        @Test
        @DisplayName("should return 404 when user does not exist")
        void shouldReturn404WhenUpdatingNonExistentUser() throws Exception {
            //arrange
            when(findUserByIdUseCase.execute(NON_EXISTENT_ID))
                    .thenThrow(new UserException(UserErrorCode.USER_NOT_FOUND));

            //act
            ResultActions resultActions = mockMvc.perform(put("/users/{id}", NON_EXISTENT_ID)
                    .contentType(APPLICATION_JSON)
                    .content(UPDATE_BODY)
                    .principal(authAs(EMAIL, "PATIENT")));

            // assert
            resultActions.andExpect(status().isNotFound());
            verify(updateUserUseCase, never()).execute(any(), any(), any());

        }

        @Test
        @DisplayName("should return 403 when user is not owner nor admin")
        void shouldReturn403WhenUserIsNotOwnerNorAdmin() throws Exception {
            //Arrange
            when(findUserByIdUseCase.execute(USER_ID)).thenReturn(user);
            when(user.getEmail()).thenReturn(EMAIL);

            //Act
            ResultActions resultActions = mockMvc.perform(put("/users/{id}", USER_ID)
                    .content(UPDATE_BODY)
                    .contentType(APPLICATION_JSON)
                    .principal(authAs("outro@gmail.com", "PATIENT")));

            //Assert
            resultActions.andExpect(status().isForbidden());
            verify(updateUserUseCase, never()).execute(any(), any(), any());
        }

        @Test
        @DisplayName("should return 200 when admin updates another user")
        void shouldReturn200WhenAdminUpdatesAnotherUser() throws Exception{
            //Arrange
            when(findUserByIdUseCase.execute(USER_ID)).thenReturn(user);
            when(user.getEmail()).thenReturn(EMAIL);
            when(updateUserUseCase.execute(USER_ID,NEW_EMAIL,PASSWORD)).thenReturn(user);
            when(mapper.toResponse(user)).thenReturn(response);

            //act
            ResultActions resultActions = mockMvc.perform(put("/users/{id}", USER_ID)
                    .content(UPDATE_BODY)
                    .contentType(APPLICATION_JSON)
                    .principal(authAs("outro@gmail.com", "ADMIN")));

            //assert
            resultActions.andExpect(status().isOk());
            verify(updateUserUseCase).execute(USER_ID,NEW_EMAIL,PASSWORD);

        }

    }

    @Nested
    @DisplayName("DELETE /users/{id}")
    class Delete {

        @Test
        @DisplayName("should return 204 and call the use case")
        void shouldDeleteUser() throws Exception {
            //act
            ResultActions resultActions = mockMvc.perform(delete("/users/{id}", USER_ID));

            //assert
            resultActions.andExpect(status().isNoContent()).andExpect(content().string(""));

            verify(deleteUserUseCase).execute(USER_ID);
        }

        @Test
        @DisplayName("should return 404 when user does not exist")
        void shouldReturn404WhenDeletingNonExistentUser() throws Exception {
            //arrange
            doThrow(new UserException(UserErrorCode.USER_NOT_FOUND))
                    .when(deleteUserUseCase).execute(NON_EXISTENT_ID);

            //act
            ResultActions resultActions = mockMvc.perform(delete("/users/{id}", NON_EXISTENT_ID));

            //assert
            resultActions.andExpect(status().isNotFound());
        }

    }
}



