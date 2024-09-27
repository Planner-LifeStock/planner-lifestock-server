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

    /*
    //company create test이전에 미리 logo 객체를 생성하기 위해 만든 코드
    //test에서 logo를 default로 넘기기에 필요없는 로직인데 메모를 위해 남겨둠
    @BeforeEach
    void setUp(){
        mockLogo = FileResponseDto.builder()
                .id("1")
                .originalName("default_profile.png")
                .mimeType("text/plain")
                .size(1234L)
                .meta("example metadata")
                .url("http://example.com/file")
                .build();
    }*/

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


        /* logo 객체가 default가 아닌 경우를 위해 짜던 코드, 나중에 필요할지 몰라서 주석으로 남겨두었습니다. */
        /*
        // JSON 요청 본문 생성
        // logo 필드까지 포함한 json 요청 본문, error가 생겨서 메모차 남겨둠
        String jsonRequest = "{ \"userId\": 1, \"name\": \"Company Name\", \"description\": \"Company Description\", " +
                "\"level\": \"LOW\", \"leastOperatePeriod\": \"ONE_MONTH\", " +
                "\"listedDate\": \"" + LocalDate.now() + "\", \"investmentAmount\": 1000000, " +
                "\"initialStockPrice\": 1000, \"initialStockQuantity\": 100, \"logo\": " +
                "{\"id\": \"1\", \"originalName\": \"example\", \"mimeType\": \"text/plain\", " +
                "\"size\": 1234, \"meta\": \"example metadata\", \"url\": \"http://example.com/file\"}}";

        // MockMultipartFile을 사용하여 파일 생성
        MockMultipartFile mockFile = new MockMultipartFile(
                "logo", // 필드명
                "example.png", // 파일 이름
                "text/plain", // MIME 타입
                "This is a test file.".getBytes() // 파일 내용
        );
        // MockMvc를 통해 multipart POST 요청을 보내고 응답을 검증
        mockMvc.perform(MockMvcRequestBuilders.multipart("/company")
                        .file(mockFile) // 파일 추가
                        .param("userId", "1")
                        .param("name", "Company Name")
                        .param("description", "Company Description")
                        .param("level", "LOW")
                        .param("leastOperatePeriod", "ONE_MONTH")
                        .param("listedDate", LocalDate.now().toString())
                        .param("investmentAmount", "1000000")
                        .param("initialStockPrice", "1000")
                        .param("initialStockQuantity", "100")
                        .with(csrf()) // CSRF 토큰 추가
                        .with(httpBasic("user1", "password1"))
                        .contentType(MediaType.MULTIPART_FORM_DATA)) // multipart/form-data 설정
                .andDo(print()) // 요청과 응답을 출력
                .andExpect(status().isOk()) // 상태 코드가 200인지 확인
                .andExpect(jsonPath("$.name").value("Company Name")) // 응답 본문에서 name 필드 확인
                .andExpect(jsonPath("$.description").value("Company Description")); // 응답 본문에서 description 필드 확인
        */
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
