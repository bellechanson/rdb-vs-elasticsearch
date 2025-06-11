# 🔍 검색 속도 비교 프로젝트 : RDBMS vs Elasticsearch

본 프로젝트는 **Spring Boot 기반 웹 애플리케이션**에서 **RDBMS(JPA)와 Elasticsearch 간의 검색 성능을 비교**하고, 자동완성 기능의 
실효성을 검증하기 위한 실험 프로젝트입니다.

---

## 🛠 기술 스택

- **Backend**: Spring Boot 3.5.0, JPA, Hibernate
- **Database**: MySQL 8.0 (Docker), Elasticsearch 8.13.2
- **Search Engine**: Elasticsearch (Java Client, Scroll API)
- **Tools**: Postman, Docker, Faker (데이터 생성)

---

## 🗂 프로젝트 구조

```
📦speedtest
 ┣ 📂config                  # 초기 더미 데이터 및 인덱스 설정
 ┣ 📂controller              # API 엔드포인트
 ┣ 📂entity                  # Title, TitleDocument 엔티티
 ┣ 📂repository              # JPA, ES 리포지토리
 ┣ 📂service                 # 검색 및 비교 로직
 ┣ 📂resources
 ┃ ┣ 📂es
 ┃ ┃ ┗ 📜 titles_autocomplete.settings.json  # 커스텀 분석기 설정
 ┃ ┗ 📜 application.properties
```


---

## ⚙ Elasticsearch 초기 세팅 가이드 (★Windows PowerShell 기준★)

> 📌 Mac/Linux 환경 또는 도커 GUI 사용 시 명령어와 동작 방식이 일부 다를 수 있습니다.

테스트 실행 전에 Elasticsearch 환경을 초기화하고 Nori 플러그인을 설치해야 검색 기능이 정상 작동합니다.

### 1. 기존 Elasticsearch 컨테이너 삭제
```powershell
docker rm -f elasticsearch
```
### 2. Elasticsearch 8.13.2 새로 설치
```
docker run -d --name elasticsearch `
  -p 9200:9200 `
  -e "discovery.type=single-node" `
  -e "xpack.security.enabled=false" `
  -e "ES_JAVA_OPTS=-Xms1g -Xmx1g" `
  docker.elastic.co/elasticsearch/elasticsearch:8.13.2
```
### 3. Nori 형태소 분석기 플러그인 설치
```
docker exec -it elasticsearch bin/elasticsearch-plugin install analysis-nori
```
### 4. 플러그인 적용을 위한 컨테이너 재시작
```
docker restart elasticsearch
```


---

## 📊 테스트 목적

- 단어 자동완성을 포함한 **Elasticsearch matchPhrasePrefix 기반 검색 속도**가 RDBMS의 `LIKE` 기반 검색보다 얼마나 유리한지 비교
- 데이터 양에 따른 성능 변화 측정 (2만 / 3만 / 5만 / 10만 / 30만건 기준)

---

## ⚙ 인덱스 설정 (Edge Ngram + Nori)

```jsonc
"title": {
  "type": "text",
  "analyzer": "nori_analyzer"
},
"titleAutocomplete": {
  "type": "text",
  "analyzer": "autocomplete_analyzer",
  "search_analyzer": "standard"
}
```

사용된 분석기:
- `nori_analyzer`: 한글 형태소 분석
- `autocomplete_analyzer`: edge_ngram 기반 자동완성

---

## 🧪 실험 방식

### ✅ Endpoint
- 일반 검색: `/api/test/title-speed/rdb-vs-es?keyword=스프링`
- Scroll 검색: `/api/test/title-speed/rdb-vs-es-scroll?keyword=스프링`

### ✅ 측정 항목
- `dbTimeMs`: RDBMS 검색 소요 시간 (ms)
- `esTimeMs`: Elasticsearch 검색 소요 시간 (ms)
- 결과 개수 비교: `dbResultSize`, `esResultSize`

---

## 📈 테스트 결과 요약

| 데이터 수 | RDB 검색속도   | ES 검색속도 (Scroll) | 비고 |
|-----------|------------|------------------|------|
| 2만       | ~94ms      | ~150ms           | 거의 유사 |
| 3만       | ~110ms     | ~213ms           | ES 조금 느림 |
| 5만       | ~180ms     | ~363ms           | RDB 우세 |
| 10만      | ~584ms     | ~1188ms          | RDB 우세 |
| **30만**  | **1605ms** | **3863ms**       | RDB가 2배 이상 빠름 |

---

![속도비교](https://github.com/user-attachments/assets/bbb93752-6895-41cb-a89d-78278dd6f424)




## 📌 결론 및 인사이트

- 단순 키워드 검색 시에는 RDBMS가 더 빠르게 작동할 수 있음
- 하지만 **Elasticsearch는**:
    - `edge_ngram` 기반 자동완성
    - `Scroll API`를 통한 대용량 처리
    - `한글 형태소 분석기`(nori) 적용
    - 복합 쿼리, 사용자 맞춤 분석이 가능하다는 **확장성**이 있음

### 💡 이력서/면접 어필 문구 예시

```
Elasticsearch의 edge_ngram 기반 자동완성과 RDBMS의 LIKE 검색을 비교 실험하여
데이터 규모와 쿼리 목적에 따른 검색 기술 선택 전략을 수립하였습니다.
단순한 성능 비교를 넘어, 특정 검색 조건(예: 자동완성, 복합 검색, 한글 형태소 분석 등)에 
따라 Elasticsearch의 강점이 어디서 발휘되는지를 수치 기반으로 설명할 수 있습니다.

```

---

## 🗓 테스트 일자

- 2025-06-05 기준 실험 결과

---

## 🧑‍💻 개발자

- minseop (Bellechanson)
