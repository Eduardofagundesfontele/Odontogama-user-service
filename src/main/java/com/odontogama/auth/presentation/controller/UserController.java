package com.odontogama.auth.presentation.controller;

import com.odontogama.auth.application.usecase.*;
import com.odontogama.auth.domain.exception.UserErrorCode;
import com.odontogama.auth.domain.exception.UserException;
import com.odontogama.auth.domain.model.User;
import com.odontogama.auth.presentation.dto.request.CreateUserRequest;
import com.odontogama.auth.presentation.dto.request.UpdateUserRequest;
import com.odontogama.auth.presentation.dto.response.UserResponse;
import com.odontogama.auth.presentation.mapper.UserPresentationMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("users")
@RequiredArgsConstructor
public class UserController {

    private final CreateUserUseCase createUserUseCase;
    private final FindUserByIdUseCase findUserByIdUseCase;
    private final FindUserByEmailUseCase findUserByEmailUseCase;
    private final FindAllUsersUseCase findAllUsersUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final UserPresentationMapper mapper;

    private void checkOwnerOrAdmin(User user, Authentication authentication) {

        boolean isOwner = user.getEmail().equals(authentication.getName());

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        if (!isOwner && !isAdmin) {
            throw new UserException(UserErrorCode.ACCESS_DENIED);
        }


    }


    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest request) {

        User user = createUserUseCase.execute(request.email(), request.password());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toResponse(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById(@PathVariable Long id, Authentication authentication) {

        User user = findUserByIdUseCase.execute(id);

        checkOwnerOrAdmin(user, authentication);

        return ResponseEntity.ok(mapper.toResponse(user));
    }


    @GetMapping
    public ResponseEntity<Page<UserResponse>> findAll(@PageableDefault(size = 10, sort = "id") Pageable pageable) {

        Page<User> users = findAllUsersUseCase.execute(pageable);

        Page<UserResponse> response = users.map(mapper::toResponse);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/email")
    public ResponseEntity<UserResponse> findByEmail(@RequestParam String email) {

        User user = findUserByEmailUseCase.execute(email);

        return ResponseEntity.ok(mapper.toResponse(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable Long id,
                                               @Valid @RequestBody UpdateUserRequest request,
                                               Authentication authentication) {


        User targetUser = findUserByIdUseCase.execute(id);

        checkOwnerOrAdmin(targetUser, authentication);

        User user = updateUserUseCase.execute(
                id,
                request.email(),
                request.password()
        );

        return ResponseEntity.ok(mapper.toResponse(user));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        deleteUserUseCase.execute(id);

        return ResponseEntity.noContent().build();
    }
}
