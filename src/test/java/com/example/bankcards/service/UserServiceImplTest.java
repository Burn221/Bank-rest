package com.example.bankcards.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;


import com.example.bankcards.dto.JwtDTO.JwtAuthDto;
import com.example.bankcards.dto.JwtDTO.RefreshTokenDto;
import com.example.bankcards.dto.JwtDTO.UserCredentialsDto;
import com.example.bankcards.dto.userdto.CreateUserRequest;
import com.example.bankcards.dto.userdto.UserResponse;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.Role;
import com.example.bankcards.exception.exceptions.ActivatedException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.jwt.JwtService;
import com.example.bankcards.service.Impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.naming.AuthenticationException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock private UserRepository repository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;

    @InjectMocks private UserServiceImpl service;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setId(10L);
        user.setUsername("user");
        user.setPassword_hash("$2a$10$YGgv96w024SKN.vjKyf93uVagTOdGxtP1AoAA95IS/Mk2DxLj5VgC");
        user.setRole(Role.USER);
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("signIn:корректные креды - JwtService.generateAuthToken")
    void signIn_ok() throws Exception {

        User user = new User();
        user.setId(10L);
        user.setUsername("user");
        user.setPassword_hash("$2a$10$YGgv96w024SKN.vjKyf93uVagTOdGxtP1AoAA95IS/Mk2DxLj5VgC");
        user.setRole(Role.USER);
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());

        UserCredentialsDto creds = new UserCredentialsDto();
        creds.setUsername(user.getUsername());
        creds.setPassword("user");
        when(repository.findByUsername("user")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("user", "$2a$10$YGgv96w024SKN.vjKyf93uVagTOdGxtP1AoAA95IS/Mk2DxLj5VgC")).thenReturn(true);

        JwtAuthDto tokens = new JwtAuthDto();
        when(jwtService.generateAuthToken("user")).thenReturn(tokens);

        JwtAuthDto out = service.signIn(creds);


        verify(jwtService).generateAuthToken("user");
    }

    @Test
    @DisplayName("signIn: некорректные креды - AuthenticationException")
    void signIn_badPassword() {

        User user = new User();
        user.setId(10L);
        user.setUsername("user");
        user.setPassword_hash("$2a$10$YGgv96w024SKN.vjKyf93uVagTOdGxtP1AoAA95IS/Mk2DxLj5VgC");
        user.setRole(Role.USER);
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());

        UserCredentialsDto creds = new UserCredentialsDto();
        creds.setUsername(user.getUsername());
        creds.setPassword("user");
        when(repository.findByUsername("user")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("user", "$2a$10$YGgv96w024SKN.vjKyf93uVagTOdGxtP1AoAA95IS/Mk2DxLj5VgC")).thenReturn(false);

        assertThatThrownBy(() -> service.signIn(creds))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("Username or password is incorrect");
        verify(jwtService, never()).generateAuthToken(anyString());
    }

    @Test
    @DisplayName("signIn: user not found - AuthenticationException")
    void signIn_userNotFound() {
        UserCredentialsDto creds = new UserCredentialsDto();
        creds.setUsername("user");
        creds.setPassword("user");
        when(repository.findByUsername("user")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.signIn(creds))
                .isInstanceOf(AuthenticationException.class);
        verify(jwtService, never()).generateAuthToken(anyString());
    }


    @Test
    @DisplayName("refreshToken: валидный refresh - JwtService.refreshBaseToken")
    void refreshToken_ok() throws Exception {

        User user = new User();
        user.setId(10L);
        user.setUsername("user");
        user.setPassword_hash("$2a$10$YGgv96w024SKN.vjKyf93uVagTOdGxtP1AoAA95IS/Mk2DxLj5VgC");
        user.setRole(Role.USER);
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());


        RefreshTokenDto dto = new RefreshTokenDto();
        dto.setRefreshToken("REFRESH123");

        when(jwtService.validateJwtToken("REFRESH123")).thenReturn(true);
        when(jwtService.getUsernameFromToken("REFRESH123")).thenReturn("user");
        when(repository.findByUsername("user")).thenReturn(Optional.of(user));

        JwtAuthDto newTokens = new JwtAuthDto();
        newTokens.setToken("newAccess");
        newTokens.setRefreshToken("newRefresh");
        when(jwtService.refreshBaseToken("user", "REFRESH123")).thenReturn(newTokens);

        JwtAuthDto out = service.refreshToken(dto);

        assertThat(out.getToken()).isEqualTo("newAccess");
        assertThat(out.getRefreshToken()).isEqualTo("newRefresh");
        verify(jwtService).refreshBaseToken("user", "REFRESH123");
    }

    @Test
    @DisplayName("refreshToken: невалидный refresh - AuthenticationException")
    void refreshToken_invalid() {
        RefreshTokenDto dto = new RefreshTokenDto();
        dto.setRefreshToken("wrong");
        when(jwtService.validateJwtToken("wrong")).thenReturn(false);

        assertThatThrownBy(() -> service.refreshToken(dto))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("Invalid refresh token");
        verify(jwtService, never()).refreshBaseToken(anyString(), anyString());
    }

    @Test
    @DisplayName("refreshToken: user из токена не найден - UsernameNotFoundException")
    void refreshToken_userNotFound() {
        RefreshTokenDto dto = new RefreshTokenDto();
        dto.setRefreshToken("REFRESH123");
        when(jwtService.validateJwtToken("REFRESH123")).thenReturn(true);
        when(jwtService.getUsernameFromToken("REFRESH123")).thenReturn("user");
        when(repository.findByUsername("user")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.refreshToken(dto))
                .isInstanceOf(UsernameNotFoundException.class);
        verify(jwtService, never()).refreshBaseToken(anyString(), anyString());
    }


    @Test
    @DisplayName("createUser: шифрует пароль и сохраняет пользователя")
    void createUser_ok() {
        CreateUserRequest req = new CreateUserRequest("user", "user", Role.USER);
        when(passwordEncoder.encode("user")).thenReturn("$2a$10$YGgv96w024SKN.vjKyf93uVagTOdGxtP1AoAA95IS/Mk2DxLj5VgC");
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        String result = service.createUser(req);

        assertThat(result).contains("created");
        verify(passwordEncoder).encode("user");
        verify(repository).save(captor.capture());

        User saved = captor.getValue();
        assertThat(saved.getUsername()).isEqualTo("user");
        assertThat(saved.getPassword_hash()).isEqualTo("$2a$10$YGgv96w024SKN.vjKyf93uVagTOdGxtP1AoAA95IS/Mk2DxLj5VgC");
        assertThat(saved.getRole()).isEqualTo(Role.USER);
        assertThat(saved.isEnabled()).isTrue();
        assertThat(saved.getCreatedAt()).isNotNull();
    }


    @Test
    @DisplayName("deleteUser:пользователь disabled удаляет")
    void deleteUser_ok() {

        User user = new User();
        user.setId(10L);
        user.setUsername("user");
        user.setPassword_hash("$2a$10$YGgv96w024SKN.vjKyf93uVagTOdGxtP1AoAA95IS/Mk2DxLj5VgC");
        user.setRole(Role.USER);
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());

        user.setEnabled(false);
        when(repository.findById(10L)).thenReturn(Optional.of(user));

        service.deleteUser(10L);

        verify(repository).findById(10L);
        verify(repository).deleteById(10L);
    }


    @Test
    @DisplayName("deleteUser: enabled=true -ActivatedException (нельзя удалять активного)")
    void deleteUser_enabled_forbidden() {

        User user = new User();
        user.setId(10L);
        user.setUsername("user");
        user.setPassword_hash("$2a$10$YGgv96w024SKN.vjKyf93uVagTOdGxtP1AoAA95IS/Mk2DxLj5VgC");
        user.setRole(Role.USER);
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());

        user.setEnabled(true);
        when(repository.findById(10L)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> service.deleteUser(10L))
                .isInstanceOf(ActivatedException.class);

        verify(repository, never()).deleteById(anyLong());
    }


    @Test
    @DisplayName("getUserByUsername: собирает UserResponse")
    void getUserByUsername_ok() {

        User user = new User();
        user.setId(10L);
        user.setUsername("user");
        user.setPassword_hash("$2a$10$YGgv96w024SKN.vjKyf93uVagTOdGxtP1AoAA95IS/Mk2DxLj5VgC");
        user.setRole(Role.USER);
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());

        when(repository.findByUsername("user")).thenReturn(Optional.of(user));

        UserResponse r = service.getUserByUsername("user");

        assertThat(r.getUsername()).isEqualTo("user");
        assertThat(r.getRole()).isEqualTo(Role.USER);
        assertThat(r.isEnabled()).isTrue();
        assertThat(r.getCreatedAt()).isNotNull();
    }


    @Test
    @DisplayName("disableUser: ok — ставит enabled=false и сохраняет")
    void disableUser_ok() {

        User user = new User();
        user.setId(10L);
        user.setUsername("user");
        user.setPassword_hash("$2a$10$YGgv96w024SKN.vjKyf93uVagTOdGxtP1AoAA95IS/Mk2DxLj5VgC");
        user.setRole(Role.USER);
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());

        when(repository.findById(10L)).thenReturn(Optional.of(user));

        UserResponse r = service.disableUser(10L);

        assertThat(r.isEnabled()).isFalse();
        verify(repository).save(any(User.class));
    }

    @Test
    @DisplayName("activateUser: ok — ставит enabled=true и сохраняет")
    void activateUser_ok() {

        User user = new User();
        user.setId(10L);
        user.setUsername("user");
        user.setPassword_hash("$2a$10$YGgv96w024SKN.vjKyf93uVagTOdGxtP1AoAA95IS/Mk2DxLj5VgC");
        user.setRole(Role.USER);
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());

        user.setEnabled(false);
        when(repository.findById(10L)).thenReturn(Optional.of(user));

        UserResponse r = service.activateUser(10L);

        assertThat(r.isEnabled()).isTrue();
        verify(repository).save(any(User.class));
    }


}
