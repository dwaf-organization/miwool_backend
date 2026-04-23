package com.taekwondo.miwool.repository;

import com.taekwondo.miwool.entity.StudentFamilySituation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentFamilySituationRepository extends JpaRepository<StudentFamilySituation, Integer> {

    /**
     * 특정 제자의 가족 특이사항 매핑 리스트 조회
     */
    List<StudentFamilySituation> findByStudentCode(String studentCode);

    /**
     * 특정 제자의 데이터 삭제 (재설정 시 사용)
     */
    void deleteByStudentCode(String studentCode);
    
}