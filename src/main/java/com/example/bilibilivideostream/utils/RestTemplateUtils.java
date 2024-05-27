package com.example.bilibilivideostream.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;

@Component
public class RestTemplateUtils {
    @Autowired
    private RestTemplate restTemplate;
    // 可根据需求调整缓冲区大小
    private final int bufferSize = 65536;

    public <T> T getResponseEntity(String url, List<String> cookies, List<String> userAgent,
                                   List<String> referer, Class<T> responseType){
        HttpHeaders headers = new HttpHeaders();
        headers.put(HttpHeaders.COOKIE, cookies);
        headers.put(HttpHeaders.USER_AGENT, userAgent);
        headers.put(HttpHeaders.REFERER, referer);
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<T> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                httpEntity,
                responseType
        );

        return responseEntity.getBody();
    }

    public <T> T getResponseEntity(String url, HttpEntity<String> httpEntity, Class<T> responseType){
        ResponseEntity<T> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                httpEntity,
                responseType
        );

        return responseEntity.getBody();
    }

    public void downloadFileFromUrl(String url, List<String> cookies, List<String> userAgent,
                                    List<String> referer, String directoryPath, String fileName){
        HttpHeaders headers = new HttpHeaders();
        headers.put(HttpHeaders.COOKIE, cookies);
        headers.put(HttpHeaders.USER_AGENT, userAgent);
        headers.put(HttpHeaders.REFERER, referer);

        ResponseEntity<Void> fileResponse = restTemplate.execute(
                url,
                HttpMethod.GET,
                request -> {
                    // 添加请求头信息
                    headers.forEach((key, values) -> {
                        values.forEach(value -> request.getHeaders().addAll(key, Collections.singletonList(value)));
                    });
                },
                response -> {
                    try (InputStream inputStream = response.getBody();
                         OutputStream outputStream = new FileOutputStream(directoryPath + "\\" + fileName)) {
                        byte[] buffer = new byte[bufferSize]; // 缓冲区大小
                        long totalBytesRead = 0;
                        long contentLength = response.getHeaders().getContentLength();
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                            totalBytesRead += bytesRead;
                            // 计算并显示进度条
                            double progress = (double) totalBytesRead / contentLength * 100;
                            System.out.printf("\rProgress of [" + fileName + "]: %.2f%%", progress);
                            System.out.flush();
                        }
                        System.out.println();
                        return null;
                    }
                }
        );
    }

    public void downloadFileFromUrl(String url, HttpHeaders headers, String directoryPath, String fileName){
        ResponseEntity<Void> fileResponse = restTemplate.execute(
                url,
                HttpMethod.GET,
                request -> {
                    // 添加请求头信息
                    headers.forEach((key, values) -> {
                        values.forEach(value -> request.getHeaders().addAll(key, Collections.singletonList(value)));
                    });
                },
                response -> {
                    try (InputStream inputStream = response.getBody();
                         OutputStream outputStream = new FileOutputStream(directoryPath + "\\" + fileName)) {
                        byte[] buffer = new byte[bufferSize]; // 缓冲区大小
                        long totalBytesRead = 0;
                        long contentLength = response.getHeaders().getContentLength();
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                            totalBytesRead += bytesRead;
                            // 计算并显示进度条
                            double progress = (double) totalBytesRead / contentLength * 100;
                            System.out.printf("\rProgress of [" + fileName + "]: %.2f%%", progress);
                            System.out.flush();
                        }
                        System.out.println();
                        return null;
                    }
                }
        );
    }
}
