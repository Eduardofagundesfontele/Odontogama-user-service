package com.odontogama.auth.application.usecase;

import com.odontogama.auth.domain.exception.UserErrorCode;
import com.odontogama.auth.domain.exception.UserException;
import com.odontogama.auth.domain.model.User;
import com.odontogama.auth.domain.repository.UserRepository;
import com.odontogama.auth.infrastructure.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginUseCase {

    private  final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private  final JwtService jwtService;

    public String execute(String email,String password){
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new UserException (UserErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(password,user.getPassword())){
               throw  new UserException(UserErrorCode.INVALID_CREDENTIALS
               );
           }
        return  jwtService.generateToken(user);
    }
}
