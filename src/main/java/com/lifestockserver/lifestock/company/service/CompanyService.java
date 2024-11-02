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

import lombok.extern.slf4j.Slf4j;

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

  public CompanyService(ChartService chartService, CompanyRepository companyRepository, CompanyMapper companyMapper, UserRepository userRepository, FileService fileService, TodoService todoService) {
    this.chartService = chartService;
    this.companyRepository = companyRepository;
    this.companyMapper = companyMapper;
    this.userRepository = userRepository;
    this.fileService = fileService;
    this.todoService = todoService;
  }

  // file 저장은 따로 api 보내야만함
  @Transactional
  public CompanyResponseDto createCompany(Long userId, CompanyCreateDto companyCreateDto) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
    companyCreateDto.setUser(user);

    Long investmentAmount = companyCreateDto.getInitialStockQuantity() * companyCreateDto.getInitialStockPrice();
    if (user.getAsset() < investmentAmount) {
      throw new IllegalArgumentException("User does not have enough asset");
    }
    user.setAsset(user.getAsset() - investmentAmount < 100000 ? 100000 : user.getAsset() - investmentAmount);

    Company company = companyMapper.toEntity(companyCreateDto);

    // companyCreateDto.logo가 null이면 기본 로고를 설정
    if (companyCreateDto.getLogoFileId() == null) {
      company.setLogo(fileService.getDefaultCompanyLogo());
    } else {
      File file = fileService.getFileById(companyCreateDto.getLogoFileId());
      company.setLogo(file);
    }

    Company savedCompany = companyRepository.save(company);
    CompanyResponseDto companyResponseDto = companyMapper.toDto(savedCompany);
    companyResponseDto.setCurrentStockPrice(savedCompany.getInitialStockPrice());

    chartService.createInitialChart(savedCompany, user, savedCompany.getInitialStockPrice());
    
    return companyResponseDto;
  }

  @Transactional
  public CompanyResponseDto updateCompany(Long companyId, CompanyUpdateDto companyUpdateDto) {
    Company company = companyRepository.findById(companyId)
      .orElseThrow(() -> new EntityNotFoundException("Company not found"));
    if (companyUpdateDto.getLogoFileId() != null && !company.getLogo().getId().equals(companyUpdateDto.getLogoFileId())) {
      File file = fileService.getFileById(companyUpdateDto.getLogoFileId());
      company.setLogo(file);
    }
    if (companyUpdateDto.getDescription() != null && !companyUpdateDto.getDescription().equals(company.getDescription())) {
      company.setDescription(companyUpdateDto.getDescription());
    }

    // companyCreateDto.logo가 null이면 기본 로고를 설정
    if (company.getLogo() == null) {
      company.setLogo(fileService.getDefaultCompanyLogo());
    }

    CompanyResponseDto companyResponseDto = companyMapper.toDto(company);
    companyResponseDto.setCurrentStockPrice(chartService.getLatestCloseByCompanyId(companyId));
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
    Long currentStockPrice = chartService.getLatestCloseByCompanyId(companyId);
    companyResponseDto.setCurrentStockPrice(currentStockPrice);
    company.getUser().setAsset(company.getUser().getAsset() + currentStockPrice * company.getInitialStockQuantity());
    return companyResponseDto;
  }

  public List<CompanyResponseDto> findAllByUserId(Long userId, CompanyStatus status) {
    if (status == CompanyStatus.LISTED) {
      return findListedCompaniesByUserId(userId);
    }
    if (status == CompanyStatus.UNLISTED) {
      return findUnlistedCompaniesByUserId(userId);
    }

    List<Company> companies = companyRepository.findAllByUserId(userId);  
    List<CompanyResponseDto> companyResponseDtos = companies.stream()
      .map(company -> {
        CompanyResponseDto companyResponseDto = companyMapper.toDto(company);
        companyResponseDto.setCurrentStockPrice(chartService.getLatestCloseByCompanyId(company.getId()));
        return companyResponseDto;
      })
      .collect(Collectors.toList());
    return companyResponseDtos;
  }

  public List<CompanyResponseDto> findListedCompaniesByUserId(Long userId) {
    List<Company> companies = companyRepository.findListedCompaniesByUserId(userId);

    return companies.stream()
      .map(company -> companyMapper.toDto(company))
      .collect(Collectors.toList());
  }

  public List<CompanyResponseDto> findUnlistedCompaniesByUserId(Long userId) {
    List<Company> companies = companyRepository.findUnlistedCompaniesByUserId(userId);

    return companies.stream()
      .map(company -> companyMapper.toDto(company))
      .collect(Collectors.toList());
  }

  public CompanyResponseDto findById(Long id) {
    Company company = companyRepository.findById(id)
      .orElseThrow(() -> new EntityNotFoundException("Company not found"));

    // 가장 최근의 high 값을 가져와서 currentStockPrice에 설정
    Long currentStockPrice = chartService.getLatestCloseByCompanyId(id);
    
    CompanyResponseDto companyResponseDto = companyMapper.toDto(company);
    companyResponseDto.setCurrentStockPrice(currentStockPrice);
    
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
}
