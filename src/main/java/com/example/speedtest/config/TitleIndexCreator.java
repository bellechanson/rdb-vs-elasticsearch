package com.example.speedtest.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

@Component // 애플리케이션 시작 시 자동 실행되는 컴포넌트
@RequiredArgsConstructor
public class TitleIndexCreator implements CommandLineRunner {

    private final ElasticsearchClient esClient;

    /**
     * 애플리케이션 실행 시 실행되는 메서드입니다.
     * 자동완성 전용 인덱스(titles_autocomplete)를 생성합니다.
     */
    @Override
    public void run(String... args) {
        System.out.println("📌 [Elasticsearch] 제목 자동완성 인덱스 초기화 시작");

        try {
            // 인덱스가 없으면 JSON 설정 파일을 기반으로 새로 생성
            createIndexIfNotExists("titles_autocomplete", "es/titles_autocomplete.settings.json");
        } catch (Exception e) {
            System.err.println("❌ [Elasticsearch] 인덱스 생성 실패: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("✅ [Elasticsearch] 제목 자동완성 인덱스 초기화 완료");
    }

    /**
     * 지정한 이름의 인덱스가 존재하지 않을 경우, 지정한 JSON 설정을 사용해 새 인덱스를 생성합니다.
     *
     * @param indexName          생성할 인덱스 이름
     * @param classpathLocation  설정 JSON 파일 경로 (resources 기준)
     */
    private void createIndexIfNotExists(String indexName, String classpathLocation) throws IOException {
        // 인덱스 존재 여부 확인
        boolean exists = esClient.indices().exists(e -> e.index(indexName)).value();

        if (!exists) {
            // 클래스패스에서 설정 파일 로드
            try (InputStream inputStream = new ClassPathResource(classpathLocation).getInputStream()) {
                String json = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

                // 인덱스 생성 요청 수행
                esClient.indices().create(c -> c
                        .index(indexName)
                        .withJson(new StringReader(json))
                );

                System.out.println("✅ [Elasticsearch] 인덱스 생성됨: " + indexName);
            }
        } else {
            System.out.println("ℹ️ [Elasticsearch] 이미 존재하는 인덱스: " + indexName);
        }
    }
}
