package com.example.speedtest.config;

import com.example.speedtest.service.TitleIndexService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Configuration // 설정 클래스 선언
@RequiredArgsConstructor
public class TitleDataIndexer {

    private final TitleIndexService titleIndexService;

    /**
     * 스프링 애플리케이션이 완전히 실행된 후 실행되는 이벤트 리스너입니다.
     * - 모든 Title 데이터를 Elasticsearch에 색인하는 작업을 수행합니다.
     * - 최초 1회 실행
     */
    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        titleIndexService.indexAll(); // JPA에서 전체 Title 데이터를 읽어와 ES에 색인
        System.out.println("☆☆☆☆☆ Elasticsearch 제목 색인 완료 ☆☆☆☆☆");
    }
}
