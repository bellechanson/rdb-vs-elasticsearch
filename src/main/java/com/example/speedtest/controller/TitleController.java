package com.example.speedtest.controller;

import com.example.speedtest.entity.Title;
import com.example.speedtest.service.TitleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // RESTful 웹 API 컨트롤러
@RequestMapping("/api/titles") // 기본 URI prefix 설정
@RequiredArgsConstructor
public class TitleController {

    private final TitleService titleService;

    /**
     * 🔍 키워드를 포함한 제목 검색 (RDB 기반)
     * - JPA의 LIKE 쿼리를 이용하여 title 컬럼에서 검색
     * - 예: /api/titles/search?keyword=스프링
     *
     * @param keyword 검색할 문자열
     * @return 제목에 해당 키워드를 포함하는 Title 리스트
     */
    @GetMapping("/search")
    public ResponseEntity<List<Title>> searchByKeyword(@RequestParam String keyword) {
        return ResponseEntity.ok(titleService.searchByKeyword(keyword));
    }

    /**
     * ✅ 전체 Title 데이터를 조회
     * - 색인 동기화 확인용 등으로 사용 가능
     * - 예: /api/titles/all
     *
     * @return 전체 Title 리스트
     */
    @GetMapping("/all")
    public ResponseEntity<List<Title>> getAll() {
        return ResponseEntity.ok(titleService.getAll());
    }
}
