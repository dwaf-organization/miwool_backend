package com.taekwondo.miwool.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taekwondo.miwool.entity.StudentBodyPart;

import java.util.List;

@Repository
public interface StudentBodyPartRepository extends JpaRepository<StudentBodyPart, Integer> {
    List<StudentBodyPart> findByStudentCode(String studentCode);
    
    void deleteByStudentCode(String studentCode);
    
}