package com.lifestockserver.lifestock.company.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

import com.lifestockserver.lifestock.company.domain.Company;
import com.lifestockserver.lifestock.chart.service.ChartService;
import com.lifestockserver.lifestock.company.repository.CompanyRepository;
import com.lifestockserver.lifestock.user.service.UserServiceImpl;
import com.lifestockserver.lifestock.company.dto.CompanyCreateDto;
import com.lifestockserver.lifestock.company.dto.CompanyResponseDto;
import com.lifestockserver.lifestock.company.mapper.CompanyMapper;
import com.lifestockserver.lifestock.user.domain.User;
import com.lifestockserver.lifestock.file.service.FileService;
import com.lifestockserver.lifestock.company.dto.CompanyUpdateDto;
import com.lifestockserver.lifestock.company.dto.CompanyDeleteDto;
import com.lifestockserver.lifestock.file.dto.FileCreateDto;
import com.lifestockserver.lifestock.file.dto.FileResponseDto;
import com.lifestockserver.lifestock.common.domain.enums.FileFolder;
import com.lifestockserver.lifestock.file.domain.File;

@Service
public class CompanyService {

  private final ChartService chartService;
  private final CompanyRepository companyRepository;
  private final CompanyMapper companyMapper;
  private final UserServiceImpl userServiceImpl;
  private final FileService fileService;

  public CompanyService(ChartService chartService, CompanyRepository companyRepository, CompanyMapper companyMapper, UserServiceImpl userServiceImpl, FileService fileService) {
    this.chartService = chartService;
    this.companyRepository = companyRepository;
    this.companyMapper = companyMapper;
    this.userServiceImpl = userServiceImpl;
    this.fileService = fileService;
  }

  @Transactional
  public CompanyResponseDto createCompany(CompanyCreateDto companyCreateDto) {
    User user = userServiceImpl.findUserById(companyCreateDto.getUserId())
      .orElseThrow(() -> new EntityNotFoundException("User not found"));
    companyCreateDto.setUser(user);

    Company company = companyMapper.toEntity(companyCreateDto);

    // companyCreateDto.logo가 null이면 기본 로고를 설정
    if (companyCreateDto.getLogo() == null) {
      company.setLogo(fileService.getDefaultCompanyLogo());
    } else {
      FileCreateDto fileCreateDto = FileCreateDto.builder()
        .file(companyCreateDto.getLogo())
        .folder(FileFolder.COMPANY)
        .build();
      FileResponseDto fileResponseDto = fileService.saveFile(fileCreateDto);
      File file = fileService.getFileById(fileResponseDto.getId());
      company.setLogo(file);
    }

    Company savedCompany = companyRepository.save(company);
    CompanyResponseDto companyResponseDto = companyMapper.toDto(savedCompany);
    companyResponseDto.setCurrentStockPrice(savedCompany.getInitialStockPrice());
    
    return companyResponseDto;
  }

  @Transactional
  public CompanyResponseDto updateCompany(Long companyId, CompanyUpdateDto companyUpdateDto) {
    Company company = companyRepository.findById(companyId)
      .orElseThrow(() -> new EntityNotFoundException("Company not found"));
    if (companyUpdateDto.getLogo() != null && !company.getLogo().getOriginalName().equals(companyUpdateDto.getLogo().getOriginalFilename())) {
      FileCreateDto fileCreateDto = FileCreateDto.builder()
        .file(companyUpdateDto.getLogo())
        .folder(FileFolder.COMPANY)
        .build();
      FileResponseDto fileResponseDto = fileService.saveFile(fileCreateDto);
      File file = fileService.getFileById(fileResponseDto.getId());
      company.setLogo(file);
    }
    if (companyUpdateDto.getDescription() != null && !company.getDescription().equals(companyUpdateDto.getDescription())) {
      company.setDescription(companyUpdateDto.getDescription());
    }
      if (companyUpdateDto.getListedDate() != null && company.getListedDate() == null) {
      company.setListedDate(companyUpdateDto.getListedDate());
    }

    // companyCreateDto.logo가 null이면 기본 로고를 설정
    if (company.getLogo() == null) {
      company.setLogo(fileService.getDefaultCompanyLogo());
    }

    Company savedCompany = companyRepository.save(company);
    CompanyResponseDto companyResponseDto = companyMapper.toDto(savedCompany);
    companyResponseDto.setCurrentStockPrice(chartService.getLatestCloseByCompanyId(companyId));
    return companyResponseDto;
  }

  @Transactional(readOnly = true)
  public List<CompanyResponseDto> findAllByUserId(Long userId) {
    List<Company> companies = companyRepository.findAllByUserIdAndDeletedAtIsNull(userId);

    List<CompanyResponseDto> companyResponseDtos = companies.stream()
      .map(company -> {
        CompanyResponseDto companyResponseDto = companyMapper.toDto(company);
        companyResponseDto.setCurrentStockPrice(chartService.getLatestCloseByCompanyId(company.getId()));
        return companyResponseDto;
      })
      .collect(Collectors.toList());
    return companyResponseDtos;
  }

  @Transactional(readOnly = true)
  public CompanyResponseDto findById(Long id) {
    Company company = companyRepository.findByIdAndDeletedAtIsNull(id);
    if (company == null) {
      throw new EntityNotFoundException("Company not found");
    }

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
