package com.project.url_shortner.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.project.url_shortner.dto.UrlDto;
import com.project.url_shortner.model.ShortUrlRequest;
import com.project.url_shortner.service.UrlService;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@CrossOrigin(origins = "*")
public class UrlController {

	private UrlService urlService;

	private static final Logger logger = LoggerFactory.getLogger(UrlService.class);
	@Autowired
	public UrlController(UrlService urlService) {
		this.urlService = urlService;
	}

    @PostMapping(path = "/api/shorten")
    public ResponseEntity<UrlDto> shortenUrl(@RequestBody ShortUrlRequest url) {
    	logger.info("getting short code request");
        return ResponseEntity.ok().body(urlService.generateShortUrl(url));
    }

	@GetMapping(path = "/{shortUrl:[a-zA-Z0-9]+}")
	public ResponseEntity<?> originalUrl(@PathVariable String shortUrl, HttpServletResponse response)
			throws IOException {
		String originalUrl = urlService.getOriginalCode(shortUrl);
		if (null != originalUrl) {
			logger.info("original url with short code was found in the database/cache hence redirecting...");
			response.sendRedirect(originalUrl);
			return new ResponseEntity<>(HttpStatus.PERMANENT_REDIRECT);
		}
		logger.info("original url with short code was not found in the database/cache hence throwing error 404");
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
}
