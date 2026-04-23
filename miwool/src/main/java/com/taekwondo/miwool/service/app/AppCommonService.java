package com.taekwondo.miwool.service.app;

import com.taekwondo.miwool.dto.common.respDto.CommonCodeRespDto;
import com.taekwondo.miwool.service.CommonCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppCommonService {

    private final CommonCodeService commonCodeService;

    /**
     * 앱 공통코드 조회
     */
    @Transactional(readOnly = true)
    public List<CommonCodeRespDto> getCodesByGroupCode(String groupCode) {
        log.info("앱 공통코드 조회: groupCode={}", groupCode);
        return commonCodeService.getCodesByGroupCode(groupCode);
    }
}