package com.example.speedtest.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "title_autocomplete", createIndex = false)
// ✅ 이 클래스는 Elasticsearch 인덱스 "title_autocomplete"에 매핑되며, 인덱스는 외부에서 수동 생성
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
// ✅ Elasticsearch 결과에 정의되지 않은 필드가 있어도 무시하고 역직렬화
public class TitleDocument {

    @Id
    private String id;
    // 🔹 Elasticsearch의 고유 ID (String 타입, UUID 또는 tId를 문자열로 저장 가능)

    @Field(type = FieldType.Long)
    private Long tId;
    // 🔹 원본 RDB 데이터의 ID (Title 엔티티의 tId)

    private String title;
    // 🔹 일반 검색용 필드 (match 쿼리 등에서 사용)

    @Field(type = FieldType.Text, analyzer = "autocomplete_analyzer", searchAnalyzer = "standard")
    private String titleAutocomplete;
    // 🔹 자동완성용 필드
    // - 저장 시: nori + edge_ngram 기반 사용자 정의 analyzer 사용
    // - 검색 시: 일반 standard analyzer 사용
}
