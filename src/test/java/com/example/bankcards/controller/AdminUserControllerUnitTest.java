package com.example.bankcards.controller;


import com.example.bankcards.dto.userdto.CreateUserRequest;
import com.example.bankcards.dto.userdto.UserResponse;
import com.example.bankcards.entity.enums.Role;
import com.example.bankcards.security.CustomUserDetailsService;
import com.example.bankcards.security.jwt.JwtService;
import com.example.bankcards.service.Impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import java.time.LocalDateTime;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@WebMvcTest(AdminUserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AdminUserControllerUnitTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockitoBean
    UserServiceImpl userService;
    @MockitoBean
    JwtService jwtService;
    @MockitoBean
    CustomUserDetailsService customUserDetailsService;


    private UserResponse sampleUser() {
        UserResponse r = new UserResponse();
        r.setUsername("nikita");
        r.setRole(Role.USER);
        r.setEnabled(true);
        r.setCreatedAt(LocalDateTime.now());
        return r;
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /api/admin/users/create - 200 OK")
    void createUser() throws Exception {
        CreateUserRequest dto = new CreateUserRequest("nikita", "secret", Role.USER);
        when(userService.createUser(dto)).thenReturn("User successfully created!");

        mockMvc.perform(post("/api/admin/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "username": "nikita",
                      "password": "secret",
                      "role": "USER"
                    }
                """))
                .andExpect(status().isOk())
                .andExpect(content().string("User successfully created!"));

        verify(userService, times(1)).createUser(dto);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /api/admin/users/delete/{id} - 204 No Content")
    void deleteUser() throws Exception {
        Long userId = 10L;

        mockMvc.perform(delete("/api/admin/users/delete/{id}", userId))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(userId);
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PATCH /api/admin/users/disable/{id} - 200 OK")
    void disableUser() throws Exception {
        Long userId = 10L;
        UserResponse resp = sampleUser();
        resp.setEnabled(false);

        when(userService.disableUser(userId)).thenReturn(resp);

        mockMvc.perform(patch("/api/admin/users/disable/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("nikita"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.enabled").value(false));

        verify(userService, times(1)).disableUser(userId);
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PATCH /api/admin/users/activate/{id} - 200 OK")
    void activateUser() throws Exception {
        Long userId = 10L;
        UserResponse resp = sampleUser();
        resp.setEnabled(true);


        when(userService.activateUser(userId)).thenReturn(resp);

        mockMvc.perform(patch("/api/admin/users/activate/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("nikita"))
                .andExpect(jsonPath("$.enabled").value(true));

        verify(userService, times(1)).activateUser(userId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /api/admin/users/{username} - 200 OK")
    void getUserByUsername() throws Exception {
        UserResponse resp = sampleUser();
        when(userService.getUserByUsername("nikita")).thenReturn(resp);

        mockMvc.perform(get("/api/admin/users/{username}", "nikita"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("nikita"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.enabled").value(true));

        verify(userService, times(1)).getUserByUsername("nikita");
    }
}
