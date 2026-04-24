package com.taekwondo.miwool.dto.app.training.reqDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterTrainingReqDto {
    
    private String dojangCode; // 도장코드
    private String studentCode; // 제자코드
    private String packageCode; // 패키지코드
    private List<String> classCodes; // 수업코드 목록 (복수 선택)
    
    // 차량정보
    private Integer useVehicle; // 차량이용여부 (1=이용, 0=미이용)
    private String pickupLocation; // 탑승장소
    private String dropoffLocation; // 하차장소
    private String handoverMethod; // 보호자인계방식
    
    // 교육비정보
    private Integer adjustmentAmount; // 조정금액 (없으면 0)
    private String adjustmentDetail; // 조정상세 (없으면 null)
    
    // 시작일
    private LocalDate trainingStartDate; // 수련시작일
    private LocalDate trainingEndDate; // 수련종료일
}