package com.example.speedtest.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.example.speedtest.entity.TitleDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service // Spring Bean으로 등록되는 서비스 계층 클래스
@RequiredArgsConstructor
public class TitleAutoCompleteService {

    private final ElasticsearchClient elasticsearchClient;

    /**
     * 🔍 입력한 prefix를 기반으로 Elasticsearch에서 자동완성 후보를 검색
     * - match_phrase_prefix 쿼리 사용
     * - 검색 대상 필드: titleAutocomplete
     * - 결과 개수 제한: 10개
     *
     * @param prefix 사용자가 입력한 검색어 앞부분
     * @return 자동완성 추천 결과 (tId와 title 정보를 Map으로 반환)
     */
    public List<Map<String, Object>> autocompleteTitle(String prefix) {
        try {
            // 🔎 Elasticsearch에 matchPhrasePrefix 쿼리 수행
            SearchResponse<TitleDocument> response = elasticsearchClient.search(s -> s
                            .index("title_autocomplete") // 자동완성 전용 인덱스
                            .query(q -> q
                                    .matchPhrasePrefix(mpp -> mpp
                                            .field("titleAutocomplete") // 자동완성 필드
                                            .query(prefix)              // 사용자 입력값
                                    )
                            )
                            .size(10), // 결과 제한 수
                    TitleDocument.class
            );

            // 🎯 검색 결과를 Map으로 가공하여 반환
            return response.hits().hits().stream()
                    .map(hit -> {
                        TitleDocument doc = hit.source();
                        Map<String, Object> map = new HashMap<>();
                        map.put("tId", doc.getTId());
                        map.put("title", doc.getTitle());
                        return map;
                    })
                    .toList();

        } catch (IOException e) {
            // 실패 시 런타임 예외로 래핑하여 전달
            throw new RuntimeException("자동완성 실패", e);
        }
    }
}
