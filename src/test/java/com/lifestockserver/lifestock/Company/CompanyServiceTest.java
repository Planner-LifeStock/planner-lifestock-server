package com.lifestockserver.lifestock.Company;

import com.lifestockserver.lifestock.chart.service.ChartService;
import com.lifestockserver.lifestock.company.domain.Company;
import com.lifestockserver.lifestock.company.dto.CompanyCreateDto;
import com.lifestockserver.lifestock.company.dto.CompanyDeleteDto;
import com.lifestockserver.lifestock.company.dto.CompanyResponseDto;
import com.lifestockserver.lifestock.company.dto.CompanyUpdateDto;
import com.lifestockserver.lifestock.company.mapper.CompanyMapper;
import com.lifestockserver.lifestock.company.repository.CompanyRepository;
import com.lifestockserver.lifestock.company.service.CompanyService;
import com.lifestockserver.lifestock.file.service.FileService;
import com.lifestockserver.lifestock.user.domain.User;
import com.lifestockserver.lifestock.user.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CompanyServiceTest {

    @Mock
    private ChartService chartService;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private CompanyMapper companyMapper;

    @Mock
    private UserServiceImpl userServiceImpl;

    @Mock
    private FileService fileService;

    @InjectMocks
    private CompanyService companyService;

    private Company company;
    private CompanyCreateDto companyCreateDto;
    private CompanyResponseDto companyResponseDto;


    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        companyCreateDto = new CompanyCreateDto();
        companyResponseDto = CompanyResponseDto.builder()
                .id(1L)
                .userId(1L)
                .name("Test Company")
                .currentStockPrice(1000L)
                .build();
    }

    @WithMockUser(username = "user1", roles = {"USER"})
    @Test
    void testCreateCompany(){

        //given
        User user = new User();
        user.setId(1L);
        companyCreateDto.setUserId(1L);
        companyCreateDto.setName("Test Company");
        companyCreateDto.setInitialStockPrice(1000L);
        companyCreateDto.setInitialStockQuantity(100L);

        when(userServiceImpl.findUserById(anyLong())).thenReturn(Optional.of(user));

        Company companyToSave = Company.builder()
                .user(user)
                .name("Test company")
                .initialStockPrice(1000L)
                .initialStockQuantity(100L)
                .build();

        when(companyMapper.toEntity(any(CompanyCreateDto.class))).thenReturn(companyToSave);
        when(companyRepository.save(any(Company.class))).thenReturn(companyToSave);
        when(companyMapper.toDto(any(Company.class))).thenReturn(companyResponseDto);
        when(chartService.getLatestCloseByCompanyId(anyLong())).thenReturn(1000L);
        //when
        CompanyResponseDto result = companyService.createCompany(companyCreateDto);

        //then
        assertNotNull(result);
        assertEquals(1000L, result.getCurrentStockPrice());
        verify(companyRepository, times(1)).save(any(Company.class));
    }

    @WithMockUser(username = "user1", roles = {"USER"})
    @Test
    void testUpdateCompany(){

        Long companyId = 1L;

        company = Company.builder()
                .id(companyId)
                .name("Test company")
                .user(new User())
                .initialStockPrice(1000L)
                .initialStockPrice(100L)
                .description("Original Description")
                .build();

        CompanyUpdateDto companyUpdateDto = new CompanyUpdateDto();
        companyUpdateDto.setDescription("Updated Description");

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        when(companyMapper.toDto(any(Company.class))).thenReturn(companyResponseDto);
        when(companyRepository.save(any(Company.class))).thenReturn(company);
        when(chartService.getLatestCloseByCompanyId(anyLong())).thenReturn(1000L);

        CompanyResponseDto result = companyService.updateCompany(companyId, companyUpdateDto);

        assertNotNull(result);
        assertEquals("Updated Description", company.getDescription());
        verify(companyRepository, times(1)).save(any(Company.class));
    }
    @Test
    void testFindAllByUserId(){

        User user = new User();
        user.setId(1L);

        company = Company.builder()
                .id(1L)
                .user(user)
                .name("Test Company")
                .initialStockPrice(1000L)
                .initialStockQuantity(100L)
                .build();

        when(companyRepository.findAllByUserIdAndDeletedAtIsNull(anyLong())).thenReturn(List.of(company));
        when(companyMapper.toDto(any(Company.class))).thenReturn(companyResponseDto);
        when(chartService.getLatestCloseByCompanyId(anyLong())).thenReturn(1000L);

        List<CompanyResponseDto> result = companyService.findAllByUserId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1000L, result.get(0).getCurrentStockPrice());
    }

    @WithMockUser(username = "user1", roles = {"USER"})
    @Test
    void testFindById(){
        Long companyId = 1L;

        company = Company.builder()
                .id(companyId)
                .name("Test Company")
                .initialStockQuantity(1000L)
                .build();

        when(companyRepository.findByIdAndDeletedAtIsNull(companyId)).thenReturn(company);
        when(companyMapper.toDto(any(Company.class))).thenReturn(companyResponseDto);
        when(chartService.getLatestCloseByCompanyId(companyId)).thenReturn(1000L);

        CompanyResponseDto result = companyService.findById(companyId);

        assertNotNull(result);
        assertEquals(companyId, result.getId());
        assertEquals(1000L, result.getCurrentStockPrice());
        verify(companyRepository, times(1)).findByIdAndDeletedAtIsNull(companyId);
    }

    @WithMockUser(username = "user1", roles = {"USER"})
    @Test
    void testDeleteCompany() {
        Long companyId = 1L;

        company = Company.builder()
                .id(companyId)
                .name("Test Company")
                .build();

        CompanyDeleteDto companyDeleteDto = new CompanyDeleteDto();
        companyDeleteDto.setDeletedReason("No longer needed");

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));

        companyService.deleteCompany(companyId, companyDeleteDto);

        assertNotNull(company.getDeletedAt()); // 삭제 시간 확인
        assertEquals("No longer needed", company.getDeletedReason());
        verify(companyRepository, times(1)).save(company);
    }

}
