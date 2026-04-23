package com.taekwondo.miwool.dto.common.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
 
import java.util.List;
import java.util.Map;
 
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CharacterTraitOptionsRespDto {
    
    private Map<String, List<TraitOptionDto>> data;
    
    // Map의 key 값들:
    // - personalityBasic: 성향_기본 (PERS_BASIC)
    // - emotion: 성향_정서 (PERS_EMOT)
    // - sociability: 성향_사회성 (PERS_SOC)
    // - lessonResponse: 성향_수업반응 (PERS_LESSON)
    // - healthTrait: 건강특성 (HEALTH_FEAT)
    // - bodyTrait: 체형특성 (BODY_FEAT)
    // - bodySensitive: 신체민감사항 (BODY_SENS)
    // - changeNeed: 변화필요부분 (CHANGE_NEED)
    // - strength: 강점 (STRENGTH)
}