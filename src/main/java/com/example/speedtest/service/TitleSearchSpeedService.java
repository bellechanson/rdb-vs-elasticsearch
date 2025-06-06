package com.example.speedtest.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.ScrollResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.example.speedtest.entity.Title;
import com.example.speedtest.entity.TitleDocument;
import com.example.speedtest.repository.TitleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service // 스프링 서비스 빈 등록
@RequiredArgsConstructor
public class TitleSearchSpeedService {

    private final TitleRepository titleRepository;               // RDB 검색용
    private final ElasticsearchClient elasticsearchClient;       // Elasticsearch 검색용

    /**
     * ✅ RDB vs Elasticsearch 일반 검색 속도 비교
     * - RDB: LIKE 기반 검색
     * - ES: matchPhrasePrefix 쿼리 기반 검색
     * - 결과 수와 검색 시간(ms) 비교
     */
    public Map<String, Object> compareSearchSpeed(String keyword) {
        Map<String, Object> result = new HashMap<>();

        // RDB 검색 시간 측정
        long dbStart = System.currentTimeMillis();
        List<Title> dbResult = titleRepository.findByTitleContaining(keyword);
        long dbEnd = System.currentTimeMillis();
        result.put("dbTimeMs", dbEnd - dbStart);
        result.put("dbResultSize", dbResult.size());

        // Elasticsearch 검색 시간 측정
        long esStart = System.currentTimeMillis();
        List<TitleDocument> esResult;

        try {
            SearchResponse<TitleDocument> response = elasticsearchClient.search(s -> s
                            .index("title_autocomplete")  // 자동완성 인덱스에서 검색
                            .size(1000)
                            .query(q -> q
                                    .matchPhrasePrefix(mpp -> mpp
                                            .field("titleAutocomplete")
                                            .query(keyword)
                                    )
                            ),
                    TitleDocument.class
            );

            // 검색 결과 추출
            esResult = response.hits().hits().stream()
                    .map(hit -> hit.source())
                    .toList();

        } catch (IOException e) {
            throw new RuntimeException("Elasticsearch 검색 중 오류 발생", e);
        }

        long esEnd = System.currentTimeMillis();
        result.put("esTimeMs", esEnd - esStart);
        result.put("esResultSize", esResult.size());

        return result;
    }

    /**
     * ✅ Scroll API 기반 대용량 검색 성능 비교
     * - RDB: 단순 전체 LIKE 검색
     * - ES: Scroll API 사용하여 페이지 단위로 반복 조회
     */
    public Map<String, Object> compareSearchSpeedWithScroll(String keyword) {
        Map<String, Object> result = new HashMap<>();

        // RDB 검색
        long dbStart = System.currentTimeMillis();
        List<Title> dbResult = titleRepository.findByTitleContaining(keyword);
        long dbEnd = System.currentTimeMillis();
        result.put("dbTimeMs", dbEnd - dbStart);
        result.put("dbResultSize", dbResult.size());

        // Elasticsearch Scroll 검색
        long esStart = System.currentTimeMillis();
        try {
            List<TitleDocument> esResult = searchAllByKeyword(keyword);
            long esEnd = System.currentTimeMillis();
            result.put("esTimeMs", esEnd - esStart);
            result.put("esResultSize", esResult.size());
        } catch (IOException e) {
            result.put("esError", e.getMessage());
        }

        return result;
    }

    /**
     * ✅ Scroll API를 사용하여 대량 데이터 반복 검색
     * - 초기 검색 요청 이후 scrollId를 이용하여 다음 페이지 요청
     * - 결과가 없을 때까지 반복
     * - 마지막에 scrollId 정리
     */
    public List<TitleDocument> searchAllByKeyword(String keyword) throws IOException {
        final int batchSize = 10000;

        List<TitleDocument> allResults = new ArrayList<>();

        // 초기 검색 (scroll 시작)
        SearchResponse<TitleDocument> initialResponse = elasticsearchClient.search(s -> s
                        .index("title_autocomplete")
                        .scroll(t -> t.time("2m")) // scroll 보관 시간
                        .size(batchSize)
                        .query(q -> q
                                .matchPhrasePrefix(mpp -> mpp
                                        .field("titleAutocomplete")
                                        .query(keyword)
                                )
                        ),
                TitleDocument.class
        );

        String scrollId = initialResponse.scrollId();
        allResults.addAll(initialResponse.hits().hits().stream()
                .map(hit -> hit.source())
                .toList());

        // scrollId를 이용한 반복 요청
        while (true) {
            String finalScrollId = scrollId;
            ScrollResponse<TitleDocument> scrollResponse = elasticsearchClient.scroll(s -> s
                            .scrollId(finalScrollId)
                            .scroll(t -> t.time("2m")),
                    TitleDocument.class
            );

            if (scrollResponse.hits().hits().isEmpty()) break;

            allResults.addAll(scrollResponse.hits().hits().stream()
                    .map(hit -> hit.source())
                    .toList());

            scrollId = scrollResponse.scrollId();
        }

        // scrollId 정리
        String finalScrollId1 = scrollId;
        elasticsearchClient.clearScroll(cs -> cs.scrollId(finalScrollId1));
        return allResults;
    }
}
