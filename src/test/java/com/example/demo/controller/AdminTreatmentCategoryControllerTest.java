package com.example.demo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.AbstractIntegrationTest;
import com.example.demo.dto.request.treatment.CategoryCreateRequestDto;
import com.example.demo.dto.response.treatment.CategoryResponseDto;
import com.example.demo.entity.TreatmentCategory;
import com.example.demo.security.JwtTokenProvider;
import com.example.demo.service.AdminTreatmentCategoryService;

import tools.jackson.databind.ObjectMapper;

/**
 * 파일명: AdminTreatmentCategoryControllerTest.java
 * 설명: 관리자 진료항목 카테고리(AdminTreatmentCategoryController) MockMvc 테스트.
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-07 | 테스트 | Phase 6 연동테스트 작성
 */
class AdminTreatmentCategoryControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private AdminTreatmentCategoryService categoryService;

    private String adminToken() {
        return "Bearer " + jwtTokenProvider.createToken("admin01", List.of("ADMIN"));
    }

    @Test
    void 인증없이_카테고리목록조회하면_401() throws Exception {
        mockMvc.perform(get("/admin/treatment-categories/all"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void ADMIN권한으로_카테고리목록조회하면_200() throws Exception {
        TreatmentCategory category = new TreatmentCategory();
        category.setCategoryId(1);
        category.setCategoryName("피부");
        category.setIsVisible(true);
        when(categoryService.getAllCategories()).thenReturn(List.of(CategoryResponseDto.from(category)));

        mockMvc.perform(get("/admin/treatment-categories/all").header("Authorization", adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categoryName").value("피부"));
    }

    @Test
    void 카테고리등록_이름누락시_400() throws Exception {
        CategoryCreateRequestDto request = new CategoryCreateRequestDto();
        // categoryName 누락

        mockMvc.perform(post("/admin/treatment-categories/register")
                        .header("Authorization", adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 카테고리등록_성공하면_200() throws Exception {
        CategoryCreateRequestDto request = new CategoryCreateRequestDto();
        request.setCategoryName("성형");
        request.setIsVisible(true);

        when(categoryService.createCategory(any())).thenReturn(1);

        mockMvc.perform(post("/admin/treatment-categories/register")
                        .header("Authorization", adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(1));
    }

    @Test
    void 카테고리삭제_성공하면_200() throws Exception {
        when(categoryService.removeCategory(3)).thenReturn(1);

        mockMvc.perform(delete("/admin/treatment-categories/delete/3").header("Authorization", adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(1));
    }
}
