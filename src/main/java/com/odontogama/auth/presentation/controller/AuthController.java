package com.odontogama.auth.presentation.controller;

import com.odontogama.auth.application.usecase.LoginUseCase;
import com.odontogama.auth.presentation.dto.request.LoginRequest;
import com.odontogama.auth.presentation.dto.response.LoginResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthController {

    private final LoginUseCase loginUseCase;


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login (@Valid @RequestBody LoginRequest request){

        String token = loginUseCase.execute(request.email(),request.password());

        return ResponseEntity.ok(new LoginResponse(token));
    }
}
