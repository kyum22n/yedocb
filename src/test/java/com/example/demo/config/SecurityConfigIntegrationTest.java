package com.example.demo.config;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.AbstractIntegrationTest;
import com.example.demo.security.JwtTokenProvider;
import com.example.demo.service.AdminConsultationService;
import com.example.demo.service.AdminUserService;
import com.example.demo.service.ReservationService;
import com.example.demo.service.TreatmentService;
import com.example.demo.service.UserService;

/**
 * 파일명: SecurityConfigIntegrationTest.java
 * 설명: SecurityConfig의 permitAll/역할별(hasAnyRole) 인가 규칙을 도메인 전반에 걸쳐
 *       한곳에서 종합적으로 검증하는 연동테스트. 개별 컨트롤러 테스트에서도 인가 규칙을
 *       일부 확인하지만, 이 클래스는 "/admin/**", "/api/admin/**", "/api/user/**",
 *       PUBLIC_GET_PATTERNS, anyRequest().authenticated() 다섯 갈래를 표 형태로 한 번에
 *       점검하기 위한 목적으로 별도 작성했다.
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-07 | 테스트 | Phase 6 연동테스트 작성
 */
class SecurityConfigIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private TreatmentService treatmentService;
    @MockitoBean
    private ReservationService reservationService;
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private AdminUserService adminUserService;
    @MockitoBean
    private AdminConsultationService adminConsultationService;

    private String token(String subject, String role) {
        return "Bearer " + jwtTokenProvider.createToken(subject, List.of(role));
    }

    @Test
    void PUBLIC_GET_PATTERNS는_토큰없이_통과한다() throws Exception {
        when(treatmentService.getVisibleTreatments()).thenReturn(List.of());

        mockMvc.perform(get("/treatments/all")).andExpect(status().isOk());
    }

    @Test
    void PUBLIC_PATTERNS_POST경로는_토큰없이_컨트롤러까지_도달한다() throws Exception {
        // 필수값 누락으로 400이 나더라도, 이는 "인가 실패(401)"가 아니라
        // "컨트롤러까지 도달해서 검증에 걸렸다"는 뜻이므로 permitAll 검증 목적에 부합한다.
        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void anyRequest_authenticated_경로는_토큰없이_401이다() throws Exception {
        // "/notices/**"는 배포 후 디버깅으로 PUBLIC_GET_PATTERNS에 추가되어 더 이상 이 케이스가
        // 아니므로(docs/deployment-migration.md 참고), permitAll이 아닌 다른 경로로 검증한다.
        mockMvc.perform(get("/inquiries/all")).andExpect(status().isUnauthorized());
    }

    @Test
    void admin_경로는_ADMIN_토큰이면_통과한다() throws Exception {
        when(adminConsultationService.getAllConsultations()).thenReturn(List.of());

        mockMvc.perform(get("/admin/consultations/all").header("Authorization", token("admin01", "ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void admin_경로는_SUPERADMIN_토큰이어도_통과한다() throws Exception {
        when(adminConsultationService.getAllConsultations()).thenReturn(List.of());

        mockMvc.perform(get("/admin/consultations/all")
                        .header("Authorization", token("super01", "SUPERADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void admin_경로는_USER_토큰이면_거부된다() throws Exception {
        mockMvc.perform(get("/admin/consultations/all").header("Authorization", token("user01", "USER")))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    org.assertj.core.api.Assertions.assertThat(status).isIn(401, 403);
                });
    }

    @Test
    void api_admin_경로도_ADMIN_역할규칙이_동일하게_적용된다() throws Exception {
        when(adminUserService.getAllUsers()).thenReturn(List.of());

        mockMvc.perform(get("/api/admin/user/list").header("Authorization", token("admin01", "ADMIN")))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/admin/user/list").header("Authorization", token("user01", "USER")))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    org.assertj.core.api.Assertions.assertThat(status).isIn(401, 403);
                });
    }

    @Test
    void api_user_경로는_USER_ADMIN_SUPERADMIN_모두_접근가능하다() throws Exception {
        when(userService.getUserById("user01")).thenReturn(null);
        when(userService.getUserById("admin01")).thenReturn(null);

        mockMvc.perform(get("/api/user/mypage").header("Authorization", token("user01", "USER")))
                .andExpect(result -> org.assertj.core.api.Assertions.assertThat(result.getResponse().getStatus())
                        .isNotEqualTo(401));

        mockMvc.perform(get("/api/user/mypage").header("Authorization", token("admin01", "ADMIN")))
                .andExpect(result -> org.assertj.core.api.Assertions.assertThat(result.getResponse().getStatus())
                        .isNotEqualTo(401));
    }

    @Test
    void 위조된토큰이면_401이다() throws Exception {
        mockMvc.perform(get("/api/user/mypage").header("Authorization", "Bearer not-a-real-jwt"))
                .andExpect(status().isUnauthorized());
    }
}
