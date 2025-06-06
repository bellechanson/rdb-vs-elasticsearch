package com.example.speedtest.service;

import com.example.speedtest.entity.Title;
import com.example.speedtest.entity.TitleDocument;
import com.example.speedtest.repository.TitleRepository;
import com.example.speedtest.repository.TitleSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service // 스프링 서비스 빈으로 등록
@RequiredArgsConstructor
public class TitleIndexService {

    private final TitleRepository titleRepository;               // RDB (MySQL) 데이터 조회용
    private final TitleSearchRepository titleSearchRepository;   // Elasticsearch 색인용

    private static final int BATCH_SIZE = 1000; // 한 번에 색인할 문서 수 (성능 최적화)

    /**
     * 🔄 전체 Title 데이터를 Elasticsearch에 색인하는 메서드
     * - RDB에서 모든 데이터를 가져온 후
     * - TitleDocument로 변환하여
     * - 지정된 배치 크기 단위로 저장
     */
    public void indexAll() {
        // 1. RDB에서 전체 Title 데이터 조회 (ID가 null인 항목은 제외)
        List<Title> titles = titleRepository.findAll().stream()
                .filter(title -> title.getTId() != null)
                .toList();

        System.out.println("💡 총 색인 대상 제목 수: " + titles.size());

        List<TitleDocument> batch = new ArrayList<>();
        int count = 0;

        // 2. 각 Title을 TitleDocument로 변환 후 배치에 추가
        for (Title title : titles) {
            TitleDocument doc = TitleDocument.builder()
                    .id(title.getTId().toString())        // Elasticsearch 고유 ID (String)
                    .tId(title.getTId())                  // RDB ID
                    .title(title.getTitle())              // 일반 검색 필드
                    .titleAutocomplete(title.getTitle())  // 자동완성 전용 필드
                    .build();

            batch.add(doc);
            count++;

            // 3. 배치 크기만큼 모이면 Elasticsearch에 저장 후 리스트 초기화
            if (batch.size() >= BATCH_SIZE) {
                titleSearchRepository.saveAll(batch);
                System.out.println("✅ 저장 완료: " + count + "개");
                batch.clear();
            }
        }

        // 4. 남은 데이터 저장
        if (!batch.isEmpty()) {
            titleSearchRepository.saveAll(batch);
            System.out.println("✅ 최종 배치 저장 완료: " + count + "개");
        }

        System.out.println("🎉 전체 색인 완료!");
    }
}
