package com.lifestockserver.lifestock.company.service;

import com.lifestockserver.lifestock.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.time.LocalDate;
import com.lifestockserver.lifestock.company.domain.Company;
import com.lifestockserver.lifestock.chart.service.ChartService;
import com.lifestockserver.lifestock.company.repository.CompanyRepository;
import com.lifestockserver.lifestock.company.dto.CompanyCreateDto;
import com.lifestockserver.lifestock.company.dto.CompanyResponseDto;
import com.lifestockserver.lifestock.company.mapper.CompanyMapper;
import com.lifestockserver.lifestock.user.domain.User;
import com.lifestockserver.lifestock.file.service.FileService;
import com.lifestockserver.lifestock.company.dto.CompanyUpdateDto;
import com.lifestockserver.lifestock.company.dto.CompanyDeleteDto;
import com.lifestockserver.lifestock.file.domain.File;
import com.lifestockserver.lifestock.company.domain.enums.CompanyStatus;
import com.lifestockserver.lifestock.todo.service.TodoService;
import com.lifestockserver.lifestock.chart.domain.Chart;
import com.lifestockserver.lifestock.chart.dto.ChartResponseDto;
import com.lifestockserver.lifestock.file.dto.FileCreateDto;
import com.lifestockserver.lifestock.common.domain.enums.FileFolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@Transactional(readOnly = true)
public class CompanyService {

  private final ChartService chartService;
  private final CompanyRepository companyRepository;
  private final CompanyMapper companyMapper;
  private final UserRepository userRepository;
  private final FileService fileService;
  private final TodoService todoService;

  public CompanyService(ChartService chartService,
                        CompanyRepository companyRepository,
                        CompanyMapper companyMapper,
                        UserRepository userRepository,
                        FileService fileService,
                        TodoService todoService) {
    this.chartService = chartService;
    this.companyRepository = companyRepository;
    this.companyMapper = companyMapper;
    this.userRepository = userRepository;
    this.fileService = fileService;
    this.todoService = todoService;
  }

  // file 저장은 따로 api 보내야만함
  @Transactional
  public CompanyResponseDto createCompany(Long userId, CompanyCreateDto companyCreateDto, MultipartFile logo) {
    File savedLogo = null;
    if (logo != null) {
      savedLogo = fileService.getFileById(fileService.saveFile(FileCreateDto.builder()
        .file(logo)
        .folder(FileFolder.COMPANY)
        .build()).getId());
    } else {
      savedLogo = fileService.getDefaultCompanyLogo();
    }
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
    companyCreateDto.setUser(user);

    Long investmentAmount = companyCreateDto.getInitialStockQuantity() * companyCreateDto.getInitialStockPrice();
    if (user.getAsset() < investmentAmount) {
      throw new IllegalArgumentException("User does not have enough asset");
    }
    user.setAsset(user.getAsset() - investmentAmount < 100000 ? 100000 : user.getAsset() - investmentAmount);
    
    Company company = companyMapper.toEntity(companyCreateDto);
    
    company.setLogo(savedLogo);
    
    Company savedCompany = companyRepository.save(company);
    ChartResponseDto chart = chartService.createInitialChart(savedCompany, user, investmentAmount);
    Long currentStockPrice = chart.getClose();
    CompanyResponseDto companyResponseDto = companyMapper.toDto(savedCompany);
    companyResponseDto.setCurrentStockPrice(currentStockPrice);
    companyResponseDto.setOpenStockPrice(chart.getOpen());

    chartService.createInitialChart(savedCompany, user, savedCompany.getInitialStockPrice());
    
    return companyResponseDto;
  }

  @Transactional
  public CompanyResponseDto updateCompany(Long companyId, CompanyUpdateDto companyUpdateDto, MultipartFile logo) {
    Company company = companyRepository.findById(companyId)
      .orElseThrow(() -> new EntityNotFoundException("Company not found"));
    if (company.getLogo() != fileService.getDefaultCompanyLogo()) {
      fileService.deleteFile(company.getLogo().getId());
    }
    File savedLogo;
    if (logo != null) {
      savedLogo = fileService.getFileById(fileService.saveFile(FileCreateDto.builder()
        .file(logo)
        .folder(FileFolder.COMPANY)
        .build()).getId());
    } else {
      savedLogo = fileService.getDefaultCompanyLogo();
    }
    company.setLogo(savedLogo);
    if (companyUpdateDto.getDescription() != null && !companyUpdateDto.getDescription().equals(company.getDescription())) {
      company.setDescription(companyUpdateDto.getDescription());
    }
    company.setLogo(savedLogo);

    CompanyResponseDto companyResponseDto = companyMapper.toDto(companyRepository.save(company));
    Chart chart = chartService.getLatestAfterMarketOpenChartByCompanyId(companyId);
    Long currentStockPrice = chart.getClose();
    companyResponseDto.setCurrentStockPrice(currentStockPrice);
    companyResponseDto.setOpenStockPrice(chart.getOpen());
    return companyResponseDto;
  }

  @Transactional
  public CompanyResponseDto listCompany(Long companyId) {
    Company company = companyRepository.findById(companyId)
      .orElseThrow(() -> new EntityNotFoundException("Company not found"));

    if (company.getListedDate() != null) {
      throw new IllegalArgumentException("Company is already listed");
    }

    LocalDate leastOperatePeriodDate = company.getCreatedAt().toLocalDate().plusDays(company.getLeastOperatePeriod().getDays());
    if (leastOperatePeriodDate.isAfter(LocalDate.now())) {
      throw new IllegalArgumentException("Company can not be listed yet");
    }

    company.setListed(LocalDate.now(), chartService.getLatestCloseByCompanyId(companyId));

    todoService.deleteTodosAfterDateByCompanyId(companyId, LocalDate.now());

    CompanyResponseDto companyResponseDto = companyMapper.toDto(company);
    Chart chart = chartService.getLatestAfterMarketOpenChartByCompanyId(companyId);
    Long currentStockPrice = chart.getClose();
    companyResponseDto.setCurrentStockPrice(currentStockPrice);
    companyResponseDto.setOpenStockPrice(chart.getOpen());

    company.getUser().setAsset(company.getUser().getAsset() + currentStockPrice * company.getInitialStockQuantity());
    return companyResponseDto;
  }

  public List<CompanyResponseDto> findAllByUserId(Long userId, CompanyStatus status) {
    List<Company> companies;
    if (status == CompanyStatus.LISTED) {
      // 상장된 회사만 조회
      companies = companyRepository.findListedCompaniesByUserId(userId);
    } else if (status == CompanyStatus.UNLISTED) {
      // 미상장된 회사만 조회
      companies = companyRepository.findUnlistedCompaniesByUserId(userId);
    } else {
      // 모든 회사 조회
      companies = companyRepository.findAllByUserId(userId);
    }

    return companies.stream()
            .map(company -> {
              Chart chart = chartService.getLatestAfterMarketOpenChartByCompanyId(company.getId());
              CompanyResponseDto companyResponseDto = companyMapper.toDto(company);
              companyResponseDto.setOpenStockPrice(chart.getOpen());
              companyResponseDto.setCurrentStockPrice(chart.getClose());
              return companyResponseDto;
            })
            .collect(Collectors.toList());
  }

  public CompanyResponseDto findById(Long id) {
    Company company = companyRepository.findById(id)
      .orElseThrow(() -> new EntityNotFoundException("Company not found"));

    // 가장 최근의 high 값을 가져와서 currentStockPrice에 설정
    Chart chart = chartService.getLatestAfterMarketOpenChartByCompanyId(id);
    Long currentStockPrice = chart.getClose();
    
    CompanyResponseDto companyResponseDto = companyMapper.toDto(company);
    companyResponseDto.setCurrentStockPrice(currentStockPrice);
    companyResponseDto.setOpenStockPrice(chart.getOpen());

    return companyResponseDto;
  }

  @Transactional
  public void deleteCompany(Long companyId, CompanyDeleteDto companyDeleteDto) {
    Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new EntityNotFoundException("Company not found"));

    company.setDeletedAt(LocalDateTime.now());
    company.setDeletedReason(companyDeleteDto.getDeletedReason());

    companyRepository.save(company);
  }

  public List<Company> findAllUnlisted() {
    return companyRepository.findAllUnlisted();
  }
}
