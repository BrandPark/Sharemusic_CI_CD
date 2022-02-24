# Sharemusic_CI_CD
Sharemusic은 제가 학교다니던 시절에 친구에게 매일 무슨 음악을 듣는지 추천받던 기억에서 시작한 개인 프로젝트입니다.

나만의 음악 정보를 서로 공유하며 팔로우를 통해 빠르게 소식을 접할 수도 있습니다.

**접속 링크 : http://ec2-52-79-179-149.ap-northeast-2.compute.amazonaws.com:8080/**

## 로컬에서 실행해 보기

1. 프로젝트를 Clone 한다.
2. IDE로 열고 profile은 `local`로 하여 실행하면 http://localhost:8080 로 접속가능합니다.
3. h2 embeded로 실행되며 db 파일은 홈 디렉터리에 생성됩니다.

```
    < 참고 >
    - 로컬 환경에서 회원가입 시 인증 토큰이 메일로 전송되지 않고 콘솔에 출력됩니다. 
        - < Send Mail : verify link = "/verify-email?token=TOKEN&email=EMAIL" > 과 같은 형식으로 출력됩니다. 
        - 해당 텍스트를 URL의 path로 붙여넣으면 회원 인증이 성공합니다.
```

## 프로젝트 시작 계기

코드를 짤 때 어떻게 짜는 게 좋을 까라는 생각이 앞서다 보니 손이 느려지는 경향이 있었습니다. 

**좋은 코드를 만들고 싶은 욕심에 비해 경험이 부족하여 무엇이 옳은지 판단을 못내리기 때문**에 딜레이가 생기는 것이라고 생각하여

하나의 서비스를 처음부터 만들어보면서 고민했던 것들을 실제로 적용해 보면 저절로 빨라질 것이라고 생각하여 시작했습니다. 

Sharemusic은 사실 졸업작품을 만들 당시 제가 냈던 아이디어인데 역할 분담 시 백엔드를 맡았다가 가장 중요하게 생각한 웹 디자인 쪽이 부족하여 역할을 변경했습니다.

백엔드 코드를 끝까지 담당하지 못한게 너무 아쉬워서 같은 아이디어로 백엔드에 초점을 맞춰 시작한 것이 이 프로젝트입니다.

<br>

## 프로젝트 목표

이 프로젝트는 기능은 간단히 하고 설계와 실제 운영 서비스 처럼 CI/CD 파이프 라인을 구축하여 배포 자동화를 하여 운영하는 것을 목표로하였습니다.

테스트 코드로 코드의 신뢰성을 높이고 그것을 기반으로 리팩토링하여 코드를 진화시키는 방식으로 코딩했습니다.

- 의존성을 관리하여 유연하고 확장 가능한 코드를 개발한다.
    - 필요에 따라 Aggregate를 나누어 결합도를 관리한다.
- 유지보수성이 높은 코드를 작성한다.
- 테스트 코드를 작성하고 리팩토링을 통해 코드를 진화시킨다.
- 운영 서비스에 빌드, 테스트, 배포를 자동화하는 CI/CD 파이프 라인을 구축한다.
- Validation 로직을 통해 유효성 검사를 실시하고 테스트 코드에 담는다.
- SPA 클라이언트처럼 동작되게 한다.
- Profile을 통해 Local, Test, 운영 환경을 분리한다.
- DB 호출 횟수를 신경써서 관리한다.

<br>

## 프로젝트를 마치며...

프로젝트를 하면서 자신감과 스스로 문제를 해결하는 능력이 많이 발전한 것 같습니다.

API와 JWT 필터를 만들었지만 사용하지 못해서 좀 아쉽습니다. 프로토타입으로 간단하게 프로젝트를 만들어 적용해 볼 예정입니다.

<br>

---

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

## ERD

> 인덱스는 읽기 작업이 더 많을 것 같은 ACCOUNT만 생성했습니다.

- ErdCloud : https://www.erdcloud.com/d/w8QX2hy9kKpdNwLhv

<br>

## 객체 다이어그램

> 서비스의 분리 가능성이 있는 **Notification**과 **Album**을 Aggregate로 분리 했습니다. 

![image](https://user-images.githubusercontent.com/53790137/154823666-53b9fa8c-a64e-4d42-90f1-0985d115c76d.png)

- **Album & Track**
  - 앨범과 트랙은 **NoSQL**을 따로 적용하면 좋을 것 같아서 Aggregate를 분리하여 의존성을 최소화 했습니다. 현재는 RDBMS입니다.
  - 음악 정보(Track)는 앨범과 life cycle이 일치하기 때문에 같은 Aggregate로 묶고 Aggregate Root는 앨범으로 했습니다.

- **Comment**
  - Comment는 Album과 Life Cycle이 다르기도 하고 멀티 쓰레드 환경인 Spring Boot에서 **Race Condition** 문제가 발생할 수 있기 Aggregate를 분리했습니다. 

- **Notification**
  - 전체 기능 중 가장 시간이 오래 걸릴 수 있는 작업이고 다른 기능에 부가적으로 작동하는 관심사가 다른 기능입니다. 
  - 그래서 분리가 가능하도록 Aggregate를 나누고 이벤트 핸들러에 의해 비동기적으로 처리되도록 구현했습니다.

- **Account & Follow**
  - Follow와 Account는 Album과 Notification과 같이 분리의 필요성을 느끼지 못하여 Aggregate를 나누지 않고 그대로 객체를 참조하도록 했습니다. 


일괄적인 알림 전송을 구현할 때 테이블의 id 채번 전략을 Auto increment로 해서 **JPA로는 Batch성 쿼리가 불가능**했습니다.

그래서 `JdbcTemplate.batchUpdate()`를 사용하여 직접 배치성 쿼리를 전송하도록 했습니다.

<br>

## 패키지 다이어그램

![image](https://user-images.githubusercontent.com/53790137/150625586-9d0a1865-abd0-438d-ad32-92315a43110e.png)

- **infra**: 프로젝트의 설정, 외부 서비스와 연동, 세션과 관련된 패키지입니다. Spring, Java와의 의존성만 존재합니다.
- **modules** : 애플리케이션의 도메인 별로 패키지가 나뉩니다.
  - **domain**: Entity와 Repository가 들어있습니다. 비즈니스 로직은 Entity가 가지고 있습니다.
  - **dto**: api, partials 패키지와의 의존성 사이클을 없애고 Service 메서드의 재사용성을 늘리기 위해 만들었습니다. api, partials는 요청 데이터를 modules.dto 로 변환하여 modules.service를 호출합니다. 
  - **form**: View의 Form 형식으로 들어오는 데이터를 받는 객체들이 있습니다.
  - **service**: Service는 트랜잭션을 제공하는 계층으로 비즈니스 로직들의 순서를 제어합니다.
  - **validator**: 사용자의 입력 데이터(Form객체)의 유효성과 페이지 권한을 체크하는 역할을 합니다. 유효성 체크라는 별개의 관심사를 한 곳에 모아 응집도를 높였습니다.
- **partials**: 마치 SPA 클라이언트를 사용하는 것처럼 보이도록 하고 싶어서 만들었습니다. 
  - Server-side로 렌더링된 html문자열을 ajax로 받아 그대로 출력하도록 하였습니다.
  - API로 데이터를 받아 javascript로 DOM을 동적으로 만드는 것은 목표에서 벗어나고 단순 노동이 될 것 같아서 편의상 만들었습니다.
- **api**: 웹앱의 기능을 API로 만들었습니다. 
  - Vue나 모바일 앱같은 클라이언트로도 구현할 수 있도록 하기 위해 만들었습니다.
  - 테스트 코드만 작성되어 있으며 현재는 사용되고 있지 않습니다.

<br>

## 프로젝트 구조

<img src="https://user-images.githubusercontent.com/53790137/154825578-f80fe8e8-68e7-4334-a678-cc95850d2e5c.png" width=600 height=450>

- CI/CD 파이프 라인을 구축하여 클라우드 네이티브하게 운영이 가능하도록 했습니다.
- Front-end libraries도 CI시 자동으로 다운로드하여 빌드하도록 했습니다.

<br>

## 기술 스택

- Back-End
  - Spring Boot
  - Spring Security
  - Spring Data JPA
  - Querydsl
  - MySQL 8.0
  - JUnit
  - Gradle
  - NPM
  - SendGrid(SMTP)
- Front-End
  - Thymeleaf
  - javascript
  - 3rd libraries
    - bootstrap(css, js)
    - jquery
    - filepond(프로필 이미지)
    - mark.js(하이라이트)
    - moment(시간 표시)
- Infra
  - AWS EC2
  - AWS RDS
  - AWS CodeDeploy
  - AWS S3
  - Travis-ci
  - Git Hub

---

## JPA & Querydsl 사용 규칙

JPA와 Querydsl로 구현한 CRUD는 테스트 코드를 작성해 SQL과 기능을 확인 후 적용했습니다. 

특히 N+1문제가 발생하지 않도록 주의했습니다.

**Repository 종류는 3가지로 구분**했습니다. Album Entity에 대해 예를 들면 다음과 같습니다.

- **AlbumRepository**
  - 메서드 이름으로 Spring이 추론하여 쿼리를 생성할 수 있는 경우와 간단한 JPQL로 해결 가능할 경우에 사용합니다. 
- **ExtendAlbumRepository & ExtendAlbumRepositoryImpl**
  - JPA와 JPQL로 간단히 만들 수 없는 쿼리의 경우 만들어서 사용합니다. 
  - 내부적으로 Querydsl을 사용합니다.
- **AlbumQueryRepository**
  - API에서 사용되는 조회에 특화된 Repository입니다. API 스펙에 종속적인 코드를 한 곳에 모아 유지보수성을 높이고자 했습니다.
  - API의 응답 스펙에 맞춰 데이터를 DTO에 담아 반환합니다.

<br>

## 코딩 규칙
- 직접 작성한 쿼리는 반드시 테스트를 통해 **기능과 실행되는 SQL을 확인하고 사용**한다.
- **Entity는 별도의 static 생성 메서드를 만들어 사용**하여 유지보수성을 높인다. (builder는 테스트 코드에서만 사용)
- 비즈니스 로직은 엔티티에 두고 Service 계층은 트랜잭션과 도메인 로직의 순서를 제어하는 역할을 한다.
- **테스트 코드는 Factory 클래스를 만들어 사용**하여 작성 시간을 줄이고 유지보수성을 높인다.([마틴 파울러 블로그 참고](https://martinfowler.com/bliki/ObjectMother.html))
- **DI는 생성자 주입**을 사용한다.
- **기능을 분리할 가능성이 있다면 Aggregate를 나눠** 참조를 끊고 분리한다. 
- **DB호출 횟수를 주의하고 N+1 문제가 발생하지 않도록 체크**한다.
- API Key와 같이 **감춰야 하는 설정들은 Private Repository에 보관하고 CI 시 로드**하여 사용하도록 한다.
- JPA의 ddl-auto 옵션과 데이터 저장소가 다르기 때문에 Profile을 통해 **Local, Test, 운영 환경을 분리**한다.
    - **local** : local
    - **Test** : test
    - **운영** : real, realdb, secretkey
- Front-end에서 사용되는 **3rd party libraries**은 CDN이나 직접 넣지 않고 **Gradle과 NPM을 통해 CI 시 다운로드**하여 빌드하도록 한다.

<br>

## 프로젝트 동안 해결한 문제
- [설계를 어떻게 해야할까?](https://javanitto.tistory.com/41)
- JPA에서 PK 채번 전략을 AUTO_INCREMENT로 하였을 때 Batch성 쿼리가 동작하지 않는 문제
- [Gmail을 SMTP 서버로 구현하던 중 만난 에러](https://javanitto.tistory.com/32) (문제는 해결했지만 SendGrid를 사용했다.)
- [로그인 기억하기 기능 구현(Remember Me)](https://javanitto.tistory.com/35)
- [NPM으로 프론트엔드 라이브러리 빌드 시 자동 다운로드하기](https://javanitto.tistory.com/34)
- [데이터베이스에 세션 저장하기](https://javanitto.tistory.com/22)
- MySQL을 사용하면서 테이블 네이밍 대문자로 변환하기 (SpringPhysicalNamingStrategy)
- [Travis-CI로 S3에 배포하기](https://docs.travis-ci.com/user/deployment/s3/)
- [CodeDeploy 로 EC2에 배포 설정하기](https://jojoldu.tistory.com/281)
- [테스트 코드 쉽게 작성하기](https://martinfowler.com/bliki/ObjectMother.html)

가장 어려웠던 문제는 CI/CD 파이프 라인을 구축하는 작업이었습니다. 
감춰야 할 설정파일들을 private repository에 따로 저장두어서 빌드할 때 가져와야 했는데 travis CI 서버 내부의 디렉터리 구조를 모르다 보니 헤맸습니다. 
급할수록 돌아가라고 documentation을 뒤져서 해결했습니다.

시간이 가장 많이 걸렸던 작업은 화면개발이었습니다. 욕심이 많아 디자인도 못나게 하고 싶지는 않았는데 어떻게 해도 못나보여서 몇번을 수정한 것 같습니다. 

테스트 코드 작성도 시간이 많이 걸렸습니다. 클래스를 수정할 때 마다 작성해 두었던 테스트 코드들도 굉장히 많은 영향을 받았습니다.
마틴 파울러의 [Object Mother](https://martinfowler.com/bliki/ObjectMother.html) 글을 보고 단순 반복되는 작업을 따로 util화 시켜서 작성했더니 유지보수성이 높아졌습니다.

<br>

---

# 캡쳐 화면

## 1. 메인 화면

> 로그인하지 않을 경우 모든 컬렉션을 구경할 수 있습니다. 로그인을 하면 팔로우한 사람의 게시물만 메인화면에 표시됩니다. 마우스를 올리면 컬렉션 안의 음악을 볼 수 있습니다.

<img src="https://user-images.githubusercontent.com/53790137/154633980-e6c35b5f-5ff9-40f9-b8e2-9878c2f8fa45.png" width=650 height=500>

## 2. 회원 인증

> 회원가입 때 등록한 이메일을 인증해야 앨범을 등록할 수 있습니다.

<img src="https://user-images.githubusercontent.com/53790137/154798027-c1ca113a-133c-459c-9b08-9e46e0ed6df1.png" width=650 height=150>

아래와 같은 인증 메일을 받고 버튼을 누르면 인증이 완료됩니다.

<img src="https://user-images.githubusercontent.com/53790137/154623549-386dcd27-002b-4dfd-861c-d9e7ccfe3277.png" width=400 height=200 style="border:1px solid black">

## 3. 회원 정보 변경

>  프로필 사진이나 기본 정보, 비밀 번호를 변경 할 수 있습니다.

<img src="https://user-images.githubusercontent.com/53790137/154634666-4172b201-d1c6-499a-8993-aea5960bd404.png" width=600 height=400>


## 4. 프로필 페이지

> 자신 또는 상대의 프로필 페이지에 들어가 업로드한 앨범들을 구경할 수 있고 팔로우 또한 가능합니다. 상대방의 팔로워와 팔로잉 또한 볼 수 있습니다.

<img src="https://user-images.githubusercontent.com/53790137/154634465-4fd32465-b50a-4403-928c-c38cf4719ae4.png" width=600 height=400>

## 5. 앨범 등록

> 공유하고 싶은 음악 정보와 이미지를 넣어 만들 수 있습니다. 자신의 앨범이라면 수정, 삭제 또한 가능합니다.

<img src="https://user-images.githubusercontent.com/53790137/154633503-24a5163e-b279-46cb-8068-eaaca399dff9.png" width=600 height=400>

## 6. 앨범 상세 & 댓글

> 앨범을 클릭하여 상세 화면을 볼 수 있습니다. 로그인을 했다면 댓글도 달 수 있습니다.

<img src="https://user-images.githubusercontent.com/53790137/154635505-f3dc308d-5207-4878-be2c-49162339cf95.png" width=600 height=400>

## 7. 검색

> 앨범 제목이나 회원 닉네임을 검색할 수 있습니다. 검색 단어는 강조되어 표시됩니다.

<img src="https://user-images.githubusercontent.com/53790137/154634106-2293c726-82a4-4420-bd79-ea71329acc6c.png" width=600 height=400>


## 8. 알림

> 알림을 받을 수 있습니다. 설정을 통해 제한할 수 있습니다.

<img src="https://user-images.githubusercontent.com/53790137/154634810-cf18a400-9775-4185-8ae0-fcef206bc459.png" width=400 height=400>

'더 많은 알림 보기'를 클릭하면 아래와 같이 전체 알림을 볼 수 있습니다. 모두 읽기 기능도 가능합니다.


<img src="https://user-images.githubusercontent.com/53790137/154635037-e700b28d-edc3-41f2-b5af-bd2d6ad32aef.png" width=650 height=450>


> 개인 설정을 통해 알림을 제한할 수 있습니다.

![image](https://user-images.githubusercontent.com/53790137/154635152-ffae6d9c-294b-4a31-9381-f7df02741abe.png)

