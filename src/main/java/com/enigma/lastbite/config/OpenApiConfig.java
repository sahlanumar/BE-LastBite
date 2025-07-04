package com.enigma.lastbite.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI lastBiteOpenAPI() {
        // Definisikan skema keamanan 'bearerAuth'
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                // 1. Tambahkan item keamanan (Security Requirement) secara global
                // Baris ini akan memunculkan gembok pada setiap endpoint
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))

                // 2. Definisikan komponen skema keamanan
                // Baris ini akan membuat tombol "Authorize" muncul dan menjelaskan cara kerja 'bearerAuth'
                .components(
                        new Components()
                                .addSecuritySchemes(securitySchemeName,
                                        new SecurityScheme()
                                                .name(securitySchemeName)
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                                .description("Masukkan JWT Token dengan prefix 'Bearer '. Contoh: 'Bearer eyJhbGciOiJI...'")
                                )
                                // Anda masih bisa menambahkan skema lain jika perlu
                                .addSecuritySchemes("superAdminKey",
                                        new SecurityScheme()
                                                .type(SecurityScheme.Type.APIKEY)
                                                .name("superAdminKey")
                                                .in(SecurityScheme.In.HEADER)
                                                .description("Super Admin secret key")
                                )
                )
                // 3. Informasi umum API
                .info(new Info()
                        .title("LastBite API")
                        .description("API documentation for LastBite application")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Kelompok 3")
                                .email("lann@restaurant.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html"))
                );
    }
}