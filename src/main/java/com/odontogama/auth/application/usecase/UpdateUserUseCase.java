package com.odontogama.auth.application.usecase;

import com.odontogama.auth.domain.exception.UserErrorCode;
import com.odontogama.auth.domain.exception.UserException;
import com.odontogama.auth.domain.model.User;
import com.odontogama.auth.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateUserUseCase {
    private final UserRepository userRepository;

    public User execute(Long id, String email, String password) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        user.update(email, password);

        return userRepository.save(user);
    }
}
