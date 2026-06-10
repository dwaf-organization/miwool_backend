package com.taekwondo.miwool.util;

import java.time.LocalDate;

public class AgeUtil {
    
    /**
     * 한국식 나이 계산 (연도 차이 + 1)
     * 한국은 태어나자마자 1살
     * 예: 1997년생 → 2026년 기준 30세
     */
    public static int calculateKoreanAge(LocalDate birthDate) {
        if (birthDate == null) {
            return 0;
        }
        return LocalDate.now().getYear() - birthDate.getYear() + 1;
    }
    
    /**
     * 학년 계산 (한국 나이 기준)
     * 0-7세: 유아
     * 8세: 초1, 9세: 초2, ..., 13세: 초6
     * 14세: 중1, 15세: 중2, 16세: 중3
     * 17세: 고1, 18세: 고2, 19세: 고3
     * 20세 이상: 성인
     */
    public static String calculateGrade(LocalDate birthDate) {
        int age = calculateKoreanAge(birthDate);
        
        if (age == 1) return "1세";
        if (age == 2) return "2세";
        if (age == 3) return "3세";
        if (age == 4) return "4세";
        if (age == 5) return "5세";
        if (age == 6) return "6세";
        if (age == 7) return "7세";
        if (age == 8) return "초1";
        if (age == 9) return "초2";
        if (age == 10) return "초3";
        if (age == 11) return "초4";
        if (age == 12) return "초5";
        if (age == 13) return "초6";
        if (age == 14) return "중1";
        if (age == 15) return "중2";
        if (age == 16) return "중3";
        if (age == 17) return "고1";
        if (age == 18) return "고2";
        if (age == 19) return "고3";
        return "성인";
    }
    
    /**
     * 학년 코드로 나이 반환
     */
    public static Integer getAgeByGrade(String gradeCode) {
        if (gradeCode == null) {
            return null;
        }
        
        switch (gradeCode) {
            case "1세": return 1;
            case "2세": return 2;
            case "3세": return 3;
            case "4세": return 4;
            case "5세": return 5;
            case "6세": return 6;
            case "7세": return 7;
            case "초1": return 8;
            case "초2": return 9;
            case "초3": return 10;
            case "초4": return 11;
            case "초5": return 12;
            case "초6": return 13;
            case "중1": return 14;
            case "중2": return 15;
            case "중3": return 16;
            case "고1": return 17;
            case "고2": return 18;
            case "고3": return 19;
            case "성인": return 20;
            default: return null;
        }
    }
    
    /**
     * 특정 학년에 해당하는지 체크
     */
    public static boolean isGradeMatch(LocalDate birthDate, String gradeCode) {
        if (gradeCode == null) {
            return true;  // 필터 없음
        }
        
        int age = calculateKoreanAge(birthDate);
        
        if ("성인".equals(gradeCode)) {
            return age >= 20;
        }
        
        Integer targetAge = getAgeByGrade(gradeCode);
        return targetAge != null && age == targetAge;
    }
}