package com.odontogama.auth.application.usecase;

import com.odontogama.auth.domain.model.User;
import com.odontogama.auth.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FindAllUsersUseCase {

    private final UserRepository userRepository;

    public Page<User> execute(Pageable pageable){

        return userRepository.findAll(pageable);

    }

}
