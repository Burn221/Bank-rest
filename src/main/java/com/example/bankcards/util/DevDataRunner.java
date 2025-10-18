package com.example.bankcards.util;

import com.example.bankcards.dto.CardDTO.CreateCardRequest;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.Role;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.Impl.CardServiceImpl;
import com.example.bankcards.service.Impl.UserServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.CommandLineRunner;

import java.time.LocalDateTime;

/** Класс добавляющий тестовые данные с правильно зашифроваными паролями и PAN, только для DEV */
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

        var user = userRepository.findByUsername("Nikita").orElseGet(() -> {
            var u2 = new User();
            u2.setUsername("Nikita");
            u2.setPassword_hash(passwordEncoder.encode("Nikita"));
            u2.setRole(Role.USER);
            u2.setEnabled(true);
            u2.setCreatedAt(LocalDateTime.now());
            return userRepository.save(u2);
        });

        var user2 = userRepository.findByUsername("Oleg").orElseGet(() -> {
            var u3 = new User();
            u3.setUsername("Oleg");
            u3.setPassword_hash(passwordEncoder.encode("Oleg"));
            u3.setRole(Role.USER);
            u3.setEnabled(true);
            u3.setCreatedAt(LocalDateTime.now());
            return userRepository.save(u3);
        });

        if (!cardRepository.existsByUser_Id(user.getId())) {

            var req1 = new CreateCardRequest(user.getId(), "Nikita", "KZT");
            var req2 = new CreateCardRequest(user.getId(), "Nikita", "KZT");
            var req3= new CreateCardRequest(user2.getId(), "Oleg", "KZT");

            var c1 = cardService.createCardUser(user.getId(), req1);
            var c2 = cardService.createCardUser(user.getId(), req2);
            var c3= cardService.createCardUser(user2.getId(),req3);


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

