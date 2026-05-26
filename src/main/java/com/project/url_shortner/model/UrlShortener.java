package com.project.url_shortner.model;

import java.time.LocalDateTime;

public record UrlShortener(String  originalUrl, String shortUrl,LocalDateTime createdAt, LocalDateTime expiresAt) {

}
