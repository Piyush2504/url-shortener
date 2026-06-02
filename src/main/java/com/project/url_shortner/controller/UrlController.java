package com.project.url_shortner.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.project.url_shortner.dto.UrlDto;
import com.project.url_shortner.model.ShortUrlRequest;
import com.project.url_shortner.service.UrlService;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class UrlController {

	private UrlService urlService;

	@Autowired
	public UrlController(UrlService urlService) {
		this.urlService = urlService;
	}

    @PostMapping(path = "/shorten")
    public ResponseEntity<UrlDto> shortenUrl(@RequestBody ShortUrlRequest url) {
        return ResponseEntity.ok().body(urlService.generateShortUrl(url));
    }

	@GetMapping(path = "/{shortUrl}")
	public ResponseEntity<?> originalUrl(@PathVariable String shortUrl, HttpServletResponse response)
			throws IOException {
		String originalUrl = urlService.getOriginalCode(shortUrl);
		if (null != originalUrl) {
			response.sendRedirect(originalUrl);
			return new ResponseEntity<>(HttpStatus.PERMANENT_REDIRECT);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
}
