package com.taekwondo.miwool.repository;

import com.taekwondo.miwool.entity.StudentSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentSkillRepository extends JpaRepository<StudentSkill, Integer> {

    // 제자코드로 단일 조회 (단일 선택)
    Optional<StudentSkill> findByStudentCode(String studentCode);

    // 제자코드로 삭제 (저장 전 기존 데이터 제거)
    @Modifying
    @Query("DELETE FROM StudentSkill ss WHERE ss.studentCode = :studentCode")
    void deleteByStudentCode(@Param("studentCode") String studentCode);
}