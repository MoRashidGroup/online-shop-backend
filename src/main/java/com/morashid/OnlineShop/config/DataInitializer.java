package com.morashid.OnlineShop.config;

import com.morashid.OnlineShop.user.entity.Role;
import com.morashid.OnlineShop.user.entity.User;
import com.morashid.OnlineShop.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * DataInitializer - inaunda data ya msingi wakati app inaanza.
 * 
 * CommandLineRunner - ina-run mara moja baada ya Spring Boot kuanza.
 * 
 * Kazi yetu:
 *   - Kuunda ADMIN wa kwanza kama hayupo
 *   - Kuunda SELLER wa demo kama hayupo (kwa testing)
 *   - Kuunda BUYER wa demo kama hayupo (kwa testing)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        createDefaultAdmin();
        createDefaultSeller();
    }

    /**
     * Unda Admin wa kwanza.
     * Email: admin@shop.com
     * Password: admin123
     */
    private void createDefaultAdmin() {
        String email = "admin@shoop.com";

        if (userRepository.existsByEmail(email)) {
            log.info("✓ Admin already exists: {}", email);
            return;
        }

        User admin = User.builder()
                .fullName("Admin")
                .email(email)
                .password(passwordEncoder.encode("admin123"))
                .phoneNo("0772413150")
                .role(Role.ADMIN)
                .enabled(true)
                .build();

        userRepository.save(admin);

        log.info("========================================");
        log.info("✓ DEFAULT ADMIN CREATED");
        log.info("  Email:    {}", email);
        log.info("  Password: admin123");
        log.info("========================================");
    }

    /**
     * Unda Seller wa demo.
     */
    private void createDefaultSeller() {
        String email = "seller@shop.com";

        if (userRepository.existsByEmail(email)) {
            log.info("✓ Seller already exists: {}", email);
            return;
        }

        User seller = User.builder()
                .fullName("Seller")
                .email(email)
                .password(passwordEncoder.encode("seller123"))
                .phoneNo("0700000002")
                .role(Role.SELLER)
                .enabled(true)
                .build();

        userRepository.save(seller);

        log.info("========================================");
        log.info("✓ DEFAULT SELLER CREATED");
        log.info("  Email:    {}", email);
        log.info("  Password: seller123");
        log.info("========================================");
    }

    
}