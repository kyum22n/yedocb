package com.example.demo.controller;

import java.util.Collections;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.demo.dao.UserDao;
import com.example.demo.dto.response.auth.TokenResponseDto;
import com.example.demo.entity.User;
import com.example.demo.security.JwtTokenProvider;

/**
 * 파일명: OAuthController.java
 * 설명: Google + Kakao OAuth2 로그인. com.example.yedocb(B)의 구조를 이식하면서
 *       이메일 검증 여부 체크를 추가했다 (인증 최소수정 1번째 항목):
 *         - Google: userinfo 응답의 email_verified가 true가 아니면 401
 *         - Kakao: kakao_account.is_email_verified가 true가 아니면 401
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-06 | 리팩토링 | 최초 생성 (email_verified / is_email_verified 체크 추가)
 */
@RestController
@RequestMapping("/api/oauth2")
public class OAuthController {

    private static final Logger log = LoggerFactory.getLogger(OAuthController.class);

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserDao userDao;

    @Value("${google.client.id:}")
    private String googleClientId;

    @Value("${google.client.secret:}")
    private String googleClientSecret;

    @Value("${google.redirect.uri:}")
    private String googleRedirectUri;

    @Value("${kakao.client.id:}")
    private String kakaoClientId;

    @Value("${kakao.redirect.uri:}")
    private String kakaoRedirectUri;

    @PostMapping("/google")
    public ResponseEntity<?> handleGoogleLogin(@RequestParam("code") String code) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String tokenUri = "https://oauth2.googleapis.com/token";

            MultiValueMap<String, String> tokenParams = new LinkedMultiValueMap<>();
            tokenParams.add("code", code);
            tokenParams.add("client_id", googleClientId);
            tokenParams.add("client_secret", googleClientSecret);
            tokenParams.add("redirect_uri", googleRedirectUri);
            tokenParams.add("grant_type", "authorization_code");

            HttpHeaders tokenHeaders = new HttpHeaders();
            tokenHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(tokenParams, tokenHeaders);
            ResponseEntity<Map> tokenResponse = restTemplate.exchange(tokenUri, HttpMethod.POST, tokenRequest, Map.class);

            String accessToken = (String) tokenResponse.getBody().get("access_token");

            HttpHeaders userHeaders = new HttpHeaders();
            userHeaders.setBearerAuth(accessToken);
            HttpEntity<?> userRequest = new HttpEntity<>(userHeaders);

            ResponseEntity<Map> userResponse = restTemplate.exchange(
                "https://www.googleapis.com/oauth2/v3/userinfo", HttpMethod.GET, userRequest, Map.class
            );

            Map userInfo = userResponse.getBody();
            String email = ((String) userInfo.get("email")).trim();

            // 인증 최소수정 1번째 항목: Google email_verified 체크
            Boolean emailVerified = (Boolean) userInfo.get("email_verified");
            if (emailVerified == null || !emailVerified) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Google 계정의 이메일이 인증되지 않았습니다.");
            }

            return handleOAuthLogin(email);

        } catch (Exception e) {
            log.error("Google OAuth 로그인 처리 실패", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/kakao")
    public ResponseEntity<?> handleKakaoLogin(@RequestParam("code") String code) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String tokenUri = "https://kauth.kakao.com/oauth/token";

            MultiValueMap<String, String> tokenParams = new LinkedMultiValueMap<>();
            tokenParams.add("grant_type", "authorization_code");
            tokenParams.add("client_id", kakaoClientId);
            tokenParams.add("redirect_uri", kakaoRedirectUri);
            tokenParams.add("code", code);

            HttpHeaders tokenHeaders = new HttpHeaders();
            tokenHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(tokenParams, tokenHeaders);
            ResponseEntity<Map> tokenResponse = restTemplate.exchange(tokenUri, HttpMethod.POST, tokenRequest, Map.class);

            String accessToken = (String) tokenResponse.getBody().get("access_token");

            HttpHeaders userHeaders = new HttpHeaders();
            userHeaders.setBearerAuth(accessToken);
            HttpEntity<?> userRequest = new HttpEntity<>(userHeaders);

            ResponseEntity<Map> userResponse = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me", HttpMethod.GET, userRequest, Map.class
            );

            Map kakaoUser = userResponse.getBody();
            Map kakaoAccount = (Map) kakaoUser.get("kakao_account");
            String email = ((String) kakaoAccount.get("email")).trim();

            // 인증 최소수정 1번째 항목: Kakao is_email_verified 체크
            Boolean emailVerified = (Boolean) kakaoAccount.get("is_email_verified");
            if (emailVerified == null || !emailVerified) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Kakao 계정의 이메일이 인증되지 않았습니다.");
            }

            return handleOAuthLogin(email);

        } catch (Exception e) {
            log.error("Kakao OAuth 로그인 처리 실패", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    private ResponseEntity<?> handleOAuthLogin(String email) {
        User user = userDao.selectUserByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("해당 이메일로 가입된 사용자가 없습니다. 회원가입이 필요합니다.");
        }

        String jwtToken = jwtTokenProvider.createToken(user.getUId(), Collections.singletonList("USER"));
        return ResponseEntity.ok(TokenResponseDto.ofAccessOnly(jwtToken, user.getUId()));
    }
}
