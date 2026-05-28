package com.project.url_shortner.service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.google.common.hash.Hashing;
import com.project.url_shortner.constants.UrlConstants;
import com.project.url_shortner.entity.Url;
import com.project.url_shortner.model.ShortUrlRequest;
import com.project.url_shortner.model.UrlMapper;
import com.project.url_shortner.repository.UrlRepository;

import tools.jackson.databind.ObjectMapper;

@Service
public class UrlService {
	private UrlRepository urlRepository;
	private final StringRedisTemplate redisTemplate;
	private static final Logger logger = LoggerFactory.getLogger(UrlService.class);

	public UrlService(UrlRepository urlRepository, StringRedisTemplate redisTemplate) {
		this.urlRepository = urlRepository;
		this.redisTemplate = redisTemplate;
	}

	public UrlMapper generateShortUrl(ShortUrlRequest url) {
		String originalUrl = url.getUrl();
		if (!originalUrl.startsWith(UrlConstants.HTTP_CONSTANT)
				&& !originalUrl.startsWith(UrlConstants.HTTPS_CONSTANT)) {
			originalUrl = UrlConstants.HTTPS_CONSTANT + originalUrl;
		}
		ObjectMapper mapper = new ObjectMapper();
		String jsonUrl;
		Optional<Url> optionalUrl = urlRepository.findByOriginalUrl(originalUrl);
		if (optionalUrl.isPresent()) {
			jsonUrl = mapper.writeValueAsString(optionalUrl.get());
			return mapper.readValue(jsonUrl, UrlMapper.class);
		}
		String shortCode = generateShortCode(originalUrl);
		Url urlEntity = new Url();
		urlEntity.setShortUrl(shortCode);
		urlEntity.setOriginalUrl(originalUrl);
		urlEntity.setCreatedAt(LocalDateTime.now());
		urlEntity.setExpiresAt(LocalDateTime.now().plusDays(1));
		urlRepository.save(urlEntity);
		redisTemplate.opsForValue().set(shortCode, originalUrl, Duration.ofDays(1));
		jsonUrl = mapper.writeValueAsString(urlEntity);
		return mapper.readValue(jsonUrl, UrlMapper.class);
	}

	private String generateShortCode(String url) {
		return Hashing.murmur3_32_fixed().hashString(url, StandardCharsets.UTF_8).toString();
	}

	public String getOriginalCode(String shortUrl) {
		String shortCode = shortUrl.substring(shortUrl.lastIndexOf(UrlConstants.FORWARD_SLASH) + 1);
		String cachedUrl = redisTemplate.opsForValue().get(shortCode);

		if (null != redisTemplate.opsForValue().get(shortUrl)) {
			logger.info("Getting data from cache ");
			return cachedUrl.toString();
		}
		Optional<Url> originalCode = urlRepository.findByShortUrl(shortCode);
		if (originalCode.isPresent()) {
			return originalCode.get().getOriginalUrl();
		}
		return ResponseEntity.notFound().toString();
	}
}
