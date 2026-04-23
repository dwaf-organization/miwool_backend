package com.taekwondo.miwool.repository;

import com.taekwondo.miwool.entity.CommonCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommonCodeRepository extends JpaRepository<CommonCode, String> {
    
    /**
     * 그룹코드별 사용중인 공통코드 조회 (정렬순서대로)
     */
    List<CommonCode> findByGroupCodeAndUseYnOrderByCodeOrderAsc(String groupCode, String useYn);
    
    /**
     * 그룹코드와 여러 공통코드로 조회
     * 급수명 조회에 사용
     */
    List<CommonCode> findByGroupCodeAndCommonCodeIn(String groupCode, List<String> commonCodes);
    
}