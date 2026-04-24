package com.taekwondo.miwool.repository;

import com.taekwondo.miwool.entity.TrainingMst;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainingMstRepository extends JpaRepository<TrainingMst, String> {
    
    /**
     * 도장별 패키지 목록 조회 (최신순)
     */
    List<TrainingMst> findByDojangCodeOrderByCreatedAtDesc(String dojangCode);
    
    /**
     * 도장별 패키지 목록 조회 (과거순)
     */
    List<TrainingMst> findByDojangCodeOrderByCreatedAtAsc(String dojangCode);
    
    /**
     * packageCode 자동 생성용
     * 형식: MW26001-PKG001
     */
    Optional<TrainingMst> findFirstByPackageCodeStartingWithOrderByPackageCodeDesc(String prefix);
}