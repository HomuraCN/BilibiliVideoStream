package com.example.bilibilivideostream.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
    private CorsConfiguration buildConfig() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOriginPattern("*"); //允许任何域名
        corsConfiguration.addAllowedHeader("*"); //允许任何请求头
        corsConfiguration.addAllowedMethod("*"); //允许任何方法
        corsConfiguration.setMaxAge(3600L); //预检请求的有效期，单位为秒。
        corsConfiguration.setAllowCredentials(true); //是否支持安全证书(必需参数)
        return corsConfiguration;
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", buildConfig());
        return new CorsFilter(source);
    }
}
