package com.project.url_shortner.service;

import com.project.url_shortner.entity.Url;
import com.project.url_shortner.model.UrlMapper;
import com.project.url_shortner.repository.UrlRepository;
import com.project.url_shortner.request_object.ShortUrlRequest;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class UrlService {
    private final String tempValues = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@$*";
    private ModelMapper modelMapper;
    private UrlRepository urlRepository;
    public UrlService(UrlRepository urlRepository, ModelMapper modelMapper) {
        this.urlRepository = urlRepository;
        this.modelMapper = modelMapper;
    }
    public UrlMapper generateShortUrl(ShortUrlRequest url) {
        String originalUrl = url.getUrl();
        if(!originalUrl.startsWith("http://") && !originalUrl.startsWith("https://")){
            originalUrl = "https://" + originalUrl;
        }
        Optional<Url> optionalUrl = urlRepository.findByOriginalUrl(originalUrl);
        if(optionalUrl.isPresent()){
            return modelMapper.map(optionalUrl.get(), UrlMapper.class);
        }
        String shortCode = generateShortCode(originalUrl);
        Url urlEntity = new Url();
        urlEntity.setShortUrl(shortCode);
        urlEntity.setOriginalUrl(originalUrl);
        urlEntity.setCreatedAt(LocalDateTime.now());
        urlEntity.setExpiresAt(LocalDateTime.now().plusDays(1));
        urlRepository.save(urlEntity);
        return modelMapper.map(urlEntity, UrlMapper.class);
    }
    private String generateShortCode(String url) {
        StringBuilder shortCode = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 7; i++) {
            shortCode.append(tempValues.charAt(random.nextInt(tempValues.length())));
        }
        return shortCode.toString();
    }

    public String getOriginalCode(String shortUrl) {
        String shortCode = shortUrl.substring(shortUrl.lastIndexOf('/') + 1);
        Optional<Url> originalCode = urlRepository.findByShortUrl(shortCode);
        if(originalCode.isPresent()){
            return originalCode.get().getOriginalUrl();
        }
        return ResponseEntity.notFound().toString();
    }
}
