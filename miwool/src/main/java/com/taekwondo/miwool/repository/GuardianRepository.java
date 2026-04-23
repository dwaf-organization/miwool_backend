package com.taekwondo.miwool.repository;

import com.taekwondo.miwool.entity.Guardian;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GuardianRepository extends JpaRepository<Guardian, String> {
    
	Optional<Guardian> findFirstByGuardianCodeStartingWithOrderByGuardianCodeDesc(String prefix);
	
    /**
     * 해당 월에 생성된 보호자 수 조회 (guardianCode 생성용)
     */
    @Query("SELECT COUNT(g) FROM Guardian g WHERE g.guardianCode LIKE :prefix")
    long countByGuardianCodePrefix(@Param("prefix") String prefix);
    
    // 기존 메서드
    List<Guardian> findAllById(Iterable<String> guardianCodes);
    
    // guardianCode 자동 생성을 위한 마지막 코드 조회
    Optional<Guardian> findTopByDojangCodeOrderByGuardianCodeDesc(String dojangCode);
    
    /**
     * 알림 - 오늘 생일인 보호자 조회
     */
    @Query(value = 
        "SELECT * FROM guardian_mst " +
        "WHERE guardian_birth_date IS NOT NULL " +
        "AND MONTH(guardian_birth_date) = :month " +
        "AND DAY(guardian_birth_date) = :day",
        nativeQuery = true)
    List<Guardian> findByBirthMonthAndDay(
        @Param("month") int month,
        @Param("day") int day);
    
    /**
     * 알림 - 오늘 결혼기념일인 보호자 조회
     */
    @Query(value = 
        "SELECT * FROM guardian_mst " +
        "WHERE guardian_anniversary_date IS NOT NULL " +
        "AND MONTH(guardian_anniversary_date) = :month " +
        "AND DAY(guardian_anniversary_date) = :day",
        nativeQuery = true)
    List<Guardian> findByAnniversaryMonthAndDay(
        @Param("month") int month,
        @Param("day") int day);
    
}