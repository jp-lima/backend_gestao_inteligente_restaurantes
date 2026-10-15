package com.app.restaurante.configuration;

import com.app.restaurante.dto.UserCreateRequestDTO;
import com.app.restaurante.entity.UserEntity;
import com.app.restaurante.repository.UserRepository;
import com.app.restaurante.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


@Component
public class AdminSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminEmail;
    private final String adminPassword;

    public AdminSeeder(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       @Value("${admin.email:}") String adminEmail,
                       @Value("${admin.password:}") String adminPassword) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (adminEmail.isBlank() || adminPassword.isBlank()) {
            return;
        }
        userRepository.findByEmail(adminEmail).ifPresentOrElse(
                existing -> {
                    if (!"ADMIN".equals(JwtService.normalizeRole(existing.getRole()))) {
                        existing.setRole("ADMIN");
                        userRepository.save(existing);
                    }
                },
                () -> {
                    UserEntity admin = new UserEntity();
                    admin.setName("Administrador");
                    admin.setCpf("00000000000");
                    admin.setPassword(passwordEncoder.encode(adminPassword));
                    admin.setBirth_date(java.time.LocalDate.of(2000, 1, 1));
                    admin.setGender("N");
                    admin.setEmail(adminEmail);
                    admin.setRole("ADMIN");
                    userRepository.save(admin);
                });
    }
}
