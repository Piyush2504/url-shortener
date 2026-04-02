package com.project.url_shortner.controller;

import com.project.url_shortner.entity.Url;
import com.project.url_shortner.model.UrlMapper;
import com.project.url_shortner.request_object.ShortUrlRequest;
import com.project.url_shortner.service.UrlService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;

@Controller
@RequestMapping("/api")
public class UrlController {

    private UrlService urlService;
    @Autowired
    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping(path = "/shorten")
    public ResponseEntity<UrlMapper> shortenUrl(@RequestBody ShortUrlRequest url) {
        return ResponseEntity.ok().body(urlService.generateShortUrl(url));
    }
    @GetMapping(path = "/{shortUrl}")
    public ResponseEntity<?> originalUrl(@PathVariable String shortUrl, HttpServletResponse response) throws IOException {
        String originalUrl = urlService.getOriginalCode(shortUrl);
        if(null!=originalUrl){
            response.sendRedirect(originalUrl);
            return null;
        }
        return new ResponseEntity<>( HttpStatus.NOT_FOUND);
    }
}
