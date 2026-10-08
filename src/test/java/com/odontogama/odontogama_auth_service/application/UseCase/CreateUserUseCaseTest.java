package com.odontogama.odontogama_auth_service.application.UseCase;

import com.odontogama.auth.application.usecase.CreateUserUseCase;
import com.odontogama.auth.domain.exception.UserErrorCode;
import com.odontogama.auth.domain.exception.UserException;
import com.odontogama.auth.domain.model.User;
import com.odontogama.auth.domain.model.UserRole;
import com.odontogama.auth.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;



@ExtendWith(MockitoExtension.class)
class CreateUserUseCaseTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private CreateUserUseCase createUserUseCase;

	@Captor
	private ArgumentCaptor<User> userCaptor;


	@Test
	void shouldThrowExceptionWhenEmailAlreadyExists() {
       // Arrange
		String email = "eduardo@email.com";
		String password = "Senha123!";

		when(userRepository.existsByEmail(email))
				.thenReturn(true);

		// Act
		UserException exception = assertThrows(
				UserException.class,
				() -> createUserUseCase.execute(email, password)
		);

		// Assert
		assertEquals(UserErrorCode.EMAIL_ALREADY_EXISTS,exception.getErrorCode());

		verify(userRepository).existsByEmail(email);
		verify(userRepository, never()).save(any());

	}

	@Test
	void shouldCreateUserWhenEmailDoesNotExist(){
		// Arrange
		String email = "eduardo@email.com";
		String password = "Senha123!";

		when(userRepository.existsByEmail(email))
				.thenReturn(false);

		// Act
		createUserUseCase.execute(email, password);


		// Assert
		verify(userRepository).save(userCaptor.capture());

		User savedUser = userCaptor.getValue();

		assertEquals(email,savedUser.getEmail());
	}

	@Test
	void shouldCreateUserAsPatient(){
		// Arrange
		String email = "eduardo@email.com";
		String password = "Senha123!";

		when(userRepository.existsByEmail(email))
				.thenReturn(false);

		// Act
		createUserUseCase.execute(email, password);

		// Assert
		verify(userRepository).save(userCaptor.capture());

		User savedUser = userCaptor.getValue();

		assertEquals(UserRole.PATIENT, savedUser.getRole());
	}





}
