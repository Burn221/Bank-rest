package com.example.bankcards.service.Impl;

import com.example.bankcards.dto.JwtDTO.JwtAuthDto;
import com.example.bankcards.dto.JwtDTO.RefreshTokenDto;
import com.example.bankcards.dto.JwtDTO.UserCredentialsDto;
import com.example.bankcards.dto.userdto.CreateUserRequest;
import com.example.bankcards.dto.userdto.UserResponse;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.ActivatedException;
import com.example.bankcards.exception.DisabledException;
import com.example.bankcards.exception.ForbiddenTransactionException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.jwt.JwtService;
import com.example.bankcards.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.AuthenticationException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepository repository;

    private PasswordEncoder passwordEncoder;

    private JwtService jwtService;

    @Transactional
    @Override
    public JwtAuthDto signIn(UserCredentialsDto dto) throws AuthenticationException{
        User user= findByCredentials(dto);

        return jwtService.generateAuthToken(user.getUsername());
    }

    @Transactional
    @Override
    public JwtAuthDto refreshToken(RefreshTokenDto dto) throws AuthenticationException {
        String refreshToken= dto.getRefreshToken();

        if(refreshToken!=null && jwtService.validateJwtToken(refreshToken)){
            User user=repository.findByUsername(jwtService.getUsernameFromToken(refreshToken))
                    .orElseThrow(()-> new UsernameNotFoundException("User not found"));

            return jwtService.refreshBaseToken(user.getUsername(), refreshToken);
        }

        throw new AuthenticationException("Invalid refresh token");

    }

    @Transactional
    @Override
    public String createUser(CreateUserRequest dto) {
        User user= new User();
        user.setUsername(dto.username());
        user.setPassword_hash(passwordEncoder.encode(dto.password()));
        user.setRole(dto.role());
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());

        repository.save(user);

        return "User successfully created!";
    }

    @Transactional
    @Override
    public void deleteUser(Long userId) {
        User user= repository.findById(userId).orElseThrow(()-> new UsernameNotFoundException("User not found"));
        if (user.isEnabled()) throw new ActivatedException("User has to be disabled");
        repository.deleteById(userId);

    }

    @Override
    public UserResponse getUserByUsername(String username) {
        User user= repository.findByUsername(username)
                .orElseThrow(()->new UsernameNotFoundException("User not found"));

        UserResponse response= new UserResponse();
        response.setUsername(user.getUsername());
        response.setRole(user.getRole());
        response.setEnabled(user.isEnabled());
        response.setCreatedAt(user.getCreatedAt());

        return response;


    }



    private User findByCredentials(UserCredentialsDto dto) throws AuthenticationException{
        Optional<User> optionalUser= repository.findByUsername(dto.getUsername());

        if(optionalUser.isPresent()){
            User user= optionalUser.get();

            if(passwordEncoder.matches(dto.getPassword(), user.getPassword_hash()))
                return user;
        }

        throw new AuthenticationException("Username or password is incorrect");

    }

    @Transactional
    @Override
    public UserResponse disableUser(Long userId) {
        User user= repository.findById(userId)
                .orElseThrow(()-> new UsernameNotFoundException("Username not found"));

        user.setEnabled(false);

        repository.save(user);

        UserResponse response= new UserResponse();
        response.setUsername(user.getUsername());
        response.setRole(user.getRole());
        response.setEnabled(user.isEnabled());
        response.setCreatedAt(user.getCreatedAt());

        return response;
    }

    @Transactional
    @Override
    public UserResponse activateUser(Long userId) {

        User user= repository.findById(userId)
                .orElseThrow(()-> new UsernameNotFoundException("Username not found"));

        user.setEnabled(true);

        repository.save(user);

        UserResponse response= new UserResponse();
        response.setUsername(user.getUsername());
        response.setRole(user.getRole());
        response.setEnabled(user.isEnabled());
        response.setCreatedAt(user.getCreatedAt());

        return response;
    }


}
