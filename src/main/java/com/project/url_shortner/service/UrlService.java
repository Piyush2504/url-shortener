package com.project.url_shortner.service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.google.common.hash.Hashing;
import com.project.url_shortner.constants.UrlConstants;
import com.project.url_shortner.dto.UrlDto;
import com.project.url_shortner.entity.Url;
import com.project.url_shortner.model.ShortUrlRequest;
import com.project.url_shortner.repository.UrlRepository;

import tools.jackson.databind.ObjectMapper;

@Service
public class UrlService {
	private UrlRepository urlRepository;
	@Autowired
	private final RedisTemplate<String,String> redisTemplate;
	private static final Logger logger = LoggerFactory.getLogger(UrlService.class);

	public UrlService(UrlRepository urlRepository, RedisTemplate<String,String> redisTemplate) {
		this.urlRepository = urlRepository;
		this.redisTemplate = redisTemplate;
	}

	public UrlDto generateShortUrl(ShortUrlRequest url) {
		String originalUrl = url.getUrl();
		if (!originalUrl.startsWith(UrlConstants.HTTP_CONSTANT)
				&& !originalUrl.startsWith(UrlConstants.HTTPS_CONSTANT)) {
			logger.info("appending HTTP/HTTPS to the originalUrl");
			originalUrl = UrlConstants.HTTPS_CONSTANT + originalUrl;
		}
		ObjectMapper mapper = new ObjectMapper();
		String cachedUrlPresent = redisTemplate.opsForValue().get(originalUrl);
		UrlDto cachedResult = new UrlDto();
		cachedResult.setOriginalUrl(originalUrl);
		cachedResult.setShortUrl(cachedUrlPresent);
		cachedResult.setCreatedAt(LocalDateTime.now());
		cachedResult.setExpiresAt(LocalDateTime.now().plusDays(1));
		if(null!=cachedUrlPresent) {
			logger.info("cache data found for the original url "+originalUrl);
			return cachedResult;
		}
		logger.info("cache data not found hence looking up to database for the short code");
		Optional<Url> shortCodeDB = urlRepository.findByOriginalUrl(url.getUrl());
		StringBuilder shortCode = new StringBuilder();
		if(shortCodeDB.isPresent()) {
			UrlDto dbUrl = new UrlDto();
			dbUrl.setOriginalUrl(url.getUrl());
			dbUrl.setShortUrl(shortCodeDB.get().getShortUrl());
			dbUrl.setCreatedAt(LocalDateTime.now());
			dbUrl.setExpiresAt(LocalDateTime.now().plusDays(1));
			shortCode.append(shortCodeDB.get().getShortUrl());
			redisTemplate.opsForValue().set(originalUrl, shortCode.toString(), Duration.ofDays(1));
			redisTemplate.opsForValue().set(shortCode.toString(), originalUrl, Duration.ofDays(1));
			return dbUrl;
		}
		String jsonUrl;
		Url urlEntity = new Url();
		shortCode.append(generateShortCode(originalUrl));
		urlEntity.setShortUrl(shortCode.toString());
		urlEntity.setOriginalUrl(originalUrl);
		urlEntity.setCreatedAt(LocalDateTime.now());
		urlEntity.setExpiresAt(LocalDateTime.now().plusDays(1));
		urlRepository.save(urlEntity);
		logger.info("maintaining dual-index in cache for both original url and short code");
		redisTemplate.opsForValue().set(originalUrl, shortCode.toString(), Duration.ofDays(1));
		redisTemplate.opsForValue().set(shortCode.toString(), originalUrl, Duration.ofDays(1));
		jsonUrl = mapper.writeValueAsString(urlEntity);
		return mapper.readValue(jsonUrl, UrlDto.class);
	}

	private String generateShortCode(String url) {
		logger.info("converting the original url to get short code");
		return Hashing.murmur3_32_fixed().hashString(url, StandardCharsets.UTF_8).toString();
	}

	public String getOriginalCode(String shortUrl) {
		String shortCode = shortUrl.substring(shortUrl.lastIndexOf(UrlConstants.FORWARD_SLASH) + 1);
		String cachedUrl = redisTemplate.opsForValue().get(shortCode).toString();

		if (null != redisTemplate.opsForValue().get(shortUrl)) {
			logger.info("cache data was present in the redis");
			return cachedUrl;
		}
		logger.info("key not found in cache hence looking up to database for the value");
		Optional<Url> originalCode = urlRepository.findByShortUrl(shortCode);
		if (originalCode.isPresent()) {
			return originalCode.get().getOriginalUrl();
		}
		return ResponseEntity.notFound().toString();
	}
}
