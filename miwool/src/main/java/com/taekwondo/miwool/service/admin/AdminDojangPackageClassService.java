package com.taekwondo.miwool.service.admin;

import com.taekwondo.miwool.dto.admin.dojang.respDto.PackagesClassesRespDto;
import com.taekwondo.miwool.dto.admin.dojang.respDto.PackagesClassesRespDto.ClassDto;
import com.taekwondo.miwool.dto.admin.dojang.respDto.PackagesClassesRespDto.PackageDto;
import com.taekwondo.miwool.repository.ClassMstRepository;
import com.taekwondo.miwool.repository.TrainingMstRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminDojangPackageClassService {
    
    @Autowired
    private TrainingMstRepository trainingMstRepository;
    
    @Autowired
    private ClassMstRepository classMstRepository;
    
    @Transactional(readOnly = true)
    public PackagesClassesRespDto getPackagesAndClasses(String dojangCode) {
        log.info("패키지 및 수업 목록 조회 시작: dojangCode={}", dojangCode);
        
        // 1. 패키지 목록 조회
        List<PackageDto> packages = getPackages(dojangCode);
        
        // 2. 수업 목록 조회
        List<ClassDto> classes = getClasses(dojangCode);
        
        log.info("패키지 및 수업 목록 조회 완료: dojangCode={}, packages={}, classes={}", 
                dojangCode, packages.size(), classes.size());
        
        return PackagesClassesRespDto.builder()
                .packages(packages)
                .classes(classes)
                .build();
    }
    
    // 패키지 목록 조회
    private List<PackageDto> getPackages(String dojangCode) {
        // (package_code, package_name, weekly_count, base_price, use_yn, student_count)
        List<Object[]> packageData = trainingMstRepository.findPackagesWithStudentCount(dojangCode);
        
        return packageData.stream()
                .map(row -> {
                    String packageName = (String) row[1];
                    Integer classCountPerWeek = ((Number) row[2]).intValue();
                    Integer monthlyFee = ((Number) row[3]).intValue();
                    String useYn = String.valueOf(row[4]); // Character → String 변환
                    Integer studentCount = ((Number) row[5]).intValue();
                    
                    String status = "Y".equals(useYn) ? "사용중" : "미사용";
                    
                    return PackageDto.builder()
                            .packageName(packageName)
                            .classCountPerWeek(classCountPerWeek)
                            .monthlyFee(monthlyFee)
                            .studentCount(studentCount)
                            .status(status)
                            .build();
                })
                .collect(Collectors.toList());
    }
    
    // 수업 목록 조회
    private List<ClassDto> getClasses(String dojangCode) {
        // (day_of_week, class_name, start_time, end_time, student_count)
        List<Object[]> classData = classMstRepository.findClassesWithStudentCount(dojangCode);
        
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        
        return classData.stream()
                .map(row -> {
                    String classDay = (String) row[0];
                    String className = (String) row[1];
                    
                    // Time 또는 LocalTime으로 올 수 있음
                    LocalTime startTime;
                    LocalTime endTime;
                    
                    if (row[2] instanceof Time) {
                        startTime = ((Time) row[2]).toLocalTime();
                        endTime = ((Time) row[3]).toLocalTime();
                    } else {
                        startTime = (LocalTime) row[2];
                        endTime = (LocalTime) row[3];
                    }
                    
                    String classTime = startTime.format(timeFormatter) + "~" + endTime.format(timeFormatter);
                    Integer studentCount = ((Number) row[4]).intValue();
                    
                    return ClassDto.builder()
                            .classDay(classDay)
                            .className(className)
                            .classTime(classTime)
                            .studentCount(studentCount)
                            .build();
                })
                .collect(Collectors.toList());
    }
}