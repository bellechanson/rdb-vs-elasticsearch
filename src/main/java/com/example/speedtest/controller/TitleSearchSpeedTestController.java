package com.example.speedtest.controller;

import com.example.speedtest.service.TitleSearchSpeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController // REST API 컨트롤러
@RequestMapping("/api/test/title-speed") // 검색 성능 비교 테스트 API 엔드포인트
@RequiredArgsConstructor
public class TitleSearchSpeedTestController {

    private final TitleSearchSpeedService titleSearchSpeedService;

    /**
     * ✅ 일반 키워드 검색 성능 비교 (RDB vs Elasticsearch)
     * - RDB: JPA LIKE 쿼리
     * - ES: match 쿼리 기반 검색
     * - 결과: 검색 결과 + 응답 시간(ms) 포함
     *
     * 예: GET /api/test/title-speed/rdb-vs-es?keyword=스프링
     *
     * @param keyword 검색 키워드
     * @return 검색 결과 및 소요 시간 비교 정보 (Map)
     */
    @GetMapping("/rdb-vs-es")
    public Map<String, Object> compareSearchSpeed(@RequestParam String keyword) {
        return titleSearchSpeedService.compareSearchSpeed(keyword);
    }

    /**
     * ✅ 대용량 데이터에 대한 검색 성능 비교 (Scroll API 기반)
     * - RDB: 대량 LIKE 검색
     * - ES: Scroll API를 통한 페이징 방식 검색
     * - 결과: 양쪽의 검색 결과 및 소요 시간 포함
     *
     * 예: GET /api/test/title-speed/rdb-vs-es-scroll?keyword=스프링
     *
     * @param keyword 검색 키워드
     * @return 검색 결과 및 성능 비교 결과 (Map)
     */
    @GetMapping("/rdb-vs-es-scroll")
    public Map<String, Object> compareSearchSpeedWithScroll(@RequestParam String keyword) {
        return titleSearchSpeedService.compareSearchSpeedWithScroll(keyword);
    }
}
