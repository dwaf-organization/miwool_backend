package com.taekwondo.miwool.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taekwondo.miwool.entity.StudentHealth;

import java.util.List;

@Repository
public interface StudentHealthRepository extends JpaRepository<StudentHealth, Integer> {
    List<StudentHealth> findByStudentCode(String studentCode);
    
    void deleteByStudentCode(String studentCode);
    
}