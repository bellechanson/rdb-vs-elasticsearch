package com.example.speedtest.controller;

import com.example.speedtest.service.TitleAutoCompleteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController // JSON 기반 REST API 컨트롤러
@RequestMapping("/api/autocomplete") // 모든 요청의 기본 URI Prefix
@RequiredArgsConstructor
public class TitleAutoCompleteController {

    private final TitleAutoCompleteService titleAutoCompleteService;

    /**
     * 🔍 제목 자동완성 API
     * - 프론트엔드에서 prefix(입력 문자열)를 넘기면
     * - Elasticsearch에서 matchPhrasePrefix 쿼리를 수행하여 연관된 제목 추천
     *
     * @param prefix 사용자가 입력 중인 문자열
     * @return 자동완성 추천 결과 (Map의 리스트 형태)
     */
    @GetMapping("/title")
    public ResponseEntity<List<Map<String, Object>>> suggest(@RequestParam String prefix) {
        return ResponseEntity.ok(titleAutoCompleteService.autocompleteTitle(prefix));
    }
}
