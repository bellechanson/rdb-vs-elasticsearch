package com.example.speedtest.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity // JPA 엔티티로 매핑
@Getter // Lombok - 모든 필드의 Getter 자동 생성
@Setter // Lombok - 모든 필드의 Setter 자동 생성
@NoArgsConstructor // Lombok - 기본 생성자 자동 생성
@AllArgsConstructor // Lombok - 전체 필드 생성자 자동 생성
@Builder // Lombok - 빌더 패턴 자동 생성
public class Title {

    @Id // 기본 키(primary key)
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT 전략 사용 (MySQL 등)
    private Long tId;

    @Column(nullable = false) // null 허용하지 않음 (NOT NULL)
    private String title;
}
