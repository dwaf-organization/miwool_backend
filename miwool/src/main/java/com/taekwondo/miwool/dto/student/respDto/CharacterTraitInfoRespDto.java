package com.taekwondo.miwool.dto.student.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CharacterTraitInfoRespDto {
    
    private String studentCode;                    // 제자코드
    
    private List<String> personalityBasic;         // 성향_기본 (PERS_BASIC)
    private String personalityBasicEtc;            // 성향_기본 기타값
    
    private List<String> emotion;                  // 성향_정서 (PERS_EMOT)
    private String emotionEtc;                     // 성향_정서 기타값
    
    private List<String> sociability;              // 성향_사회성 (PERS_SOC)
    private String sociabilityEtc;                 // 성향_사회성 기타값
    
    private List<String> lessonResponse;           // 성향_수업반응 (PERS_LESSON)
    private String lessonResponseEtc;              // 성향_수업반응 기타값
    
    private List<String> healthTrait;              // 건강특성 (HEALTH_FEAT)
    private String healthTraitEtc;                 // 건강특성 기타값
    
    private List<String> bodyTrait;                // 체형특성 (BODY_FEAT)
    private String bodyTraitEtc;                   // 체형특성 기타값
    
    private List<String> bodySensitive;            // 신체민감사항 (BODY_SENS)
    private String bodySensitiveEtc;               // 신체민감사항 기타값
    
    private List<String> changeNeed;               // 변화필요부분 (CHANGE_NEED)
    private String changeNeedEtc;                  // 변화필요부분 기타값
    
    private List<String> strength;                 // 강점 (STRENGTH)
    private String strengthEtc;                    // 강점 기타값
    
    private String skill;                          // 기능습득속도 (SKILL) 단일 선택
    private String skillEtc;                       // 기능습득속도 기타값
}