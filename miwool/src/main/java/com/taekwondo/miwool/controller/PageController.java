package com.taekwondo.miwool.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    /**
     * 제자 대량 등록 페이지
     * GET /admin/students/bulk-register
     */
    @GetMapping("/admin/students/bulk-register")
    public String bulkRegisterPage() {
        return "student-bulk-upload";
    }
}