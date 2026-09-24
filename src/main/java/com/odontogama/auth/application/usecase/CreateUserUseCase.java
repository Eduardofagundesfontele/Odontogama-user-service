package com.odontogama.auth.application.usecase;

import com.odontogama.auth.domain.exception.UserErrorCode;
import com.odontogama.auth.domain.exception.UserException;
import com.odontogama.auth.domain.model.User;
import com.odontogama.auth.domain.model.UserRole;
import com.odontogama.auth.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateUserUseCase {

    private final UserRepository userRepository;

    public User execute(String email,String password){
        if (userRepository.existsByEmail(email)){
            throw new UserException(UserErrorCode.EMAIL_ALREADY_EXISTS);
        }
        User user = User.create(
                email,
                password,
                UserRole.PATIENT);

        return userRepository.save(user);

    }
}
