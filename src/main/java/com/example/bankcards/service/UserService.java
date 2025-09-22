package com.example.bankcards.service;

import com.example.bankcards.dto.JwtDTO.JwtAuthDto;
import com.example.bankcards.dto.JwtDTO.RefreshTokenDto;
import com.example.bankcards.dto.JwtDTO.UserCredentialsDto;
import com.example.bankcards.dto.userdto.CreateUserRequest;
import com.example.bankcards.dto.userdto.UserResponse;
import com.example.bankcards.entity.User;

import javax.naming.AuthenticationException;

public interface UserService {


    JwtAuthDto signIn(UserCredentialsDto dto) throws AuthenticationException;

    JwtAuthDto refreshToken(RefreshTokenDto dto) throws AuthenticationException;

    String createUser(CreateUserRequest dto);

    void deleteUser(Long userId);

    UserResponse findUserByUsername(String username);


}
