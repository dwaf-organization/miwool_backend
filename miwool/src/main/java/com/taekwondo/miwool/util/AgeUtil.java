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
        
        if (age <= 7) {
            return "유아";
        } else if (age == 8) {
            return "초1";
        } else if (age == 9) {
            return "초2";
        } else if (age == 10) {
            return "초3";
        } else if (age == 11) {
            return "초4";
        } else if (age == 12) {
            return "초5";
        } else if (age == 13) {
            return "초6";
        } else if (age == 14) {
            return "중1";
        } else if (age == 15) {
            return "중2";
        } else if (age == 16) {
            return "중3";
        } else if (age == 17) {
            return "고1";
        } else if (age == 18) {
            return "고2";
        } else if (age == 19) {
            return "고3";
        } else {
            return "성인";
        }
    }
    
    /**
     * 학년 코드로 나이 반환
     */
    public static Integer getAgeByGrade(String gradeCode) {
        if (gradeCode == null) {
            return null;
        }
        
        switch (gradeCode) {
            case "유아": return null;  // 0-7세 범위
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
            case "성인": return 20;  // 20세 이상
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
        
        if ("유아".equals(gradeCode)) {
            return age <= 7;
        } else if ("성인".equals(gradeCode)) {
            return age >= 20;
        } else {
            Integer targetAge = getAgeByGrade(gradeCode);
            return targetAge != null && age == targetAge;
        }
    }
}