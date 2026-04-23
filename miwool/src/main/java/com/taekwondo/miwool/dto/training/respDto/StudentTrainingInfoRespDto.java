package com.taekwondo.miwool.dto.training.respDto;

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
public class StudentTrainingInfoRespDto {
    
    private Integer trainingInfoCode;    // 수련정보코드
    private String packageCode;          // 패키지코드
    private String packageName;          // 패키지명
    private Integer basePrice;           // 기본교육비
    private Integer adjustmentAmount;    // 조정금액
    private String adjustmentDetail;     // 조정상세
    private Integer actualPrice;         // 총교육비
    private LocalDate trainingStartDate; // 수련시작일
    
    private List<TrainingClassDto> classes;  // 수업 목록
    
    private Integer useVehicle;          // 차량이용여부
    private String pickupLocation;       // 탑승장소
    private String dropoffLocation;      // 하차장소
    private String handoverMethod;       // 보호자인계방식
}