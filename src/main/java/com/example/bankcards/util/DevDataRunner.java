package com.example.bankcards.util;

import com.example.bankcards.dto.CardDTO.CreateCardRequest;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.Role;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.Impl.CardServiceImpl;
import com.example.bankcards.service.Impl.UserServiceImpl;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.CommandLineRunner;

import java.time.LocalDateTime;
import java.time.Year;

@Component
@Profile("dev")
@AllArgsConstructor
public class DevDataRunner implements CommandLineRunner{


    private CardServiceImpl cardService;
    private UserServiceImpl userService;
    private UserRepository userRepository;
    private CardRepository cardRepository;
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        var admin = userRepository.findByUsername("admin").orElseGet(() -> {
            var u = new User();
            u.setUsername("admin");
            u.setPassword_hash(passwordEncoder.encode("admin"));
            u.setRole(Role.ADMIN);
            u.setEnabled(true);
            u.setCreatedAt(LocalDateTime.now());
            return userRepository.save(u);
        });

        var user = userRepository.findByUsername("user").orElseGet(() -> {
            var u2 = new User();
            u2.setUsername("user");
            u2.setPassword_hash(passwordEncoder.encode("user"));
            u2.setRole(Role.USER);
            u2.setEnabled(true);
            u2.setCreatedAt(LocalDateTime.now());
            return userRepository.save(u2);
        });

        if (!cardRepository.existsByUser_Id(user.getId())) {

            var req1 = new CreateCardRequest(user.getId(), "Nikita", "KZT");
            var req2 = new CreateCardRequest(user.getId(), "Nikita", "KZT");

            var c1 = cardService.createCardUser(user.getId(), req1);
            var c2 = cardService.createCardUser(user.getId(), req2);


            cardRepository.findForUpdate(c1.getId()).ifPresent(card -> {
                card.setBalanceMinor(1000L);
                cardRepository.save(card);
            });
            cardRepository.findForUpdate(c2.getId()).ifPresent(card -> {
                card.setBalanceMinor(1000L);
                cardRepository.save(card);
            });
        }
    }

    }

