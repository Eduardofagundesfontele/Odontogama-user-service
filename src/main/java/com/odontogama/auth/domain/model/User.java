package com.odontogama.auth.domain.model;

import com.odontogama.auth.domain.exception.UserErrorCode;
import com.odontogama.auth.domain.exception.UserException;

import java.time.LocalDateTime;

public class User {

    private Long id;
    private String email;
    private String password;
    private UserRole role;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


protected User(){}

public  static User create(String email,String password,UserRole role){

 validate(email,password);

 User user = new User();
user.email = email;
user.password = password;
user.role = role;
user.active = true;
user.createdAt = LocalDateTime.now();
user.updatedAt = LocalDateTime.now();

return user;
}
    public void deactivate() {
        this.active = false;
    }
public static User reconstruct(Long id,String email,String password,UserRole role,Boolean active,LocalDateTime createdAt, LocalDateTime updatedAt){

    User user = new User();
    user.id = id;
    user.email = email;
    user.password = password;
    user.role = role;
    user.active = active;
    user.createdAt = createdAt;
    user.updatedAt = updatedAt;

    return user;
}

public void update(String email, String password){
    validate(email,password);
    this.email = email;
    this.password = password;
}

    private static void validate(String email, String password){
        if (email == null || email.isBlank()) {
            throw new UserException(UserErrorCode.INVALID_EMAIL);
        }

        if (password == null || password.isBlank()) {
            throw new UserException(UserErrorCode.INVALID_PASSWORD);
        }}

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Boolean getActive() {
        return active;
    }

    public UserRole getRole() {
        return role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }



}

