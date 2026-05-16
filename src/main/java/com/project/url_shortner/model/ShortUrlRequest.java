package com.project.url_shortner.model;


import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ShortUrlRequest {
    private String url;
}
