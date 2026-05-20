package com.taekwondo.miwool.repository;

import com.taekwondo.miwool.entity.Dojang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
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
    
    /**
     * 삭제되지 않은 도장 개수 조회
     */
    long countByIsDeleted(Integer isDeleted);
    
    /**
     * 관리자 도장 목록 조회 (필터링)
     */
    @Query(value = """
        SELECT 
          d.dojang_code,
          d.dojang_name,
          d.master_name,
          d.master_phone,
          d.dojang_tel,
          CONCAT(COALESCE(d.dojang_add, ''), ' ', COALESCE(d.dojang_add2, '')) as dojang_address,
          d.dojang_status,
          d.approval_yn,
          d.vip_package
        FROM taekwondo_mst d
        WHERE d.is_deleted = 0
          AND (:dojangSearch IS NULL OR :dojangSearch = '' 
               OR d.dojang_name LIKE CONCAT('%', :dojangSearch, '%')
               OR d.dojang_code LIKE CONCAT('%', :dojangSearch, '%'))
          AND (:dojangStatus = '전체' OR d.dojang_status = :dojangStatus)
          AND (:approvalYn = '전체' 
               OR (:approvalYn = '1' AND d.approval_yn = 1)
               OR (:approvalYn = '0' AND d.approval_yn = 0))
          AND (:vipPackage = '전체' OR d.vip_package = :vipPackage)
        ORDER BY d.created_at DESC
        """, nativeQuery = true)
    List<Object[]> findDojangsForAdmin(
            @Param("dojangSearch") String dojangSearch,
            @Param("dojangStatus") String dojangStatus,
            @Param("approvalYn") String approvalYn,
            @Param("vipPackage") String vipPackage);
    
    
}