package com.taekwondo.miwool.repository;

import com.taekwondo.miwool.entity.ClassMst;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassMstRepository extends JpaRepository<ClassMst, String> {
    
    /**
     * 도장별 수업 목록 조회 (최신순)
     */
    List<ClassMst> findByDojangCodeOrderByCreatedAtDesc(String dojangCode);
    
    /**
     * classCode 자동 생성용
     * 형식: MW26001-CLS001
     */
    Optional<ClassMst> findFirstByClassCodeStartingWithOrderByClassCodeDesc(String prefix);
}