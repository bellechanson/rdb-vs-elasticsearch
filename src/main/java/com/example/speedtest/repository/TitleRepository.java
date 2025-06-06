package com.example.speedtest.repository;

import com.example.speedtest.entity.Title;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TitleRepository extends JpaRepository<Title, Long> {

    /**
     * ✅ 키워드를 포함하는 제목을 검색 (RDB)
     * - SQL: SELECT * FROM title WHERE title LIKE %:keyword%
     * - JPA 메서드 명명 규칙 기반 자동 쿼리 생성
     *
     * @param keyword 검색 키워드
     * @return 제목에 키워드를 포함하는 Title 엔티티 리스트
     */
    List<Title> findByTitleContaining(String keyword);
}
