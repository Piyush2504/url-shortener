package com.project.url_shortner.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UrlDto{
    private String  originalUrl;
    private String shortUrl;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
}
