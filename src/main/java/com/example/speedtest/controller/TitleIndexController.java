package com.example.speedtest.controller;

import com.example.speedtest.service.TitleIndexService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // REST API 컨트롤러
@RequiredArgsConstructor
public class TitleIndexController {

    private final TitleIndexService titleIndexService;

    /**
     * 🔄 Title 전체 데이터를 Elasticsearch에 색인하는 엔드포인트
     * - 수동 색인 실행용 (자동 색인이 안 되었거나 재색인 필요 시 사용)
     * - 예: GET /api/index
     *
     * @return 완료 메시지
     */
    @GetMapping("/api/index")
    public String indexData() {
        titleIndexService.indexAll(); // JPA → Elasticsearch 색인 실행
        return "완료!";
    }
}
