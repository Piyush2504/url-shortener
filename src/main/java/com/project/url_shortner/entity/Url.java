package com.project.url_shortner.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name="urls")
public class Url {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String originalUrl;

    @Column(nullable = false,unique = true)
    private String shortUrl;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
}
