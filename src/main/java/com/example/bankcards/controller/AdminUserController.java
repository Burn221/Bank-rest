package com.example.bankcards.controller;

import com.example.bankcards.dto.userdto.CreateUserRequest;
import com.example.bankcards.dto.userdto.UserResponse;
import com.example.bankcards.service.Impl.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@Validated
@RequestMapping("/api/admin/users")
@Tag(name="Admin User Controller", description = "Controller for admin user management")
public class AdminUserController {

    private UserServiceImpl userService;

    @Operation(summary = "Create user")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/create")
    public String createUser(@Valid @RequestBody CreateUserRequest request){

        return userService.createUser(request);

    }

    @Operation(summary = "Delete user", description = "User must be disabled before deleting")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId){
        userService.deleteUser(userId);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Disable user")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PatchMapping("/disable/{userId}")
    public ResponseEntity<UserResponse> disableUser(@PathVariable Long userId){

        return ResponseEntity.ok(userService.disableUser(userId));
    }


    @Operation(summary = "Activate user")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PatchMapping("/activate/{userId}")
    public ResponseEntity<UserResponse> activateUser(@PathVariable Long userId){

        return ResponseEntity.ok(userService.activateUser(userId));
    }


    @Operation(summary = "Find user by username")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/{username}")
    public ResponseEntity<UserResponse> findUserByUsername(@PathVariable String username){
        UserResponse response= userService.getUserByUsername(username);

        return ResponseEntity.ok(response);
    }
}
