package com.project.url_shortner.request_object;


import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ShortUrlRequest {
    private String url;
}
