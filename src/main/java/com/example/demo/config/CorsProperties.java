package com.example.demo.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * 파일명: CorsProperties.java
 * 설명: CORS 허용 오리진을 설정 파일(application*.properties)에서 주입받기 위한 프로퍼티 클래스.
 *       B는 SecurityConfig 내부에 오리진을 하드코딩하고 있었으나, 여기서는 설정으로 외부화한다.
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-06 | 리팩토링 | 최초 생성
 */
@Component
@ConfigurationProperties(prefix = "cors")
@Data
public class CorsProperties {
    private List<String> allowedOrigins = List.of("http://localhost:5173");
}
