package com.example.speedtest.repository;

import com.example.speedtest.entity.TitleDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * ✅ Elasticsearch 기반 자동완성/검색 리포지토리
 * - TitleDocument 인덱스에 대한 CRUD 및 커스텀 쿼리 정의 가능
 * - 기본적으로 save, findById, delete 등 제공
 * - 필요 시 @Query 애너테이션 또는 커스텀 구현체로 확장 가능
 */
public interface TitleSearchRepository extends ElasticsearchRepository<TitleDocument, String> {

    // 현재는 기본 메서드만 사용 중
    // 예: save(), findById(), findAll() 등
}
