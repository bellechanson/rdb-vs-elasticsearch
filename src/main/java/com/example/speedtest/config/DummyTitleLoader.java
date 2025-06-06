package com.example.speedtest.config;

import com.example.speedtest.entity.Title;
import com.example.speedtest.repository.TitleRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component // 스프링 부트 시작 시 자동으로 빈으로 등록되는 컴포넌트
@RequiredArgsConstructor
public class DummyTitleLoader {

    private final TitleRepository titleRepository;
    private final Random random = new Random();

    // 전체 더미 데이터 개수
    private static final int TOTAL_COUNT = 300000;

    // 한 번에 DB에 저장할 배치 크기
    private static final int BATCH_SIZE = 200;

    // 더미 데이터로 사용할 제목 목록
    private final String[] 한글제목목록 = {
            "스프링 입문 강좌",
            "스프링 부트 실전 가이드",
            "스프링 MVC 패턴 이해",
            "스프링 시큐리티 적용법",
            "스프링 핵심 개념",
            "스프링 게시판 만들기",
            "스프링 REST API 설계",
            "스프링 데이터 활용법",
            "스프링 JPA 실습",
            "스프링 테스트 코드 작성"
    };

    /**
     * 애플리케이션 시작 시 한 번 실행되며,
     * Title 데이터가 없을 경우 30만 건의 더미 데이터를 생성하여 저장합니다.
     */
    @PostConstruct
    @Transactional
    public void initDummyTitles() {
        if (titleRepository.count() > 0) {
            log("📌 Title 데이터가 이미 존재합니다.");
            return;
        }

        List<Title> titles = new ArrayList<>();

        for (int i = 0; i < TOTAL_COUNT; i++) {
            // 랜덤으로 제목을 선택하고 " 테스트"를 붙임
            String titleText = 한글제목목록[random.nextInt(한글제목목록.length)] + " 테스트";

            // Title 엔티티 생성
            Title titleEntity = Title.builder()
                    .title(titleText)
                    .build();

            titles.add(titleEntity);

            // 배치 단위로 저장
            if (titles.size() % BATCH_SIZE == 0) {
                titleRepository.saveAll(titles); // 배치 저장
                log("✅ Title " + BATCH_SIZE + "개 저장 완료");
                titles.clear(); // 리스트 초기화
            }
        }

        // 남은 데이터가 있다면 한 번 더 저장
        if (!titles.isEmpty()) {
            titleRepository.saveAll(titles);
        }

        log("🎉 총 " + TOTAL_COUNT + "개 Title 더미 데이터 생성 완료");
    }

    // 콘솔에 로그 출력
    private void log(String message) {
        System.out.println(message);
    }
}
