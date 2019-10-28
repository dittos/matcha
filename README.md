# Matcha

MSA 연습 프로젝트. 왓챠와 유사한 사이트를 만들어볼 것이다. 구조는 진행하면서 계속 바뀔 수 있다.

## 구조

### `media-app`

작품 정보를 제공한다.

### `collection-app`

사용자의 작품 컬렉션을 관리한다.

### `review-app`

사용자가 공유한 리뷰를 인덱싱한다.

### `gateway-app`

프론트엔드를 위해 여러 백엔드 서비스를 호출해서 처리하기 쉽게 가공해준다.

### `frontend`

프론트엔드

## 실행 환경 초기화

1. `cd docker; docker-compose up`
2. `docker exec -it docker_mysql_1 mysql -uroot -ppw`
    1. `create database matcha_collection;`
3. `curl localhost:9200/matcha_collection_item -XPUT -d @review-app\src\main\resources\mapping_item.json -H "content-type:application/json"`
