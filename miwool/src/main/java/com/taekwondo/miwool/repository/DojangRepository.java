package com.taekwondo.miwool.repository;

import com.taekwondo.miwool.entity.Dojang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DojangRepository extends JpaRepository<Dojang, String> {
    
    /**
     * 아이디로 도장 조회
     */
    Optional<Dojang> findByDojangId(String dojangId);
    
    /**
     * 아이디 중복 체크
     */
    boolean existsByDojangId(String dojangId);
    
    /**
     * 도장 코드로 조회
     */
    Optional<Dojang> findByDojangCode(String dojangCode);
    
    /**
     * 오늘 생성된 도장 수 조회 (dojangCode 생성용)
     */
    @Query("SELECT COUNT(d) FROM Dojang d WHERE d.dojangCode LIKE :prefix")
    long countByDojangCodePrefix(@Param("prefix") String prefix);
    
    /**
     * 특정 문자열로 시작하는 코드 중 코드 내림차순으로 첫 번째 항목 조회
     */
    Optional<Dojang> findFirstByDojangCodeStartingWithOrderByDojangCodeDesc(String prefix);
    
    /**
     * 삭제되지 않은 도장만 조회
     */
    @Query("SELECT d FROM Dojang d WHERE d.dojangId = :dojangId AND d.isDeleted = 0")
    Optional<Dojang> findActiveByDojangId(@Param("dojangId") String dojangId);
    
    /**
     * 도장명과 이메일로 도장 찾기 (아이디 찾기용)
     */
    Optional<Dojang> findByDojangNameAndDojangEmailAndIsDeleted(String dojangName, String dojangEmail, Integer isDeleted);
    
    /**
     * 아이디, 도장명, 이메일로 도장 찾기 (비밀번호 찾기용)
     */
    Optional<Dojang> findByDojangIdAndDojangNameAndDojangEmailAndIsDeleted(String dojangId, String dojangName, String dojangEmail, Integer isDeleted);
}