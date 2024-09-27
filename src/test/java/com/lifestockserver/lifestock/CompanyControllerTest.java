package com.lifestockserver.lifestock;

import com.lifestockserver.lifestock.auth.service.TokenServiceImpl;
import com.lifestockserver.lifestock.company.controller.CompanyController;
import com.lifestockserver.lifestock.company.domain.enums.CompanyLeastOperatePeriod;
import com.lifestockserver.lifestock.company.domain.enums.CompanyLevel;
import com.lifestockserver.lifestock.company.dto.CompanyResponseDto;
import com.lifestockserver.lifestock.company.service.CompanyService;
import com.lifestockserver.lifestock.file.dto.FileResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;



@WebMvcTest(CompanyController.class)
@Import(TestSecurityConfig.class)
public class CompanyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    //CompanyController에서 잡고 있는 Bean 객체에 대해 Mock 형태의 객체 생성
    @MockBean
    private CompanyService companyService;

    @MockBean
    private TokenServiceImpl tokenService;

    private FileResponseDto mockLogo;

    @Test
    @DisplayName("Company 데이터 생성 테스트")
    @WithMockUser(username = "user1", roles = {"USER"})
    void testCreateCompany() throws Exception{
        Long companyId = 1L;

        CompanyResponseDto mockResponse = CompanyResponseDto.builder()
                .userId(1L)
                .id(companyId)
                .name("Company Name")
                .description("Company Description")
                .level(CompanyLevel.LOW)
                .leastOperatePeriod(CompanyLeastOperatePeriod.ONE_MONTH)
                .listedDate(LocalDate.now())
                .investmentAmount(1000000L)
                .initialStockPrice(1000L)
                .initialStockQuantity(100L)
                .logo(null)
                .currentStockPrice(12000L)
                .build();

        when(companyService.createCompany(any())).thenReturn(mockResponse);

        // JSON 요청 본문 생성 (logo 필드 제외)
        String jsonRequest = "{ \"userId\": 1, \"name\": \"Company Name\", \"description\": \"Company Description\", " +
                "\"level\": \"LOW\", \"leastOperatePeriod\": \"ONE_MONTH\", " +
                "\"listedDate\": \"" + LocalDate.now() + "\", \"investmentAmount\": 1000000, " +
                "\"initialStockPrice\": 1000, \"initialStockQuantity\": 100 }";

        // MockMvc를 통해 multipart POST 요청을 보내고 응답을 검증 (파일 업로드 없음)
        mockMvc.perform(post("/company")
                        .content(jsonRequest)
                        .with(csrf()) // CSRF 토큰 추가
                        .with(httpBasic("user1", "password1"))
                        .contentType(MediaType.APPLICATION_JSON)) // JSON 데이터로 전송
                .andDo(print()) // 요청과 응답을 출력
                .andExpect(status().isOk()) // 상태 코드가 200인지 확인
                .andExpect(jsonPath("$.name").value("Company Name")) // 응답 본문에서 name 필드 확인
                .andExpect(jsonPath("$.description").value("Company Description")); // 응답 본문에서 description 필드 확인

        verify(companyService, times(1)).createCompany(any());
    }

    @Test
    @DisplayName("Company 정보 조회 테스트")
    @WithMockUser(username = "user1", roles = {"USER"})
    void testGetCompany() throws Exception{
        Long companyId = 1L;

        CompanyResponseDto mockResponse = CompanyResponseDto.builder()
                .userId(1L)
                .id(companyId)
                .name("Company Name")
                .description("Company Description")
                .level(CompanyLevel.LOW)
                .leastOperatePeriod(CompanyLeastOperatePeriod.ONE_MONTH)
                .listedDate(LocalDate.now())
                .investmentAmount(1000000L)
                .initialStockPrice(1000L)
                .initialStockQuantity(100L)
                .logo(null)
                .currentStockPrice(12000L)
                .build();

        //Service 레이어가 반환할 데이터 설정
        when(companyService.findById(companyId)).thenReturn(mockResponse);

        mockMvc.perform(get("/company/{companyId}", companyId)
                        .with(csrf())
                        .with(httpBasic("user1", "password1")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(companyId))
                .andExpect(jsonPath("$.name").value("Company Name"))
                .andExpect(jsonPath("$.description").value("Company Description"));

        //companyService.findById()가 한 번 호출되었는지 검증
        verify(companyService, times(1)).findById(companyId);
    }
}
