package com.taekwondo.miwool.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taekwondo.miwool.entity.StudentBodyType;

import java.util.List;

@Repository
public interface StudentBodyTypeRepository extends JpaRepository<StudentBodyType, Integer> {
    List<StudentBodyType> findByStudentCode(String studentCode);
    
    void deleteByStudentCode(String studentCode);
    
}