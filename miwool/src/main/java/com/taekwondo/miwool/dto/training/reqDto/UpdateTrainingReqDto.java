package com.taekwondo.miwool.dto.training.reqDto;

import jakarta.validation.constraints.*;
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
public class UpdateTrainingReqDto {
    
    @NotNull(message = "수련정보코드는 필수입니다")
    private Integer trainingInfoCode;    // 수련정보코드
    
    @NotBlank(message = "제자코드는 필수입니다")
    private String studentCode;          // 제자코드
    
    @NotBlank(message = "패키지코드는 필수입니다")
    private String packageCode;          // 패키지코드
    
    @NotNull(message = "수업코드는 필수입니다")
    @Size(min = 1, message = "최소 1개 이상의 수업을 선택해야 합니다")
    private List<String> classCodes;     // 수업코드 목록 (복수 선택)
    
    // 차량정보
    @NotNull(message = "차량이용여부는 필수입니다")
    private Integer useVehicle;          // 차량이용여부 (1=이용, 0=미이용)
    
    private String pickupLocation;       // 탑승장소
    private String dropoffLocation;      // 하차장소
    private String handoverMethod;       // 보호자인계방식
    
    // 교육비정보
    private Integer adjustmentAmount;    // 조정금액 (없으면 0)
    private String adjustmentDetail;     // 조정상세 (없으면 null)
    
    // 시작일
    @NotNull(message = "수련시작일은 필수입니다")
    private LocalDate trainingStartDate; // 수련시작일
}