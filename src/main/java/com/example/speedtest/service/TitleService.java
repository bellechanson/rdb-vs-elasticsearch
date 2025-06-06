package com.example.speedtest.service;

import com.example.speedtest.entity.Title;
import com.example.speedtest.repository.TitleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service // 스프링 서비스 컴포넌트로 등록
@RequiredArgsConstructor
public class TitleService {

    private final TitleRepository titleRepository;

    /**
     * 🔍 제목 키워드 포함 검색 (RDB)
     * - JPA의 LIKE 쿼리 기반
     * - SQL: SELECT * FROM title WHERE title LIKE %keyword%
     *
     * @param keyword 검색어
     * @return 키워드를 포함하는 Title 리스트
     */
    public List<Title> searchByKeyword(String keyword) {
        return titleRepository.findByTitleContaining(keyword);
    }

    /**
     * 📦 전체 Title 데이터 조회
     * - 색인용, 테스트용 등으로 사용
     *
     * @return 모든 Title 리스트
     */
    public List<Title> getAll() {
        return titleRepository.findAll();
    }

    /**
     * 🛠 더미 데이터를 일괄 저장
     * - Faker 등으로 생성된 데이터를 저장할 때 사용
     *
     * @param titles 저장할 Title 리스트
     */
    public void saveAll(List<Title> titles) {
        titleRepository.saveAll(titles);
    }
}
