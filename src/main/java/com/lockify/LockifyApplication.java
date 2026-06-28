package com.lockify;

import com.lockify.phase2.account.config.AccountSecurityProperties;
import com.lockify.shared.config.SecurityProperties;
import com.lockify.shared.config.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Lockify Application Entry Point
 *
 * Yeh main class hai - Spring Boot yahan se start hota hai.
 * @SpringBootApplication automatically component scan karta hai
 * com.lockify package ke andar sab beans ko.
 */
@SpringBootApplication
@EnableConfigurationProperties({JwtProperties.class, SecurityProperties.class, AccountSecurityProperties.class})
public class LockifyApplication {

    public static void main(String[] args) {
        SpringApplication.run(LockifyApplication.class, args);
    }
}
