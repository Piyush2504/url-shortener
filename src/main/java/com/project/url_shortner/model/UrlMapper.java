package com.project.url_shortner.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UrlMapper {
    private String  originalUrl;
    private String shortUrl;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
}
