package com.example.speedtest.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // 스프링이 설정 클래스로 인식하게 함
public class ElasticsearchConfig {

    /**
     * Elasticsearch Java Client를 생성하여 Bean으로 등록합니다.
     * - localhost:9200에 접속
     * - JSON 매핑은 Jackson 기반으로 처리
     */
    @Bean
    public ElasticsearchClient elasticsearchClient() {
        // Elasticsearch HTTP 통신을 위한 RestClient 생성 (기본 포트: 9200)
        RestClient restClient = RestClient.builder(
                new HttpHost("localhost", 9200)
        ).build();

        // JSON 직렬화/역직렬화를 위한 Transport 설정 (Jackson 기반)
        RestClientTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper());

        // ElasticsearchClient 생성 (Elasticsearch Java API Client)
        return new ElasticsearchClient(transport);
    }
}
