package com.example.demo.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.AbstractIntegrationTest;
import com.example.demo.dto.response.treatment.CategoryResponseDto;
import com.example.demo.entity.TreatmentCategory;
import com.example.demo.service.TreatmentCategoryService;

/**
 * 파일명: TreatmentCategoryControllerTest.java
 * 설명: 사용자용 진료항목 카테고리(TreatmentCategoryController) MockMvc 테스트. GET은
 *       SecurityPaths.PUBLIC_GET_PATTERNS("/treatment-categories/**")에 포함되어 인증 없이도
 *       접근 가능하다.
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-07 | 테스트 | Phase 6 연동테스트 작성
 */
class TreatmentCategoryControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TreatmentCategoryService categoryService;

    @Test
    void 노출카테고리목록조회는_인증없이도_200() throws Exception {
        TreatmentCategory category = new TreatmentCategory();
        category.setCategoryId(1);
        category.setCategoryName("피부");
        category.setIsVisible(true);
        when(categoryService.getAllVisibleCategories())
                .thenReturn(List.of(CategoryResponseDto.from(category)));

        mockMvc.perform(get("/treatment-categories/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categoryName").value("피부"));
    }
}
