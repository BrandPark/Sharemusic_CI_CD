# Sharemusic_CI_CD
Sharemusic은 제가 학교다니던 시절에 친구에게 매일 무슨 음악을 듣는지 추천받던 기억에서 시작한 개인 프로젝트입니다.
저와 같이 듣는 음악의 취향은 분명하게 있으나 원하는 음악의 정보를 알 수 있는 **통로**가 없는 사람들은 결국 음원 차트 순위에 올라와 있는 음악를 듣게됩니다.
Sharemusic 은 음악을 공유할 수 있는 **통로**의 역할을 할 수 있도록 만들었습니다.

## 프로젝트 시작 계기
회사에서 실무를 해보면서 내가 코드를 짤 때 굉장히 생각이 많다는 것을 새삼 느꼈다. 내 자신을 되돌아본 결과 **좋은 코드를 만들고 싶은 욕심에 비해 경험이 부족하여 무엇이 옳은지 판단을 못내리는 것이었다.** 고민 끝에 개인 프로젝트를 통해 하나의 서비스를 처음부터 만들어보면서 공부를 병행하면 부족한 경험과 판단 속도가 생길 것이라 생각하여 시작하게 되었다. 

Sharemusic은 사실 졸업작품을 만들 당시 내가 냈던 아이디어인데 역할 분담 시 웹 디자인을 맡게 되어서 백엔드 쪽을 많이 건드려보지 못했다. 그게 너무 아쉬워서 이번에는 같은 아이디어로 백엔드에 초점을 맞춰서 만들었다. 

## 프로젝트 목표
- 서비스 규모 확장성을 고려하여 **패키지 간의 의존성을 관리해 모듈화한다.**
- 무료 EC2는 하나만 생성가능하기 때문에 **API서버와 App 서버를 하나의 프로젝트에 만든다.** 대신 규모가 확장성을 고려하여 분리하여 설계한다.
- **도메인 간의 결합도를 최소화 한다.**
- Restful API를 만들어 Front-End Framework나 Android 개발에 열려있도록 한다.
- **최대한 객체지향적으로 코드를 짜며 유지보수성을 높인다.**
- Presentation Layer와 Application Layer를 구분하여 유지보수성을 높인다.
- APP과 DB의 네트워크 통신 횟수를 고려하여 성능 저하를 예방한다.
- **클라우드 서비스(AWS EC2, AWS RDS, S3, CodeDeploy)의 무료 credit을 최대한 활용**하여 서버를 구축한다.
- local 환경과 운영 환경, 테스트 환경을 구분한다.
- Travis-CI와 AWS S3, CodeDeploy를 사용하여 **CI/CD 파이프라인을 구축**한다.
- **테스트 코드를 작성**하여 코드의 안정성과 신뢰성을 높인다. 
- **개발 편이성을 위해 댓글의 길이 제한과 같은 것은 생략한다.**
- SSR이지만 ajax를 활용하여 CSR인 것 처럼 만든다.
- **유효성 검사**를 신경써서 코드를 작성한다.

<br>

## 프로젝트를 마치며...
부족한 지식을 찾아가며 해결하면서 문제 해결력과 침착함이 발전한 것 같다. 아쉬운 점은 뷰나 리액트를 공부해서 Rest API 통신을 하여 웹앱을 만들어보고 싶지만 처음 목표에서 벗어나는 것 같아 다음에 시간이 나면 발전 시키려고한다. 이것 말고도 기능적으로도 [last.fm api](https://www.last.fm/api/show/artist.search)를 통해 음악을 검색해서 검색 된 음악만 저장할 수 있게 하는 기능 등 시간이 날 때 마다 발전 시켜 볼 예정이다.

<br>

## 코딩 규칙
1. JPA와 Querydsl을 적극 활용하여 유지보수가 쉽게하고 기술적 한계는 JDBC를 활용한다. 적용 고려 순서는 다음과 같다.
    1. 메서드 이름으로 쿼리를 자동 생성하는 기능을 사용한다. ([공식문서 참고](https://docs.spring.io/spring-data/jpa/docs/2.4.15/reference/html/#jpa.query-methods.query-creation))
    2. @Query와 JPQL을 사용하여 직접 작성한다.
    3. querydsl을 사용하여 작성한다.
    4. JDBC를 사용한다.
    5. 모든 방법이 안된다면 Native sql을 사용한다.
1. Repository는 `QueryRepository`와 `JpaRepository 구현체`로 나눈다.
    - `QueryRepository`: 복잡한 API Response 스펙이나 PresentationLayer에서 사용되는 Form에 맞춰 데이터를 **조회**해 주는 Repository다. 주로 querydsl을 사용한다. PresentationLayer Form에 맞춘 QueryRepository는 수정 가능성이 높기 때문에 따로 두어 유지보수성을 높인다.
    - `JpaRepository 구현체`: 엔티티에 대한 전반적인 쿼리를 수행하는 Repository다. 복잡한 쿼리일 경우 사용자 정의 Repository([공식 문서 참고](https://docs.spring.io/spring-data/jpa/docs/2.4.15/reference/html/#repositories.custom-implementations))를 querydsl로 구현하여 확장 시킨다. 
1. 데이터의 수에 비례하여 쿼리를 보내는 수가 증가하는 것은 반드시 수정한다. (ex. JPA의 N+1 문제나 batch update, batch insert와 같은 것들)
1. Repository 단위 테스트와 통합 테스트는 반드시 하고 필요한 경우에 Service 단위 테스트를 한다.
3. `엔티티`들은 최대한 낮은 결합도와 순수한 상태를 유지한다.
4. `엔티티`는 비즈니스 객체로서 갖고 있는 상태값에 대한 비즈니스 로직을 직접 수행하게 한다.
5. `엔티티`는 PresentationLayer나 API Response로 직접적으로 사용하지 않고 Form Object나 Response DTO를 만들어서 사용한다.
6. DI는 테스트 코드를 제외하고 생성자 주입을 사용한다.
7. **테스트는 Factory 클래스를 만들어 사용하여 유지보수성을 높인다.**([마틴 파울러 블로그 참고](https://martinfowler.com/bliki/ObjectMother.html))
8. 테스트는 기본적으로 given-when-then 패턴을 따른다.
9. 실제 운영 서버(AWS EC2)에서 사용되는 api key와 같은 **secret key는 따로 모아서 `Github Private Repository`에 저장하고 CI script에서 로드하여 사용하도록 한다.**
10. `Dependency Cycle`이 생기지 않도록 한다. 특히 패키지 간의 사이클이 생기지 않도록 하여 모듈간의 결합도를 낮춘다.
11. Front-end 에서 사용 될 library는 npm을 사용하여 dependency를 package.json에 저장하고 gradle task로 빌드시 다운받도록 관리한다. ([참고](https://javanitto.tistory.com/34))

<br>

## Sharemusic 도메인 요구사항
- **회원**
  - 회원은 이메일 인증을 한 USER와 하지 않은 GUEST로 구분되며, USER만 음악 리스트(이하 앨범)를 만들 수 있다.
  - 회원은 서로 팔로우를 할 수 있다.
  - 로그인을 하면 자신이 팔로우한 사람과 자신이 업로드한 앨범이 홈 화면에 나열된다.
  - 로그인을 하지 않으면 모든 사람의 앨범이 홈 화면에 나열된다. (구경할 수 있도록 하기 위함)]
  - 회원의 비밀번호는 bcrypt 알고리즘을 통해 암호화 하여 저장된다.
  - 회원가입 시 기입한 이메일 또는 닉네임으로 로그인 가능하다.
  - 이메일 또는 닉네임이 같은 회원은 존재할 수 없다.
  - 프로필 이미지의 크기는 3MB로 제한한다.
- **앨범**
  - 앨범은 음악을 최대 5개 까지만 허용한다.
  - 음악은 name과 artist가 있으며 같은 음악은 앨범 내에 존재할 수 없다.
  - 앨범에는 여러개의 댓글이 달릴 수 있다.
  - 자신이 만든 앨범은 삭제할 수 있다.
  - 앨범을 삭제하면 복구가 불가능하다.
  - 앨범을 삭제하면 댓글도 삭제된다.
- **댓글**
  - 로그인한 경우만 댓글을 달 수 있다.
  - 자신이 단 댓글은 삭제할 수 있다. 
- **알림**
  - 알림의 종류는 3가지다.
    - 누군가 자신을 팔로우한 팔로우 알림
    - 자신이 팔로우한 사람이 앨범을 업로드한 경우
    - 자신이 만든 앨범에 댓글이 달릴 경우
  - 알림은 설정을 통해 on/off 할 수 있다. (기본 값은 on)
- **팔로우**
  - 팔로우를 한 경우 팔로우한 사람들의 앨범 업로드 소식을 접할 수 있다.
  - 팔로우를 한 경우 상대방에게 알림이 전송된다. (상대방이 알림이 켜져있을 때)
- **검색**
  - 검색 기능은 앨범 이름을 통해 앨범을 검색하거나 사용자의 닉네임 또는 이름을 통해 사용자를 검색할 수 있다.
  - 아무것도 입력하지 않고 검색하면 전체 검색을 한다.

<br>


## Domain Diagram


## Package Dependency Diagram


## 기술 스택

## Sharemusic API Docs
* [API Docs with Swagger](http://ec2-52-79-179-149.ap-northeast-2.compute.amazonaws.com:8080/swagger-ui.html)
