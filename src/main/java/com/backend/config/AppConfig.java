package com.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.cdimascio.dotenv.Dotenv;

@Configuration
public class AppConfig {

                @Bean
                Dotenv dotenv() {
                    return Dotenv.load();
                }
}
        